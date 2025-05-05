package com.sasucare.controller;

import com.sasucare.model.User;
import com.sasucare.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

/**
 * Controller for handling user account management
 */
@Controller
@RequestMapping("/account")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    
    private final UserService userService;
    
    @Autowired
    public AccountController(UserService userService) {
        this.userService = userService;
    }
    /**
     * Display user profile page
     */
    @GetMapping("/profile")
    public String viewProfile(Model model) {
        // Get current logged in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(auth.getName());
        
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "account/profile";
    }

    /**
     * Display edit profile form
     */
    @GetMapping("/profile/edit")
    public String editProfileForm(Model model) {
        // Get current logged in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(auth.getName());
        
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "account/edit-profile";
    }
    
    /**
     * Process edit profile form submission
     */
    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("user") User updatedUser, 
                              BindingResult bindingResult,
                              @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                              RedirectAttributes redirectAttributes) {
        
        // Get current logged in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(auth.getName());
        
        if (user == null) {
            return "redirect:/login";
        }
        
        // If there are validation errors, return to form
        if (bindingResult.hasErrors()) {
            return "account/edit-profile";
        }
        
        try {
            // Update user fields (but don't change email or password here)
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            if (updatedUser.getShopName() != null) {
                user.setShopName(updatedUser.getShopName());
            }
            userService.updateUser(user);
            
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
            return "redirect:/account/profile";
            
        } catch (Exception e) {
            logger.error("Error updating profile: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Failed to update profile: " + e.getMessage());
            return "redirect:/account/profile/edit";
        }
    }
    
    /**
     * Display change password form
     */
    @GetMapping("/password")
    public String changePasswordForm() {
        return "account/change-password";
    }
    
    /**
     * Process change password form submission
     */
    @PostMapping("/password/update")
    public String updatePassword(@RequestParam("currentPassword") String currentPassword,
                               @RequestParam("newPassword") String newPassword,
                               @RequestParam("confirmPassword") String confirmPassword,
                               RedirectAttributes redirectAttributes) {
        
        // Get current logged in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(auth.getName());
        
        if (user == null) {
            return "redirect:/login";
        }
        
        // Validate current password
        if (!userService.authenticate(user.getEmail(), currentPassword)) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
            return "redirect:/account/password";
        }
        
        // Validate password match
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match");
            return "redirect:/account/password";
        }
        
        try {
            // Update password
            userService.updatePassword(user, newPassword);
            redirectAttributes.addFlashAttribute("success", "Password updated successfully!");
            return "redirect:/account/profile";
        } catch (Exception e) {
            logger.error("Error updating password: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Failed to update password: " + e.getMessage());
            return "redirect:/account/password";
        }
    }
    
    /**
     * Display orders page
     */
    @GetMapping("/orders")
    public String viewOrders(Model model) {
        // Get current logged in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(auth.getName());
        
        if (user == null) {
            return "redirect:/login";
        }
        
        // TODO: Load orders for this user
        // List<Order> orders = orderService.getOrdersByCustomer(user);
        // model.addAttribute("orders", orders);
        
        return "account/orders";
    }
}
