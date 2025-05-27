package com.retailonboardpro.dto;

import com.retailonboardpro.constant.AppConstants;
import com.retailonboardpro.entity.RoleRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for RoleRequest entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequestDto {
    
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String currentRole;
    private AppConstants.Role requestedRole;
    private String reason;
    private RoleRequest.RoleRequestStatus status;
    private String statusDisplayName;
    private Long reviewedById;
    private String reviewedByName;
    private String adminComments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime reviewedAt;
    
    /**
     * Constructor from RoleRequest entity
     */
    public RoleRequestDto(RoleRequest roleRequest) {
        this.id = roleRequest.getId();
        this.userId = roleRequest.getUser().getId();
        this.userName = roleRequest.getUser().getName();
        this.userEmail = roleRequest.getUser().getEmail();
        this.currentRole = roleRequest.getUser().getRole();
        this.requestedRole = roleRequest.getRequestedRole();
        this.reason = roleRequest.getReason();
        this.status = roleRequest.getStatus();
        this.statusDisplayName = roleRequest.getStatus().getDisplayName();
        this.adminComments = roleRequest.getAdminComments();
        this.createdAt = roleRequest.getCreatedAt();
        this.updatedAt = roleRequest.getUpdatedAt();
        this.reviewedAt = roleRequest.getReviewedAt();
        
        if (roleRequest.getReviewedBy() != null) {
            this.reviewedById = roleRequest.getReviewedBy().getId();
            this.reviewedByName = roleRequest.getReviewedBy().getName();
        }
    }
    
    /**
     * Check if the request is pending
     */
    public boolean isPending() {
        return status == RoleRequest.RoleRequestStatus.PENDING;
    }
    
    /**
     * Check if the request is approved
     */
    public boolean isApproved() {
        return status == RoleRequest.RoleRequestStatus.APPROVED;
    }
    
    /**
     * Check if the request is rejected
     */
    public boolean isRejected() {
        return status == RoleRequest.RoleRequestStatus.REJECTED;
    }
} 