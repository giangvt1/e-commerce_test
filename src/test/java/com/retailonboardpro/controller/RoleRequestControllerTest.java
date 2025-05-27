package com.retailonboardpro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retailonboardpro.constant.AppConstants;
import com.retailonboardpro.dto.CreateRoleRequestDto;
import com.retailonboardpro.dto.ReviewRoleRequestDto;
import com.retailonboardpro.dto.RoleRequestDto;
import com.retailonboardpro.entity.RoleRequest;
import com.retailonboardpro.security.service.UserDetailsImpl;
import com.retailonboardpro.service.RoleRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleRequestController.class)
class RoleRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleRequestService roleRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateRoleRequestDto createDto;
    private ReviewRoleRequestDto reviewDto;
    private RoleRequestDto roleRequestDto;
    private UserDetailsImpl staffUser;
    private UserDetailsImpl adminUser;

    @BeforeEach
    void setUp() {
        createDto = new CreateRoleRequestDto();
        createDto.setRequestedRole(AppConstants.Role.Manager);
        createDto.setReason("I want to be promoted for better career prospects");

        reviewDto = new ReviewRoleRequestDto();
        reviewDto.setAction(RoleRequest.RoleRequestStatus.APPROVED);
        reviewDto.setComments("Good performance, approved");

        roleRequestDto = new RoleRequestDto();
        roleRequestDto.setId(1L);
        roleRequestDto.setUserId(1L);
        roleRequestDto.setUserName("John Doe");
        roleRequestDto.setUserEmail("john@test.com");
        roleRequestDto.setCurrentRole("Staff");
        roleRequestDto.setRequestedRole(AppConstants.Role.Manager);
        roleRequestDto.setReason("I want to be promoted");
        roleRequestDto.setStatus(RoleRequest.RoleRequestStatus.PENDING);
        roleRequestDto.setCreatedAt(LocalDateTime.now());

        staffUser = new UserDetailsImpl(1L, "staff", "staff@test.com", "Staff User", "password", "Staff", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_STAFF")));
        adminUser = new UserDetailsImpl(2L, "admin", "admin@test.com", "Admin User", "password", "CEO", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CEO")));
    }

    @Test
    @WithMockUser(roles = "Staff")
    void testCreateRoleRequest_Success() throws Exception {
        // Given
        when(roleRequestService.canUserCreateRoleRequest(1L)).thenReturn(true);
        when(roleRequestService.createRoleRequest(eq(1L), any(CreateRoleRequestDto.class)))
                .thenReturn(roleRequestDto);

        // When & Then
        mockMvc.perform(post("/api/role-requests")
                        .with(user(staffUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Yêu cầu thay đổi vai trò đã được tạo thành công"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.requestedRole.name").value("Manager"));

        verify(roleRequestService).canUserCreateRoleRequest(1L);
        verify(roleRequestService).createRoleRequest(eq(1L), any(CreateRoleRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "Staff")
    void testCreateRoleRequest_CannotCreate() throws Exception {
        // Given
        when(roleRequestService.canUserCreateRoleRequest(1L)).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/role-requests")
                        .with(user(staffUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Bạn không thể tạo yêu cầu thay đổi vai trò lúc này"));

        verify(roleRequestService).canUserCreateRoleRequest(1L);
        verify(roleRequestService, never()).createRoleRequest(anyLong(), any(CreateRoleRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "Manager") // Not Staff
    void testCreateRoleRequest_Forbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/role-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CEO")
    void testGetPendingRoleRequests_Success() throws Exception {
        // Given
        List<RoleRequestDto> requests = Arrays.asList(roleRequestDto);
        when(roleRequestService.getPendingRoleRequests()).thenReturn(requests);

        // When & Then
        mockMvc.perform(get("/api/role-requests/pending")
                        .with(user(adminUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Lấy danh sách yêu cầu chờ xử lý thành công"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1L));

        verify(roleRequestService).getPendingRoleRequests();
    }

    @Test
    @WithMockUser(roles = "Staff") // Not CEO or Director
    void testGetPendingRoleRequests_Forbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/role-requests/pending"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CEO")
    void testReviewRoleRequest_Approve_Success() throws Exception {
        // Given
        roleRequestDto.setStatus(RoleRequest.RoleRequestStatus.APPROVED);
        when(roleRequestService.reviewRoleRequest(eq(1L), eq(2L), any(ReviewRoleRequestDto.class)))
                .thenReturn(roleRequestDto);

        // When & Then
        mockMvc.perform(post("/api/role-requests/1/review")
                        .with(user(adminUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Đã phê duyệt yêu cầu thành công"))
                .andExpect(jsonPath("$.data.status").value("APPROVED"));

        verify(roleRequestService).reviewRoleRequest(eq(1L), eq(2L), any(ReviewRoleRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "CEO")
    void testReviewRoleRequest_Reject_Success() throws Exception {
        // Given
        reviewDto.setAction(RoleRequest.RoleRequestStatus.REJECTED);
        roleRequestDto.setStatus(RoleRequest.RoleRequestStatus.REJECTED);
        when(roleRequestService.reviewRoleRequest(eq(1L), eq(2L), any(ReviewRoleRequestDto.class)))
                .thenReturn(roleRequestDto);

        // When & Then
        mockMvc.perform(post("/api/role-requests/1/review")
                        .with(user(adminUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Đã từ chối yêu cầu thành công"))
                .andExpect(jsonPath("$.data.status").value("REJECTED"));

        verify(roleRequestService).reviewRoleRequest(eq(1L), eq(2L), any(ReviewRoleRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "Staff") // Not CEO or Director
    void testReviewRoleRequest_Forbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/role-requests/1/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "Staff")
    void testGetMyRoleRequests_Success() throws Exception {
        // Given
        List<RoleRequestDto> requests = Arrays.asList(roleRequestDto);
        when(roleRequestService.getUserRoleRequests(1L)).thenReturn(requests);

        // When & Then
        mockMvc.perform(get("/api/role-requests/my-requests")
                        .with(user(staffUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Lấy danh sách yêu cầu thành công"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1L));

        verify(roleRequestService).getUserRoleRequests(1L);
    }

    @Test
    @WithMockUser(roles = "CEO")
    void testGetRoleRequestsByStatus_Success() throws Exception {
        // Given
        List<RoleRequestDto> requests = Arrays.asList(roleRequestDto);
        when(roleRequestService.getRoleRequestsByStatus(RoleRequest.RoleRequestStatus.PENDING))
                .thenReturn(requests);

        // When & Then
        mockMvc.perform(get("/api/role-requests/status/pending")
                        .with(user(adminUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Lấy danh sách yêu cầu theo trạng thái thành công"))
                .andExpect(jsonPath("$.data").isArray());

        verify(roleRequestService).getRoleRequestsByStatus(RoleRequest.RoleRequestStatus.PENDING);
    }

    @Test
    @WithMockUser(roles = "CEO")
    void testGetRoleRequestsByStatus_InvalidStatus() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/role-requests/status/invalid")
                        .with(user(adminUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Trạng thái không hợp lệ: invalid"));
    }

    @Test
    @WithMockUser(roles = "Staff")
    void testCanCreateRoleRequest_Success() throws Exception {
        // Given
        when(roleRequestService.canUserCreateRoleRequest(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/role-requests/can-create")
                        .with(user(staffUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.canCreate").value(true))
                .andExpect(jsonPath("$.data.message").value("Có thể tạo yêu cầu"));

        verify(roleRequestService).canUserCreateRoleRequest(1L);
    }

    @Test
    @WithMockUser(roles = "Manager") // Not Staff
    void testCanCreateRoleRequest_Forbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/role-requests/can-create"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateRoleRequest_InvalidInput() throws Exception {
        // Given - Invalid DTO (reason too short)
        createDto.setReason("short"); // Less than 10 characters

        // When & Then
        mockMvc.perform(post("/api/role-requests")
                        .with(user(staffUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CEO")
    void testReviewRoleRequest_ServiceException() throws Exception {
        // Given
        when(roleRequestService.reviewRoleRequest(eq(1L), eq(2L), any(ReviewRoleRequestDto.class)))
                .thenThrow(new RuntimeException("Yêu cầu này đã được xử lý"));

        // When & Then
        mockMvc.perform(post("/api/role-requests/1/review")
                        .with(user(adminUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Yêu cầu này đã được xử lý"));
    }
} 