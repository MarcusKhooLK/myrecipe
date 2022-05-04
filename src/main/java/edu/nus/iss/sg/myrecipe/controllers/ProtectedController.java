package edu.nus.iss.sg.myrecipe.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/protected")
public class ProtectedController {

    @GetMapping("/{view}")
    @PostMapping("/{view}")
    public ModelAndView post(@PathVariable String view, HttpSession session) {

        String username = (String)session.getAttribute("name");
        
        ModelAndView mav = new ModelAndView();

        mav.setViewName(view);
        mav.addObject("username", username);
        mav.setStatus(HttpStatus.OK);

        return mav;
    }

}
