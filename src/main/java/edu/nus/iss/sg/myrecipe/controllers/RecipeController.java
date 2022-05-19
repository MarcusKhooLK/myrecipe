package edu.nus.iss.sg.myrecipe.controllers;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import edu.nus.iss.sg.myrecipe.models.Recipe;
import edu.nus.iss.sg.myrecipe.services.AmazonS3Service;
import edu.nus.iss.sg.myrecipe.services.RecipeService;
import edu.nus.iss.sg.myrecipe.services.SearchService;
import edu.nus.iss.sg.myrecipe.utils.ConversionUtils;

@Controller
@RequestMapping(path = "/account/recipe")
public class RecipeController {

    @Autowired
    private RecipeService recipeSvc;

    @Autowired
    private AmazonS3Service s3Svc;

    @Autowired
    private SearchService searchSvc;

    @GetMapping(path = "/create")
    public ModelAndView showCreateRecipe(HttpSession session) {
        String username = (String) session.getAttribute("name");
        ModelAndView mav = new ModelAndView();
        mav.addObject("userLoggedIn", username);

        List<String> areas = searchSvc.getAllAreas();
        List<String> categories = searchSvc.getAllCategories();

        mav.addObject("recipeCategories", categories);
        mav.addObject("recipeAreas", areas);
        mav.setViewName("create_recipe");
        mav.setStatus(HttpStatus.OK);

        return mav;
    }

    @PostMapping(path = "/create")
    public ModelAndView postCreateRecipe(@RequestParam MultiValueMap<String, String> form,
            @RequestParam MultipartFile recipeThumbnail,
            HttpSession session) {
        String username = (String) session.getAttribute("name");
        ModelAndView mav = new ModelAndView();

        String thumbnailId = s3Svc.upload(recipeThumbnail, username);

        Recipe r = ConversionUtils.convert(form);
        r.setCreatedBy(username);
        r.setThumbnail(thumbnailId);

        try {
            recipeSvc.createRecipe(r, username);
            mav.setViewName("create_recipe_success");
            mav.setStatus(HttpStatus.OK);
        } catch (Exception ex) {
            s3Svc.delete(thumbnailId);
            mav.setViewName("error");
            mav.addObject("errorMsg", "Something went wrong when creating recipe");
            mav.setStatus(HttpStatus.BAD_REQUEST);
            return mav;
        }
        mav.addObject("userLoggedIn", username);

        return mav;
    }

    @GetMapping(path = "/view")
    public ModelAndView showUserRecipes(HttpSession session) {

        String username = (String) session.getAttribute("name");

        List<Recipe> recipes = recipeSvc.getAllUserRecipesByUserId(username);

        ModelAndView mav = new ModelAndView();

        mav.setViewName("user_recipe_view");
        mav.setStatus(HttpStatus.OK);
        mav.addObject("userLoggedIn", username);
        mav.addObject("recipes", recipes);
        return mav;
    }

    @PostMapping(path = "/delete")
    public ModelAndView showDeleteConfimation(HttpSession session, @RequestBody MultiValueMap<String, String> form) {
        String username = (String) session.getAttribute("name");
        ModelAndView mav = new ModelAndView();
        mav.setViewName("delete_recipe_confirmation");
        mav.setStatus(HttpStatus.OK);
        mav.addObject("recipeIdToDelete", form.getFirst("recipeIdToDelete"));
        mav.addObject("userLoggedIn", username);
        return mav;
    }

