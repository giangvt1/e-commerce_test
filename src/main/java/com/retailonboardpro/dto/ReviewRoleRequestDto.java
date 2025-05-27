package com.retailonboardpro.dto;

import com.retailonboardpro.entity.RoleRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for reviewing (approve/reject) a role request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRoleRequestDto {
    
    @NotNull(message = "Hành động không được để trống")
    private RoleRequest.RoleRequestStatus action; // APPROVED or REJECTED
    
    @Size(max = 500, message = "Nhận xét không được vượt quá 500 ký tự")
    private String comments;
} 