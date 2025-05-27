package com.retailonboardpro.controller;

import com.retailonboardpro.constant.AppConstants;
import com.retailonboardpro.dto.RequestDto;
import com.retailonboardpro.dto.RoleRequestDto;
import com.retailonboardpro.service.RequestService;
import com.retailonboardpro.service.RoleRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    
    private final RoleRequestService roleRequestService;
    private final RequestService requestService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'DIRECTOR', 'CEO')")
    public String dashboard(Model model) {
        // Lấy thống kê cho Training Requests (chức năng chính)
        List<RequestDto> allRequests = requestService.getAllRequests();
        
        Map<String, Long> stats = new HashMap<>();
        
        // Đếm các requests pending (tất cả các trạng thái pending)
        long pendingCount = allRequests.stream()
                .filter(request -> request.getStatus().startsWith("Pending"))
                .count();
        
        // Đếm các requests approved
        long approvedCount = allRequests.stream()
                .filter(request -> AppConstants.STATUS_APPROVED.equals(request.getStatus()))
                .count();
        
        // Đếm các requests rejected (tất cả các trạng thái rejected)
        long rejectedCount = allRequests.stream()
                .filter(request -> request.getStatus().startsWith("Rejected"))
                .count();
        
        stats.put("pending", pendingCount);
        stats.put("approved", approvedCount);
        stats.put("rejected", rejectedCount);
        
        // Lấy các Training Requests gần đây (7 ngày gần nhất)
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<RequestDto> recentRequests = allRequests.stream()
                .filter(request -> request.getCreatedAt().isAfter(sevenDaysAgo))
                .limit(5) // Giới hạn 5 requests gần nhất
                .collect(Collectors.toList());
        
        model.addAttribute("stats", stats);
        model.addAttribute("recentRequests", recentRequests);
        
        return "dashboard";
    }
} 