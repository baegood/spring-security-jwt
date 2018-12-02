package com.example.springsecuritydemo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello {

    @GetMapping("/hello")
    public String hello() {
        return "Hello Sangwoo!!!!!!!!!!!";
    }

    @GetMapping("/test")
    public String test() {
        return "test user controller!!!";
    }
}
