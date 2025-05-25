package com.retailonboardpro.controller;

import com.retailonboardpro.entity.User;
import com.retailonboardpro.security.service.UserDetailsImpl;
import com.retailonboardpro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.getUserEntityById(userDetails.getId());
        
        model.addAttribute("user", user);
        return "profile/index";
    }
    
    @PostMapping("/update")
    public String updateProfile(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam(value = "currentPassword", required = false) String currentPassword,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            RedirectAttributes redirectAttributes) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.getUserEntityById(userDetails.getId());
        
        // Cập nhật thông tin cơ bản
        user.setName(name);
        
        // Kiểm tra xem email có bị thay đổi không
        if (!user.getEmail().equals(email)) {
            // Kiểm tra email mới có bị trùng không
            if (userService.existsByEmail(email) && !user.getEmail().equals(email)) {
                redirectAttributes.addFlashAttribute("error", "Email is already used by another account");
                return "redirect:/profile";
            }
            user.setEmail(email);
        }
        
        // Cập nhật mật khẩu nếu có
        if (newPassword != null && !newPassword.isEmpty()) {
            if (currentPassword == null || currentPassword.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please enter your current password");
                return "redirect:/profile";
            }
            
            // Cập nhật qua UserService (bao gồm cả việc xác thực mật khẩu hiện tại)
            try {
                userService.updatePassword(user.getId(), currentPassword, newPassword);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
                return "redirect:/profile";
            }
        }
        
        // Lưu các thay đổi khác
        userService.updateUser(user.getId(), user);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        
        return "redirect:/profile";
    }
} 