    @PostMapping(path = "/delete", params = "yes")
    public ModelAndView yesDeleteConfimation(HttpSession session, @RequestBody MultiValueMap<String, String> form) {
        String username = (String) session.getAttribute("name");
        ModelAndView mav = new ModelAndView();
        mav.addObject("userLoggedIn", username);
        mav.setViewName("delete_recipe_status");

        String recipeIdStr = form.getFirst("recipeIdToDelete");
        Integer recipeId = null;
        try {
            recipeId = Integer.parseInt(recipeIdStr);
        } catch (NumberFormatException ex) {
            mav.addObject("statusMessage", "Something went wrong! %s".formatted(ex.getMessage()));
            mav.setStatus(HttpStatus.BAD_REQUEST);
            return mav;
        }

        Optional<Recipe> r = recipeSvc.getRecipeByRecipeId(recipeId);

        if (r.isEmpty()) {
            mav.addObject("statusMessage", "Something went wrong! Recipe not found!");
            mav.setStatus(HttpStatus.NOT_FOUND);
            return mav;
        }

        s3Svc.delete(r.get().getThumbnail());
        recipeSvc.deleteRecipeByRecipeId(recipeId);
        mav.addObject("statusMessage", "Successfully deleted!");
        mav.setStatus(HttpStatus.OK);
        return mav;
    }

    @PostMapping(path = "/delete", params = "no")
    public ModelAndView noDeleteConfimation(HttpSession session, @RequestBody MultiValueMap<String, String> form) {
        String recipeId = form.getFirst("recipeIdToDelete");
        return new ModelAndView("redirect:/search/u/%s".formatted(recipeId));
    }

    @PostMapping(path = "/edit")
    public ModelAndView showEditForm(HttpSession session, @RequestParam MultiValueMap<String, String> form) {
        String username = (String) session.getAttribute("name");
        ModelAndView mav = new ModelAndView();
        mav.addObject("userLoggedIn", username);

        String recipeId = form.getFirst("recipeIdToEdit");
        Optional<Recipe> recipeOpt = recipeSvc.getRecipeByRecipeId(Integer.parseInt(recipeId));
        if (recipeOpt.isEmpty()) {
            mav.setViewName("error");
            mav.addObject("errorMsg", "Something went wrong when editing recipe!");
            mav.setStatus(HttpStatus.NOT_FOUND);
            return mav;
        }

        List<String> areas = searchSvc.getAllAreas();
        List<String> categories = searchSvc.getAllCategories();

        mav.addObject("recipe", recipeOpt.get());
        mav.addObject("recipeAreas", areas);
        mav.addObject("recipeCategories", categories);
        mav.setViewName("edit_recipe");
        mav.setStatus(HttpStatus.OK);
        return mav;
    }

    @PostMapping(path = "/edit", params = "done")
    public ModelAndView postEditForm(HttpSession session,
            @RequestParam MultiValueMap<String, String> form,
            @RequestParam MultipartFile recipeThumbnail) {

        String username = (String) session.getAttribute("name");
        ModelAndView mav = new ModelAndView();
        mav.addObject("userLoggedIn", username);

        String recipeId = form.getFirst("recipeIdToEdit");

        Optional<Recipe> recipeOpt = recipeSvc.getRecipeByRecipeId(Integer.parseInt(recipeId));

        if (recipeOpt.isEmpty()) {
            mav.setViewName("error");
            mav.addObject("errorMsg", "Something went wrong when editing recipe! Recipe not found!");
            mav.setStatus(HttpStatus.NOT_FOUND);
            return mav;
        }

        Recipe oldRecipe = recipeOpt.get();
        
        String thumbnailId = oldRecipe.getThumbnail();

        if (!recipeThumbnail.isEmpty()) {
            s3Svc.delete(thumbnailId);
            thumbnailId = s3Svc.upload(recipeThumbnail, username);
        }

        Recipe r = ConversionUtils.convert(form);
        r.setCreatedBy(username);
        r.setThumbnail(thumbnailId);
        r.setRecipeId(oldRecipe.getRecipeId());

        try {
            recipeSvc.updateRecipe(r, username);
            mav.setViewName("edit_recipe_success");
            mav.setStatus(HttpStatus.OK);
        } catch(Exception ex) {
            if(!recipeThumbnail.isEmpty()) {
                s3Svc.delete(thumbnailId);
            }
            mav.setViewName("error");
            mav.addObject("errorMsg", "Something went wrong when editing recipe");
            mav.setStatus(HttpStatus.BAD_REQUEST);
            return mav;
        }

        return mav;
    }

}
