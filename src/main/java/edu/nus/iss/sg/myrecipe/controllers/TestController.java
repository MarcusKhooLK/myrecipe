package edu.nus.iss.sg.myrecipe.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestController {
    
    @Autowired
    private JdbcTemplate template;
    
    @GetMapping(path="/test")
    public ModelAndView test() {

        List<String> testData = new ArrayList<String>();
        final SqlRowSet result = template.queryForRowSet("select * from test;");
        while(result.next()) {
            testData.add(result.getString("name"));
        }
        ModelAndView mav = new ModelAndView();
        mav.setStatus(HttpStatus.OK);
        mav.setViewName("test");
        mav.addObject("testData", testData);

        return mav;
    }
}
