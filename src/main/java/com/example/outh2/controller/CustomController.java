package com.example.outh2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/api")
public class CustomController {
    @GetMapping
    public String index() {
        return "home page";
    }

    @GetMapping(path = "/customers")
    public String customers() {
        return "customers page";
    }

    @GetMapping(path = "/welcome")
    public String welcome() {
        return "welcome page";
    }

}

