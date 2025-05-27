package com.retailonboardpro.controller;

import com.retailonboardpro.dto.CreateRoleRequestDto;
import com.retailonboardpro.dto.ReviewRoleRequestDto;
import com.retailonboardpro.dto.RoleRequestDto;
import com.retailonboardpro.entity.RoleRequest;
import com.retailonboardpro.security.service.UserDetailsImpl;
import com.retailonboardpro.service.RoleRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/role-requests")
@RequiredArgsConstructor
@Slf4j
public class RoleRequestController {
    
    private final RoleRequestService roleRequestService;
    
    @PostMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> createRoleRequest(
            @Valid @RequestBody CreateRoleRequestDto createDto,
            Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            if (!roleRequestService.canUserCreateRoleRequest(userDetails.getId())) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("You cannot create a role change request at this time"));
            }
            
            RoleRequestDto roleRequest = roleRequestService.createRoleRequest(userDetails.getId(), createDto);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createSuccessResponse("Role change request created successfully", roleRequest));
            
        } catch (Exception e) {
            log.error("Error creating role request", e);
            return ResponseEntity.badRequest()
                    .body(createErrorResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/pending")
    @PreAuthorize("hasRole('CEO') or hasRole('DIRECTOR')")
    public ResponseEntity<?> getPendingRoleRequests() {
        try {
            List<RoleRequestDto> requests = roleRequestService.getPendingRoleRequests();
            return ResponseEntity.ok(createSuccessResponse("Pending requests retrieved successfully", requests));
        } catch (Exception e) {
            log.error("Error getting pending role requests", e);
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/review")
    @PreAuthorize("hasRole('CEO') or hasRole('DIRECTOR')")
    public ResponseEntity<?> reviewRoleRequest(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRoleRequestDto reviewDto,
            Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            RoleRequestDto updatedRequest = roleRequestService.reviewRoleRequest(id, userDetails.getId(), reviewDto);
            
            String action = reviewDto.getAction() == RoleRequest.RoleRequestStatus.APPROVED ? "approved" : "rejected";
            return ResponseEntity.ok(createSuccessResponse("Request " + action + " successfully", updatedRequest));
            
        } catch (Exception e) {
            log.error("Error reviewing role request", e);
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return response;
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
    
    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('STAFF') or hasRole('MANAGER') or hasRole('DIRECTOR') or hasRole('CEO')")
    public ResponseEntity<?> getMyRoleRequests(Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<RoleRequestDto> requests = roleRequestService.getUserRoleRequests(userDetails.getId());
            
            return ResponseEntity.ok(createSuccessResponse("Requests retrieved successfully", requests));
            
        } catch (Exception e) {
            log.error("Error getting user role requests", e);
            return ResponseEntity.badRequest()
                    .body(createErrorResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('CEO') or hasRole('DIRECTOR')")
    public ResponseEntity<?> getRoleRequestsByStatus(@PathVariable String status) {
        try {
            RoleRequest.RoleRequestStatus requestStatus;
            try {
                requestStatus = RoleRequest.RoleRequestStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Invalid status: " + status));
            }
            
            List<RoleRequestDto> requests = roleRequestService.getRoleRequestsByStatus(requestStatus);
            
            return ResponseEntity.ok(createSuccessResponse("Requests by status retrieved successfully", requests));
            
        } catch (Exception e) {
            log.error("Error getting role requests by status", e);
            return ResponseEntity.badRequest()
                    .body(createErrorResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/can-create")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> canCreateRoleRequest(Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            boolean canCreate = roleRequestService.canUserCreateRoleRequest(userDetails.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("canCreate", canCreate);
            response.put("message", canCreate ? "Can create request" : "Cannot create request at this time");
            
            return ResponseEntity.ok(createSuccessResponse("Create permission check successful", response));
            
        } catch (Exception e) {
            log.error("Error checking can create role request", e);
            return ResponseEntity.badRequest()
                    .body(createErrorResponse(e.getMessage()));
        }
    }
} 