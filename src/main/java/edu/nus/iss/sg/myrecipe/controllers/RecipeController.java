package edu.nus.iss.sg.myrecipe.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(path="/recipe")
public class RecipeController {

    @GetMapping(path="/create")
    public ModelAndView showCreateRecipe(HttpSession session) {
        String username = (String)session.getAttribute("name");
        
        ModelAndView mav = new ModelAndView();

        mav.setViewName("create_recipe");
        mav.addObject("userLoggedIn", username);
        mav.setStatus(HttpStatus.OK);

        return mav;
    }
    
}
