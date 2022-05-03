package edu.nus.iss.sg.myrecipe.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public ModelAndView postSearch(@RequestParam String s) {
        final ModelAndView mav = new ModelAndView();
        List<Recipe> recipes = searchSvc.searchRecipes(s.trim().toLowerCase(), false);

        mav.setStatus(HttpStatus.OK);
        mav.setViewName("search");
        mav.addObject("recipes", recipes);
        mav.addObject("recipe", null);
        mav.addObject("searchString", s);
        return mav;
    }

    @GetMapping(path = "/{recipeId}")
    public ModelAndView postSearchByRecipeId(@PathVariable String recipeId) {
        final ModelAndView mav = new ModelAndView();
        Optional<Recipe> recipe = searchSvc.searchRecipeById(recipeId.trim());

        if (recipe.isEmpty()) {
            mav.setStatus(HttpStatus.NOT_FOUND);
        } else {
            mav.setStatus(HttpStatus.OK);
        }

        mav.setViewName("search");
        mav.addObject("recipes", new ArrayList<Recipe>());
        mav.addObject("recipe", recipe.get());
        mav.addObject("searchString", "");
        return mav;
    }
}
