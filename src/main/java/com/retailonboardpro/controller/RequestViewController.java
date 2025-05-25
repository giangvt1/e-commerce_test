package com.retailonboardpro.controller;

import com.retailonboardpro.constant.AppConstants;
import com.retailonboardpro.dto.RequestDetailsDto;
import com.retailonboardpro.entity.User;
import com.retailonboardpro.security.service.UserDetailsImpl;
import com.retailonboardpro.service.RequestService;
import com.retailonboardpro.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/requests-view")
public class RequestViewController {
    
    @Autowired
    private RequestService requestService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public String getAllRequests(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.getUserEntityById(userDetails.getId());
        
        // Phân quyền truy cập dữ liệu
        if (user.getRole().equals(AppConstants.ROLE_STAFF)) {
            // Nhân viên chỉ thấy yêu cầu của mình
            model.addAttribute("requests", requestService.getRequestsByCreator(user));
        } else if (user.getRole().equals(AppConstants.ROLE_MANAGER)) {
            // Manager thấy các yêu cầu đang chờ xử lý
            model.addAttribute("requests", requestService.getRequestsByStatus(AppConstants.STATUS_PENDING_MANAGER));
        } else if (user.getRole().equals(AppConstants.ROLE_DIRECTOR)) {
            // Director thấy các yêu cầu đang chờ xử lý
            model.addAttribute("requests", requestService.getRequestsByStatus(AppConstants.STATUS_PENDING_DIRECTOR));
        } else if (user.getRole().equals(AppConstants.ROLE_CEO)) {
            // CEO thấy tất cả
            model.addAttribute("requests", requestService.getAllRequests());
        }
        
        model.addAttribute("categories", AppConstants.REQUEST_CATEGORIES);
        model.addAttribute("userRole", user.getRole());
        
        return "requests/list";
    }
    
    @GetMapping("/{id}")
    public String viewRequest(@PathVariable Long id, Model model) {
        RequestDetailsDto request = requestService.getRequestDetailsById(id);
        model.addAttribute("request", request);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.getUserEntityById(userDetails.getId());
        model.addAttribute("userRole", user.getRole());
        
        return "requests/detail";
    }
    
    @GetMapping("/create")
    public String createRequestForm(Model model) {
        model.addAttribute("categories", AppConstants.REQUEST_CATEGORIES);
        return "requests/create";
    }
    
    @PostMapping("/create")
    public String createRequest(@RequestParam("category") String category,
                               @RequestParam("reason") String reason,
                               RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User creator = userService.getUserEntityById(userDetails.getId());
            
            requestService.createRequest(category, reason, creator);
            redirectAttributes.addFlashAttribute("success", "Request created successfully");
            return "redirect:/requests-view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating request: " + e.getMessage());
            return "redirect:/requests-view/create";
        }
    }
    
    @PostMapping("/{id}/approve")
    public String approveRequest(@PathVariable Long id, @RequestParam String comments, RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User approver = userService.getUserEntityById(userDetails.getId());
            
            String nextStatus;
            
            // Xác định trạng thái tiếp theo dựa trên vai trò người phê duyệt
            if (approver.getRole().equals(AppConstants.ROLE_MANAGER)) {
                nextStatus = AppConstants.STATUS_PENDING_DIRECTOR;
            } else if (approver.getRole().equals(AppConstants.ROLE_DIRECTOR)) {
                nextStatus = AppConstants.STATUS_PENDING_CEO;
            } else {
                nextStatus = AppConstants.STATUS_APPROVED;
            }
            
            requestService.updateRequestStatus(id, nextStatus, approver, comments);
            redirectAttributes.addFlashAttribute("success", "Request has been approved");
            return "redirect:/requests-view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error approving request: " + e.getMessage());
            return "redirect:/requests-view/" + id;
        }
    }
    
    @PostMapping("/{id}/reject")
    public String rejectRequest(@PathVariable Long id, @RequestParam String comments, RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User approver = userService.getUserEntityById(userDetails.getId());
            
            String rejectionStatus;
            
            // Xác định trạng thái từ chối dựa trên vai trò người phê duyệt
            if (approver.getRole().equals(AppConstants.ROLE_MANAGER)) {
                rejectionStatus = AppConstants.STATUS_REJECTED_MANAGER;
            } else if (approver.getRole().equals(AppConstants.ROLE_DIRECTOR)) {
                rejectionStatus = AppConstants.STATUS_REJECTED_DIRECTOR;
            } else {
                rejectionStatus = AppConstants.STATUS_REJECTED_CEO;
            }
            
            requestService.updateRequestStatus(id, rejectionStatus, approver, comments);
            redirectAttributes.addFlashAttribute("success", "Request has been rejected");
            return "redirect:/requests-view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error rejecting request: " + e.getMessage());
            return "redirect:/requests-view/" + id;
        }
    }
} 