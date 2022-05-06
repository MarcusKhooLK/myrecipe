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
    public ModelAndView postSearch(@RequestParam String s, HttpSession session) {
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
    public ModelAndView postSearchByRecipeId(@PathVariable(name="recipeId") String recipeId, 
                                            @PathVariable(name="pathId", required = false) String pathId,
                                            HttpSession session) {
        final ModelAndView mav = new ModelAndView();
        String username = (String)session.getAttribute("name");
        Optional<Recipe> recipe = Optional.empty();

        if(pathId == null || pathId.trim().isBlank()) {
            recipe = searchSvc.searchRecipeFromMealDbById(recipeId.trim());
        } else {
            recipe = searchSvc.searchRecipeFromMyRecipeDbById(recipeId.trim());
        }

        if (recipe.isEmpty()) {
            mav.setStatus(HttpStatus.NOT_FOUND);
            mav.addObject("errorMsg", "Not found!");
            mav.setViewName("error");
        } else {
            mav.setStatus(HttpStatus.OK);
            mav.addObject("recipe", recipe.get());
            mav.setViewName("search");
            mav.addObject("recipes", new ArrayList<Recipe>());
            mav.addObject("searchString", "");
        }
        mav.addObject("userLoggedIn", username);
        return mav;
    }
}
