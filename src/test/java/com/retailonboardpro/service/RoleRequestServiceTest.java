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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleRequestServiceTest {

    @Mock
    private RoleRequestRepository roleRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApprovalHistoryRepository approvalHistoryRepository;

    @InjectMocks
    private RoleRequestService roleRequestService;

    private User staffUser;
    private User adminUser;
    private RoleRequest roleRequest;
    private CreateRoleRequestDto createDto;
    private ReviewRoleRequestDto reviewDto;

    @BeforeEach
    void setUp() {
        staffUser = new User();
        staffUser.setId(1L);
        staffUser.setUsername("staff");
        staffUser.setRole("Staff");
        staffUser.setEmail("staff@test.com");

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("admin");
        adminUser.setRole("CEO");
        adminUser.setEmail("admin@test.com");

        roleRequest = new RoleRequest();
        roleRequest.setId(1L);
        roleRequest.setUser(staffUser);
        roleRequest.setRequestedRole(AppConstants.Role.Manager);
        roleRequest.setReason("I want to be promoted");
        roleRequest.setStatus(RoleRequest.RoleRequestStatus.PENDING);
        roleRequest.setCreatedAt(LocalDateTime.now());

        createDto = new CreateRoleRequestDto();
        createDto.setRequestedRole(AppConstants.Role.Manager);
        createDto.setReason("I want to be promoted for better career");

        reviewDto = new ReviewRoleRequestDto();
        reviewDto.setAction(RoleRequest.RoleRequestStatus.APPROVED);
        reviewDto.setComments("Good performance");
    }

    @Test
    void testCreateRoleRequest_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(staffUser));
        when(roleRequestRepository.existsByUserAndStatus(staffUser, RoleRequest.RoleRequestStatus.PENDING))
                .thenReturn(false);
        when(roleRequestRepository.save(any(RoleRequest.class))).thenReturn(roleRequest);
        when(approvalHistoryRepository.save(any(ApprovalHistory.class))).thenReturn(new ApprovalHistory());

        // When
        RoleRequestDto result = roleRequestService.createRoleRequest(1L, createDto);

        // Then
        assertNotNull(result);
        assertEquals(roleRequest.getId(), result.getId());
        assertEquals(roleRequest.getRequestedRole(), result.getRequestedRole());
        verify(roleRequestRepository).save(any(RoleRequest.class));
        verify(approvalHistoryRepository).save(any(ApprovalHistory.class));
    }

    @Test
    void testCreateRoleRequest_UserNotStaff_ThrowsException() {
        // Given
        staffUser.setRole("Manager");
        when(userRepository.findById(1L)).thenReturn(Optional.of(staffUser));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> roleRequestService.createRoleRequest(1L, createDto));
        assertEquals("Chỉ nhân viên (Staff) mới có thể yêu cầu thay đổi vai trò", exception.getMessage());
    }

    @Test
    void testCreateRoleRequest_PendingRequestExists_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(staffUser));
        when(roleRequestRepository.existsByUserAndStatus(staffUser, RoleRequest.RoleRequestStatus.PENDING))
                .thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> roleRequestService.createRoleRequest(1L, createDto));
        assertEquals("Bạn đã có một yêu cầu thay đổi vai trò đang chờ xử lý", exception.getMessage());
    }

    @Test
    void testCreateRoleRequest_RequestStaffRole_ThrowsException() {
        // Given
        createDto.setRequestedRole(AppConstants.Role.Staff);
        when(userRepository.findById(1L)).thenReturn(Optional.of(staffUser));
        when(roleRequestRepository.existsByUserAndStatus(staffUser, RoleRequest.RoleRequestStatus.PENDING))
                .thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> roleRequestService.createRoleRequest(1L, createDto));
        assertEquals("Không thể yêu cầu thay đổi về vai trò Staff", exception.getMessage());
    }

    @Test
    void testReviewRoleRequest_Approve_Success() {
        // Given
        when(roleRequestRepository.findById(1L)).thenReturn(Optional.of(roleRequest));
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
        when(roleRequestRepository.save(any(RoleRequest.class))).thenReturn(roleRequest);
        when(userRepository.save(any(User.class))).thenReturn(staffUser);
        when(approvalHistoryRepository.save(any(ApprovalHistory.class))).thenReturn(new ApprovalHistory());

        // When
        RoleRequestDto result = roleRequestService.reviewRoleRequest(1L, 2L, reviewDto);

        // Then
        assertNotNull(result);
        assertEquals(RoleRequest.RoleRequestStatus.APPROVED, result.getStatus());
        verify(userRepository).save(staffUser); // User role should be updated
        verify(roleRequestRepository).save(roleRequest);
        verify(approvalHistoryRepository).save(any(ApprovalHistory.class));
    }

    @Test
    void testReviewRoleRequest_Reject_Success() {
        // Given
        reviewDto.setAction(RoleRequest.RoleRequestStatus.REJECTED);
        when(roleRequestRepository.findById(1L)).thenReturn(Optional.of(roleRequest));
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
        when(roleRequestRepository.save(any(RoleRequest.class))).thenReturn(roleRequest);
        when(approvalHistoryRepository.save(any(ApprovalHistory.class))).thenReturn(new ApprovalHistory());

        // When
        RoleRequestDto result = roleRequestService.reviewRoleRequest(1L, 2L, reviewDto);

        // Then
        assertNotNull(result);
        assertEquals(RoleRequest.RoleRequestStatus.REJECTED, result.getStatus());
        verify(userRepository, never()).save(staffUser); // User role should NOT be updated
        verify(roleRequestRepository).save(roleRequest);
        verify(approvalHistoryRepository).save(any(ApprovalHistory.class));
    }

    @Test
    void testReviewRoleRequest_NotAdmin_ThrowsException() {
        // Given
        adminUser.setRole("Staff");
        when(roleRequestRepository.findById(1L)).thenReturn(Optional.of(roleRequest));
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> roleRequestService.reviewRoleRequest(1L, 2L, reviewDto));
        assertEquals("Chỉ CEO hoặc Director mới có thể phê duyệt yêu cầu thay đổi vai trò", exception.getMessage());
    }

    @Test
    void testReviewRoleRequest_AlreadyProcessed_ThrowsException() {
        // Given
        roleRequest.setStatus(RoleRequest.RoleRequestStatus.APPROVED);
        when(roleRequestRepository.findById(1L)).thenReturn(Optional.of(roleRequest));
        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> roleRequestService.reviewRoleRequest(1L, 2L, reviewDto));
        assertEquals("Yêu cầu này đã được xử lý", exception.getMessage());
    }

    @Test
    void testGetUserRoleRequests_Success() {
        // Given
        List<RoleRequest> requests = Arrays.asList(roleRequest);
        when(roleRequestRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(requests);

        // When
        List<RoleRequestDto> result = roleRequestService.getUserRoleRequests(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(roleRequest.getId(), result.get(0).getId());
    }

    @Test
    void testCanUserCreateRoleRequest_Staff_NoPendingRequest_ReturnsTrue() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(staffUser));
        when(roleRequestRepository.existsByUserAndStatus(staffUser, RoleRequest.RoleRequestStatus.PENDING))
                .thenReturn(false);

        // When
        boolean result = roleRequestService.canUserCreateRoleRequest(1L);

        // Then
        assertTrue(result);
    }

    @Test
    void testCanUserCreateRoleRequest_NotStaff_ReturnsFalse() {
        // Given
        staffUser.setRole("Manager");
        when(userRepository.findById(1L)).thenReturn(Optional.of(staffUser));

        // When
        boolean result = roleRequestService.canUserCreateRoleRequest(1L);

        // Then
        assertFalse(result);
    }

    @Test
    void testCanUserCreateRoleRequest_HasPendingRequest_ReturnsFalse() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(staffUser));
        when(roleRequestRepository.existsByUserAndStatus(staffUser, RoleRequest.RoleRequestStatus.PENDING))
                .thenReturn(true);

        // When
        boolean result = roleRequestService.canUserCreateRoleRequest(1L);

        // Then
        assertFalse(result);
    }

    @Test
    void testGetPendingRoleRequestsCount_Success() {
        // Given
        when(roleRequestRepository.countByStatus(RoleRequest.RoleRequestStatus.PENDING)).thenReturn(5L);

        // When
        long result = roleRequestService.getPendingRoleRequestsCount();

        // Then
        assertEquals(5L, result);
    }

    @Test
    void testGetRecentRoleRequests_Success() {
        // Given
        List<RoleRequest> requests = Arrays.asList(roleRequest);
        when(roleRequestRepository.findRecentRequests(any(LocalDateTime.class))).thenReturn(requests);

        // When
        List<RoleRequestDto> result = roleRequestService.getRecentRoleRequests();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(roleRequestRepository).findRecentRequests(any(LocalDateTime.class));
    }
} 