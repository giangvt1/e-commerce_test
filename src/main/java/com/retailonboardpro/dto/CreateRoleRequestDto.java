package com.retailonboardpro.dto;

import com.retailonboardpro.constant.AppConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new role request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleRequestDto {
    
    @NotNull(message = "Vai trò được yêu cầu không được để trống")
    private AppConstants.Role requestedRole;
    
    @NotBlank(message = "Lý do yêu cầu không được để trống")
    @Size(min = 10, max = 1000, message = "Lý do phải có từ 10 đến 1000 ký tự")
    private String reason;
} 