package com.retailonboardpro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalHistoryDto {
    private Long id;
    
    private Long requestId;
    
    private UserDto approver;
    
    @NotBlank(message = "Hành động không được để trống")
    @Size(max = 20, message = "Hành động không được vượt quá 20 ký tự")
    private String action;
    
    private String comments;
    
    private LocalDateTime actionAt;
} 