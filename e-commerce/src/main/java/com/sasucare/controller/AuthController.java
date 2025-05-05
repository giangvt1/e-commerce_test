package com.sasucare.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for handling authentication-related requests
 */
@Controller
public class AuthController {

    /**
     * Display login page or redirect if already logged in
     */
    @GetMapping("/login")
    public String login(RedirectAttributes redirectAttributes) {
        // Check if user is already authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            // User is already logged in, redirect to home with message
            redirectAttributes.addFlashAttribute("info", "You are already logged in");
            return "redirect:/home";
        }
        return "login";
    }

    /**
     * Display forgot password page
     */
    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }
}
