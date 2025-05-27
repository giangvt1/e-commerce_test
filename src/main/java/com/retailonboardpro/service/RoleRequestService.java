package com.retailonboardpro.service;

import com.retailonboardpro.constant.AppConstants;
import com.retailonboardpro.dto.CreateRoleRequestDto;
import com.retailonboardpro.dto.ReviewRoleRequestDto;
import com.retailonboardpro.dto.RoleRequestDto;
import com.retailonboardpro.entity.ApprovalHistory;
import com.retailonboardpro.entity.RoleRequest;
import com.retailonboardpro.entity.User;
import com.retailonboardpro.repository.ApprovalHistoryRepository;
import com.retailonboardpro.repository.RoleRequestRepository;
import com.retailonboardpro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing role change requests
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleRequestService {
    
    private final RoleRequestRepository roleRequestRepository;
    private final UserRepository userRepository;
    private final ApprovalHistoryRepository approvalHistoryRepository;
    
    /**
     * Create a new role change request
     */
    public RoleRequestDto createRoleRequest(Long userId, CreateRoleRequestDto createDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user is Staff (handle both "Staff" and "STAFF")
        if (!isStaffRole(user.getRole())) {
            throw new RuntimeException("Only Staff members can request role changes");
        }
        
        // Check if user already has a pending request
        if (roleRequestRepository.existsByUserAndStatus(user, RoleRequest.RoleRequestStatus.PENDING)) {
            throw new RuntimeException("You already have a pending role change request");
        }
        
        // Validate requested role
        if (createDto.getRequestedRole() == AppConstants.Role.Staff) {
            throw new RuntimeException("Cannot request change to Staff role");
        }
        
        // Create role request
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setUser(user);
        roleRequest.setRequestedRole(createDto.getRequestedRole());
        roleRequest.setReason(createDto.getReason());
        roleRequest.setStatus(RoleRequest.RoleRequestStatus.PENDING);
        
        roleRequest = roleRequestRepository.save(roleRequest);
        
        log.info("User {} created role change request to {}", user.getUsername(), createDto.getRequestedRole());
        
        return new RoleRequestDto(roleRequest);
    }
    
    /**
     * Get all role requests for a user
     */
    @Transactional(readOnly = true)
    public List<RoleRequestDto> getUserRoleRequests(Long userId) {
        return roleRequestRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(RoleRequestDto::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all pending role requests (for admin)
     */
    @Transactional(readOnly = true)
    public List<RoleRequestDto> getPendingRoleRequests() {
        return roleRequestRepository.findPendingRequestsWithUserDetails(RoleRequest.RoleRequestStatus.PENDING)
                .stream()
                .map(RoleRequestDto::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all role requests by status
     */
    @Transactional(readOnly = true)
    public List<RoleRequestDto> getRoleRequestsByStatus(RoleRequest.RoleRequestStatus status) {
        return roleRequestRepository.findByStatusWithDetails(status)
                .stream()
                .map(RoleRequestDto::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Review (approve/reject) a role request
     */
    public RoleRequestDto reviewRoleRequest(Long requestId, Long adminId, ReviewRoleRequestDto reviewDto) {
        RoleRequest roleRequest = roleRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Role request not found"));
        
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Administrator not found"));
        
        // Check if admin has permission (CEO or Director)
        if (!isCeoOrDirectorRole(admin.getRole())) {
            throw new RuntimeException("Only CEO or Director can approve role change requests");
        }
        
        // Check if request is still pending
        if (!roleRequest.isPending()) {
            throw new RuntimeException("This request has already been processed");
        }
        
        // Validate action
        if (reviewDto.getAction() != RoleRequest.RoleRequestStatus.APPROVED && 
            reviewDto.getAction() != RoleRequest.RoleRequestStatus.REJECTED) {
            throw new RuntimeException("Invalid action");
        }
        
        // Update role request
        roleRequest.setStatus(reviewDto.getAction());
        roleRequest.setReviewedBy(admin);
        roleRequest.setAdminComments(reviewDto.getComments());
        roleRequest.setReviewedAt(LocalDateTime.now());
        
        // If approved, update user's role
        if (reviewDto.getAction() == RoleRequest.RoleRequestStatus.APPROVED) {
            User user = roleRequest.getUser();
            String oldRole = user.getRole();
            user.setRole(roleRequest.getRequestedRole().getValue());
            userRepository.save(user);
            
            log.info("User {} role changed from {} to {} by admin {}", 
                    user.getUsername(), oldRole, user.getRole(), admin.getUsername());
        }
        
        roleRequest = roleRequestRepository.save(roleRequest);
        
        log.info("Admin {} {} role request {} with comments: {}", 
                admin.getUsername(), 
                reviewDto.getAction().name().toLowerCase(), 
                requestId, 
                reviewDto.getComments());
        
        return new RoleRequestDto(roleRequest);
    }
    
    /**
     * Get role request by ID
     */
    @Transactional(readOnly = true)
    public RoleRequestDto getRoleRequestById(Long requestId) {
        RoleRequest roleRequest = roleRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Role request not found"));
        return new RoleRequestDto(roleRequest);
    }
    
    /**
     * Check if user can create role request
     */
    @Transactional(readOnly = true)
    public boolean canUserCreateRoleRequest(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Only Staff can create role requests (handle both "Staff" and "STAFF")
        if (!isStaffRole(user.getRole())) {
            return false;
        }
        
        // Check if user already has a pending request
        return !roleRequestRepository.existsByUserAndStatus(user, RoleRequest.RoleRequestStatus.PENDING);
    }
    
    /**
     * Check if the role is Staff (handles both "Staff" and "STAFF")
     */
    private boolean isStaffRole(String role) {
        return AppConstants.ROLE_STAFF.equals(role) || "STAFF".equals(role) || "Staff".equals(role);
    }
    
    /**
     * Check if the role is CEO or Director (handles different case formats)
     */
    private boolean isCeoOrDirectorRole(String role) {
        return AppConstants.ROLE_CEO.equals(role) || "CEO".equals(role) ||
               AppConstants.ROLE_DIRECTOR.equals(role) || "DIRECTOR".equals(role) || "Director".equals(role);
    }
    
    /**
     * Get count of pending role requests
     */
    @Transactional(readOnly = true)
    public long getPendingRoleRequestsCount() {
        return roleRequestRepository.countByStatus(RoleRequest.RoleRequestStatus.PENDING);
    }
    
    /**
     * Get recent role requests (last 7 days)
     */
    @Transactional(readOnly = true)
    public List<RoleRequestDto> getRecentRoleRequests() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return roleRequestRepository.findRecentRequests(sevenDaysAgo)
                .stream()
                .map(RoleRequestDto::new)
                .collect(Collectors.toList());
    }
} 