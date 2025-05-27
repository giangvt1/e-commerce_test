package com.retailonboardpro.entity;

import com.retailonboardpro.constant.AppConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a role change request
 * Users can request to change their role from Staff to Manager, Director, or CEO
 */
@Entity
@Table(name = "role_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * User who submitted the role change request
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * The role being requested (Manager, Director, CEO)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "requested_role", nullable = false)
    private AppConstants.Role requestedRole;
    
    /**
     * Reason for the role change request
     */
    @Column(name = "reason", columnDefinition = "TEXT", nullable = false)
    private String reason;
    
    /**
     * Current status of the request
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RoleRequestStatus status = RoleRequestStatus.PENDING;
    
    /**
     * Admin who approved/rejected the request
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;
    
    /**
     * Admin's comments on the decision
     */
    @Column(name = "admin_comments", columnDefinition = "TEXT")
    private String adminComments;
    
    /**
     * When the request was created
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * When the request was last updated
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * When the request was reviewed (approved/rejected)
     */
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    /**
     * Status enum for role requests
     */
    public enum RoleRequestStatus {
        PENDING("Pending"),
        APPROVED("Approved"),
        REJECTED("Rejected");
        
        private final String displayName;
        
        RoleRequestStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Check if the request can be approved/rejected
     */
    public boolean isPending() {
        return status == RoleRequestStatus.PENDING;
    }
    
    /**
     * Check if the request has been processed
     */
    public boolean isProcessed() {
        return status == RoleRequestStatus.APPROVED || status == RoleRequestStatus.REJECTED;
    }
} 