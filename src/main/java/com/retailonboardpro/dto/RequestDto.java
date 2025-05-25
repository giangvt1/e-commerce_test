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
public class RequestDto {
    private Long id;
    
    private UserDto creator;
    
    @NotBlank(message = "Danh mục không được để trống")
    @Size(max = 50, message = "Danh mục không được vượt quá 50 ký tự")
    private String category;
    
    @NotBlank(message = "Lý do không được để trống")
    private String reason;
    
    private String status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 