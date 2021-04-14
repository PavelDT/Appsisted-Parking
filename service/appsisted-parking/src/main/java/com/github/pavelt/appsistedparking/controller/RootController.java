package com.github.pavelt.appsistedparking.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    /**
     * Root controler of the app.
     * @return
     */
    @RequestMapping("/")
    @ResponseBody
    public String root(){
        return "Welcome to Appsisted-parking!\nNothing to see here.";
    }
}
