package com.retailonboardpro.controller;

import com.retailonboardpro.dto.auth.RegisterRequest;
import com.retailonboardpro.entity.User;
import com.retailonboardpro.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("")
@Slf4j
public class AuthViewController {

    @Autowired
    private UserService userService;
    
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        // Đảm bảo form luôn có một đối tượng RegisterRequest trống
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute RegisterRequest registerRequest,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        
        log.info("Received registration request: {}", registerRequest);
        
        // Kiểm tra lỗi validation từ annotations
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("error", "Vui lòng kiểm tra lại thông tin đăng ký!");
            redirectAttributes.addFlashAttribute("registerRequest", registerRequest);
            return "redirect:/register";
        }
        
        // Validate password strength (minimum 8 characters)
        if (registerRequest.getPassword() == null || registerRequest.getPassword().length() < 8) {
            log.warn("Password is too short: {}", registerRequest.getPassword() != null ? registerRequest.getPassword().length() : "null");
            redirectAttributes.addFlashAttribute("error", "Mật khẩu phải có ít nhất 8 ký tự!");
            redirectAttributes.addFlashAttribute("registerRequest", registerRequest);
            return "redirect:/register";
        }
        
        // Check email uniqueness first (more specific error)
        if (userService.existsByEmail(registerRequest.getEmail())) {
            log.warn("Email already exists: {}", registerRequest.getEmail());
            redirectAttributes.addFlashAttribute("error", "Email đã được sử dụng!");
            redirectAttributes.addFlashAttribute("registerRequest", registerRequest);
            return "redirect:/register";
        }
        
        // Check username uniqueness (case insensitive)
        if (userService.existsByUsernameIgnoreCase(registerRequest.getUsername())) {
            log.warn("Username already exists (case insensitive): {}", registerRequest.getUsername());
            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập '" + registerRequest.getUsername() + "' đã được sử dụng! Vui lòng chọn tên đăng nhập khác.");
            redirectAttributes.addFlashAttribute("registerRequest", registerRequest);
            return "redirect:/register";
        }

        try {
            User user = new User();
            user.setName(registerRequest.getName());
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(registerRequest.getPassword());
            // Always assign STAFF role by default for new registrations
            user.setRole("STAFF");
            
            log.info("Creating new user with role STAFF");
            userService.createUser(user);
            
            log.info("User registered successfully: {}", user.getUsername());
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Bạn có thể đăng nhập với tài khoản STAFF.");
            return "redirect:/login";
            
        } catch (Exception e) {
            log.error("Error registering user", e);
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tạo tài khoản: " + e.getMessage());
            redirectAttributes.addFlashAttribute("registerRequest", registerRequest);
            return "redirect:/register";
        }
    }
} 