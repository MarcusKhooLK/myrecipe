package edu.nus.iss.sg.myrecipe.controllers;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import edu.nus.iss.sg.myrecipe.services.SearchService;

@Controller
public class MainController {

    @Autowired
    private SearchService searchSvc;

    @GetMapping(path="")
    public ModelAndView showIndex(HttpSession session) {
        String username = (String)session.getAttribute("name");
        ModelAndView mav = new ModelAndView();
        
        mav.addObject("userLoggedIn", username);

        List<String> categories = searchSvc.getAllCategories();
        List<String> areas = searchSvc.getAllAreas();
        
        mav.addObject("categories", categories);
        mav.addObject("areas", areas);
        mav.setStatus(HttpStatus.OK);
        mav.setViewName("index");
        return mav;
    }
}
