package edu.nus.iss.sg.myrecipe.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import edu.nus.iss.sg.myrecipe.models.Recipe;
import edu.nus.iss.sg.myrecipe.services.SearchService;

@Controller
@RequestMapping(path = "/search")
public class SearchController {

    @Autowired
    private SearchService searchSvc;

    @GetMapping(path = "")
    public ModelAndView getSearch(@RequestParam String s, HttpSession session) {
        String username = (String)session.getAttribute("name");
        final ModelAndView mav = new ModelAndView();
        final String searchString = s.trim().toLowerCase();
        List<Recipe> recipes = searchSvc.searchRecipesFromMealDb(searchString);
        recipes.addAll(searchSvc.searchRecipesFromMyRecipeDb(searchString));

        mav.setStatus(HttpStatus.OK);
        mav.setViewName("search");
        mav.addObject("recipes", recipes);
        mav.addObject("recipe", null);
        mav.addObject("searchString", s);
        mav.addObject("userLoggedIn", username);
        return mav;
    }

    @GetMapping(path = {"/{recipeId}", "/{pathId}/{recipeId}"})
    public ModelAndView getSearchByRecipeId(@PathVariable(name="recipeId") String recipeIdStr, 
                                            @PathVariable(name="pathId", required = false) String pathId,
                                            HttpSession session) {
        final ModelAndView mav = new ModelAndView();
        String username = (String)session.getAttribute("name");
        mav.addObject("userLoggedIn", username);
        Optional<Recipe> recipeOpt = Optional.empty();
        Integer recipeId = null;
        
        try {
            recipeId = Integer.parseInt(recipeIdStr.trim());
        } catch(NumberFormatException ex) {
            mav.setStatus(HttpStatus.NOT_FOUND);
            mav.addObject("errorMsg", "Page not found!");
            mav.setViewName("error");
            mav.addObject("isCreatedByOwnUser", false);
            return mav;
        }

        if(pathId == null || pathId.trim().isBlank()) {
            recipeOpt = searchSvc.searchRecipeFromMealDbById(recipeId);
        } else {
            recipeOpt = searchSvc.searchRecipeFromMyRecipeDbById(recipeId);
        }

        if (recipeOpt.isEmpty()) {
            mav.setStatus(HttpStatus.NOT_FOUND);
            mav.addObject("errorMsg", "Page not found!");
            mav.setViewName("error");
            mav.addObject("isCreatedByOwnUser", false);
        } else {
            Recipe r = recipeOpt.get();

            Boolean isCreatedByOwnUser = false;
            if(r.getCreatedBy() != null) {
                isCreatedByOwnUser = r.getCreatedBy().contentEquals(username);
            }

            mav.setStatus(HttpStatus.OK);
            mav.addObject("recipe", r);
            mav.setViewName("search");
            mav.addObject("recipes", new ArrayList<Recipe>());
            mav.addObject("searchString", "");
            mav.addObject("isCreatedByOwnUser", isCreatedByOwnUser);
        }
        
        return mav;
    }
}
