package com.route.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
@RestController
public class HomeController {
    
    @GetMapping("/test")
    public String getTest() {
        return "GET test successful";
    }

    @GetMapping("/hello3.0")
    public String hello(){
        return "GET hello";
    }

    @PostMapping("/test")
    public String postTest(@RequestParam String data) {
        return "POST test successful with data: " + data;
    }
}
