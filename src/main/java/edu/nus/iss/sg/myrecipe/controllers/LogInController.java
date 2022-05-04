package edu.nus.iss.sg.myrecipe.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import edu.nus.iss.sg.myrecipe.services.LogInService;

@Controller
@RequestMapping(path="/login")
public class LogInController {

    @Autowired
    private LogInService logInSvc;

    @GetMapping(path="/logout")
    public ModelAndView getLogOut(HttpSession session) {
        session.invalidate();
        ModelAndView mav = new ModelAndView();
        mav.setStatus(HttpStatus.OK);
        mav.setViewName("index");
        return mav;
    }
    
    @PostMapping(path="/create")
    public ModelAndView createAcc(@RequestBody MultiValueMap<String, String> form) {
        String username = form.getFirst("usernameNew");
        String password = form.getFirst("passwordNew");
        String email = form.getFirst("emailNew");

        Boolean result = logInSvc.createAccount(username, email, password);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("login");
        mav.setStatus(HttpStatus.OK);
        if(result) {
            mav.addObject("statusMessage", "Thank you! Please log in.");
        }
        else{
            mav.addObject("statusMessage", "Email is taken! Please use another email");
        }

        return mav;
    }

    @PostMapping(path="/auth")
    public ModelAndView authAcc(@RequestBody MultiValueMap<String, String> form, HttpSession session) {
        String username = form.getFirst("username");
        String password = form.getFirst("password");
        Boolean result = logInSvc.authAccount(username, password);
        ModelAndView mav = new ModelAndView();
        mav.setStatus(HttpStatus.OK);
        if(result) {
            session.setAttribute("name", username);
            mav.addObject("userLoggedIn", username);
            mav.addObject("statusMessage", "Welcome %s!".formatted(username));
            mav.setViewName("index");
        } else {
            mav.addObject("statusMessage", "Log in failed!");
            mav.setViewName("login");
        }
        return mav;
    }
}
