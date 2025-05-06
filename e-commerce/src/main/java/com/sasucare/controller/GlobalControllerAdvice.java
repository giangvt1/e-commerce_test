package com.sasucare.controller;

import com.sasucare.model.User;
import com.sasucare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Global controller advice that adds common model attributes to all controllers
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserService userService;
    
    @Autowired
    public GlobalControllerAdvice(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Add the currently logged-in user to the model for all controllers
     */
    @ModelAttribute("currentUser")
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        
        return userService.findByEmail(auth.getName());
    }
}
