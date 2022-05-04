package edu.nus.iss.sg.myrecipe.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {

    @GetMapping(path="")
    public ModelAndView showIndex(HttpSession session) {
        String username = (String)session.getAttribute("name");
        ModelAndView mav = new ModelAndView();
        mav.setStatus(HttpStatus.OK);
        mav.setViewName("index");
        mav.addObject("userLoggedIn", username);
        return mav;
    }
    
    @GetMapping(path="/login")
    public ModelAndView showLoginView() {
        ModelAndView mav = new ModelAndView();
        mav.setStatus(HttpStatus.OK);
        mav.setViewName("login");
        return mav;
    }

    @GetMapping(path="/createRecipe")
    public ModelAndView showCreateRecipe(HttpSession session) {
        return new ModelAndView("redirect:/protected/createRecipe");
    }
}
