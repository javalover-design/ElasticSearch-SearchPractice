package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author lambda
 */
@Controller
public class IndexController {

    @GetMapping({"/","/index,/search"})
    public String indexPage(){
        return "index";
    }
}
