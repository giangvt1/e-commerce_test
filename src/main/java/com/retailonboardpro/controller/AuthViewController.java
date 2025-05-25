package com.retailonboardpro.controller;

import com.retailonboardpro.dto.auth.RegisterRequest;
import com.retailonboardpro.entity.User;
import com.retailonboardpro.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthViewController {

    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute RegisterRequest registerRequest,
                               RedirectAttributes redirectAttributes) {
        
        if (userService.existsByUsername(registerRequest.getUsername())) {
            redirectAttributes.addAttribute("error", "Tên đăng nhập đã được sử dụng!");
            return "redirect:/register";
        }

        if (userService.existsByEmail(registerRequest.getEmail())) {
            redirectAttributes.addAttribute("error", "Email đã được sử dụng!");
            return "redirect:/register";
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setRole(registerRequest.getRole());

        userService.createUser(user);
        
        redirectAttributes.addAttribute("registered", "true");
        return "redirect:/login";
    }
} 