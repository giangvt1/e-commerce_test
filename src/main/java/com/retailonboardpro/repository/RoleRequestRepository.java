package com.retailonboardpro.repository;

import com.retailonboardpro.entity.RoleRequest;
import com.retailonboardpro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RoleRequest entity
 */
@Repository
public interface RoleRequestRepository extends JpaRepository<RoleRequest, Long> {
    
    /**
     * Find all role requests by user
     */
    List<RoleRequest> findByUserOrderByCreatedAtDesc(User user);
    
    /**
     * Find all role requests by user ID
     */
    List<RoleRequest> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Find all pending role requests
     */
    List<RoleRequest> findByStatusOrderByCreatedAtAsc(RoleRequest.RoleRequestStatus status);
    
    /**
     * Find pending role requests for a specific user
     */
    List<RoleRequest> findByUserAndStatus(User user, RoleRequest.RoleRequestStatus status);
    
    /**
     * Check if user has any pending role requests
     */
    boolean existsByUserAndStatus(User user, RoleRequest.RoleRequestStatus status);
    
    /**
     * Find role request by ID and user (for security)
     */
    Optional<RoleRequest> findByIdAndUser(Long id, User user);
    
    /**
     * Count pending role requests
     */
    long countByStatus(RoleRequest.RoleRequestStatus status);
    
    /**
     * Find all role requests with user details for admin dashboard
     */
    @Query("SELECT rr FROM RoleRequest rr " +
           "JOIN FETCH rr.user u " +
           "WHERE rr.status = :status " +
           "ORDER BY rr.createdAt ASC")
    List<RoleRequest> findPendingRequestsWithUserDetails(@Param("status") RoleRequest.RoleRequestStatus status);
    
    /**
     * Find recent role requests (last 7 days)
     */
    @Query("SELECT rr FROM RoleRequest rr " +
           "JOIN FETCH rr.user u " +
           "WHERE rr.createdAt >= :sevenDaysAgo " +
           "ORDER BY rr.createdAt DESC")
    List<RoleRequest> findRecentRequests(@Param("sevenDaysAgo") java.time.LocalDateTime sevenDaysAgo);
    
    /**
     * Find role requests by status with user details
     */
    @Query("SELECT rr FROM RoleRequest rr " +
           "JOIN FETCH rr.user u " +
           "LEFT JOIN FETCH rr.reviewedBy rb " +
           "WHERE rr.status = :status " +
           "ORDER BY rr.updatedAt DESC")
    List<RoleRequest> findByStatusWithDetails(@Param("status") RoleRequest.RoleRequestStatus status);
} 