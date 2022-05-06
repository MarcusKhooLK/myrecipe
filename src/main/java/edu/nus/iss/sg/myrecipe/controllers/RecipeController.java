package edu.nus.iss.sg.myrecipe.controllers;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import edu.nus.iss.sg.myrecipe.models.Recipe;
import edu.nus.iss.sg.myrecipe.services.AmazonS3Service;
import edu.nus.iss.sg.myrecipe.services.RecipeService;
import edu.nus.iss.sg.myrecipe.utils.ConversionUtils;

@Controller
@RequestMapping(path="/account/recipe")
public class RecipeController {
    
    @Autowired
    private RecipeService recipeSvc;

    @Autowired
    private AmazonS3Service s3Svc;

    @GetMapping(path="/create")
    public ModelAndView showCreateRecipe(HttpSession session) {
        String username = (String)session.getAttribute("name");
        
        ModelAndView mav = new ModelAndView();

        mav.setViewName("create_recipe");
        mav.addObject("userLoggedIn", username);
        mav.setStatus(HttpStatus.OK);

        return mav;
    }

    @PostMapping(path="/create")
    public ModelAndView postCreateRecipe(@RequestParam MultiValueMap<String, String> form,
                                            @RequestParam MultipartFile recipeThumbnail,
                                            HttpSession session) {
        String username = (String)session.getAttribute("name");
        ModelAndView mav = new ModelAndView();
    
        String thumbnailId = s3Svc.upload(recipeThumbnail, username);

        Recipe r = ConversionUtils.convert(form);
        r.setCreatedBy(username);
        r.setThumbnail("https://dumpbucket.sgp1.digitaloceanspaces.com/myrecipe/images/%s".formatted(thumbnailId));

        if(recipeSvc.createRecipe(r, username)) {
            mav.setViewName("create_recipe_success");
            mav.setStatus(HttpStatus.OK);
        } else {
            s3Svc.delete(thumbnailId);
            mav.setViewName("error");
            mav.addObject("errorMsg", "Something went wrong when creating recipe");
            mav.setStatus(HttpStatus.BAD_REQUEST);
        }
        mav.addObject("userLoggedIn", username);

        return mav;
    }
    
    @GetMapping(path="/view")
    public ModelAndView showUserRecipes(HttpSession session) {

        String username = (String)session.getAttribute("name");

        List<Recipe> recipes = recipeSvc.getAllUserRecipesByUserId(username);

        ModelAndView mav = new ModelAndView();

        mav.setViewName("user_recipe_view");
        mav.setStatus(HttpStatus.OK);
        mav.addObject("userLoggedIn", username);
        mav.addObject("recipes", recipes);
        return mav;
    }
    
}
