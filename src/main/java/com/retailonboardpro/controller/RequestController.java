package com.retailonboardpro.controller;

import com.retailonboardpro.constant.AppConstants;
import com.retailonboardpro.dto.RequestDetailsDto;
import com.retailonboardpro.dto.RequestDto;
import com.retailonboardpro.entity.User;
import com.retailonboardpro.security.service.UserDetailsImpl;
import com.retailonboardpro.service.RequestService;
import com.retailonboardpro.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/requests")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<RequestDto>> getAllRequests(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.getUserEntityById(userDetails.getId());

        List<RequestDto> requests;
        
        // Phân quyền truy cập dữ liệu
        if (user.getRole().equals(AppConstants.ROLE_STAFF)) {
            // Nhân viên chỉ thấy yêu cầu của mình
            requests = requestService.getRequestsByCreator(user);
        } else if (user.getRole().equals(AppConstants.ROLE_MANAGER)) {
            // Manager thấy các yêu cầu đang chờ xử lý
            requests = requestService.getRequestsByStatus(AppConstants.STATUS_PENDING_MANAGER);
        } else if (user.getRole().equals(AppConstants.ROLE_DIRECTOR)) {
            // Director thấy các yêu cầu đang chờ xử lý
            requests = requestService.getRequestsByStatus(AppConstants.STATUS_PENDING_DIRECTOR);
        } else {
            // CEO thấy tất cả
            requests = requestService.getAllRequests();
        }

        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestDetailsDto> getRequestById(@PathVariable Long id) {
        RequestDetailsDto request = requestService.getRequestDetailsById(id);
        return ResponseEntity.ok(request);
    }

    @PostMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<RequestDto> createRequest(@Valid @RequestBody Map<String, String> requestData, 
                                                    Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User creator = userService.getUserEntityById(userDetails.getId());
        
        String category = requestData.get("category");
        String reason = requestData.get("reason");
        
        RequestDto createdRequest = requestService.createRequest(category, reason, creator);
        return ResponseEntity.ok(createdRequest);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('MANAGER', 'DIRECTOR', 'CEO')")
    public ResponseEntity<RequestDto> approveRequest(@PathVariable Long id, 
                                                    @RequestBody Map<String, String> approvalData,
                                                    Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User approver = userService.getUserEntityById(userDetails.getId());
        
        String comments = approvalData.get("comments");
        String nextStatus;
        
        // Xác định trạng thái tiếp theo dựa trên vai trò người phê duyệt
        if (approver.getRole().equals(AppConstants.ROLE_MANAGER)) {
            nextStatus = AppConstants.STATUS_PENDING_DIRECTOR;
        } else if (approver.getRole().equals(AppConstants.ROLE_DIRECTOR)) {
            nextStatus = AppConstants.STATUS_PENDING_CEO;
        } else {
            nextStatus = AppConstants.STATUS_APPROVED;
        }
        
        RequestDto updatedRequest = requestService.updateRequestStatus(id, nextStatus, approver, comments);
        return ResponseEntity.ok(updatedRequest);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('MANAGER', 'DIRECTOR', 'CEO')")
    public ResponseEntity<RequestDto> rejectRequest(@PathVariable Long id, 
                                                   @RequestBody Map<String, String> rejectionData,
                                                   Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User approver = userService.getUserEntityById(userDetails.getId());
        
        String comments = rejectionData.get("comments");
        String rejectionStatus;
        
        // Xác định trạng thái từ chối dựa trên vai trò người phê duyệt
        if (approver.getRole().equals(AppConstants.ROLE_MANAGER)) {
            rejectionStatus = AppConstants.STATUS_REJECTED_MANAGER;
        } else if (approver.getRole().equals(AppConstants.ROLE_DIRECTOR)) {
            rejectionStatus = AppConstants.STATUS_REJECTED_DIRECTOR;
        } else {
            rejectionStatus = AppConstants.STATUS_REJECTED_CEO;
        }
        
        RequestDto updatedRequest = requestService.updateRequestStatus(id, rejectionStatus, approver, comments);
        return ResponseEntity.ok(updatedRequest);
    }
} 