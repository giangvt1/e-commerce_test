package com.retailonboardpro.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String homepage() {
        return "homepage";
    }
    
    @GetMapping("/index")
    public String index() {
        return "index";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/home")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'DIRECTOR', 'CEO')")
    public String home() {
        return "home";
    }
} 