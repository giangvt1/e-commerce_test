package com.retailonboardpro.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for admin view pages
 */
@Controller
@RequestMapping("/admin")
public class AdminViewController {
    
    /**
     * Admin dashboard for managing role requests
     */
    @GetMapping("/role-requests")
    @PreAuthorize("hasRole('CEO') or hasRole('DIRECTOR')")
    public String adminRoleRequests() {
        return "admin/role-requests";
    }
    
    /**
     * Admin dashboard for approving training requests
     */
    @GetMapping("/requests")
    @PreAuthorize("hasRole('MANAGER') or hasRole('DIRECTOR') or hasRole('CEO')")
    public String adminRequests() {
        return "requests/list";
    }
} 