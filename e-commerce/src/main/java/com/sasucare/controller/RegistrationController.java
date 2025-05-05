package com.sasucare.controller;

import com.sasucare.model.Role;
import com.sasucare.model.User;
import com.sasucare.service.RoleService;
import com.sasucare.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.Set;

/**
 * Controller for handling user registration
 */
@Controller
public class RegistrationController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public RegistrationController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    /**
     * Display customer registration form
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "register";
    }

    /**
     * Display seller registration form
     */
    @GetMapping("/register-seller")
    public String showSellerRegistrationForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "register-seller";
    }

    /**
     * Process customer registration form submission
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                              BindingResult bindingResult,
                              @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
                              @RequestParam(value = "agreeTerms", required = false) Boolean agreeTerms,
                              RedirectAttributes redirectAttributes,
                              Model model) {

        // Validate passwords match
        if (!user.getPassword().equals(confirmPassword)) {
            bindingResult.rejectValue("password", "error.user", "Passwords do not match");
        }

        // Validate terms agreement
        if (agreeTerms == null || !agreeTerms) {
            model.addAttribute("error", "You must agree to the Terms of Service and Privacy Policy");
            return "register";
        }

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Check if email already exists
        if (userService.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "An account with that email already exists");
            return "register";
        }

        try {
            // Set default roles (CUSTOMER for regular signups)
            Role customerRole = roleService.getOrCreateCustomerRole();
            Set<Role> roles = new HashSet<>();
            roles.add(customerRole);
            user.setRoles(roles);

            // Set account status
            user.setActive(true);

            // Save user - password will be encoded in service
            userService.registerUser(user);

            // Add success message and redirect to login
            redirectAttributes.addFlashAttribute("success",
                "Registration successful! Please check your email to verify your account.");
            return "redirect:/login";

        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    /**
     * Process seller registration form submission
     */
    @PostMapping("/register-seller")
    public String registerSeller(@Valid @ModelAttribute("user") User user,
                                BindingResult bindingResult,
                                @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
                                @RequestParam(value = "agreeTerms", required = false) Boolean agreeTerms,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        // Validate passwords match
        if (confirmPassword == null || !user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register-seller";
        }

        // Validate terms agreement
        if (agreeTerms == null || !agreeTerms) {
            model.addAttribute("error", "You must agree to the terms and conditions");
            return "register-seller";
        }

        // Check if shop name is provided
        if (user.getShopName() == null || user.getShopName().trim().isEmpty()) {
            model.addAttribute("error", "Shop name is required");
            return "register-seller";
        }

        // Check if email is already in use
        if (userService.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Email is already in use");
            return "register-seller";
        }

        // If there are validation errors, return to form
        if (bindingResult.hasErrors()) {
            return "register-seller";
        }

        try {
            // Set seller role
            Role sellerRole = roleService.getOrCreateSellerRole();
            Set<Role> roles = new HashSet<>();
            roles.add(sellerRole);
            user.setRoles(roles);

            // Set account status (inactive until approved)
            user.setActive(true);
            user.setVerified(false); // Require verification for seller accounts

            // Save user - password will be encoded in service
            userService.registerUser(user);

            // Add success message and redirect to login
            redirectAttributes.addFlashAttribute("success",
                "Shop registration successful! Please check your email to verify your account.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register-seller";
        }
    }

    /**
     * Verify user email with token
     */
    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        if (userService.verifyEmail(token)) {
            redirectAttributes.addFlashAttribute("success",
                "Your email has been verified successfully. You can now log in.");
        } else {
            redirectAttributes.addFlashAttribute("error",
                "Invalid or expired verification token.");
        }
        return "redirect:/login";
    }
}
