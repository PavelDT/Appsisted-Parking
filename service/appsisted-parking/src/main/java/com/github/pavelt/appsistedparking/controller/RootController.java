package com.github.pavelt.appsistedparking.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @RequestMapping("/")
    public String root(){
        return "You've accessed the root. Why?";
    }
}
