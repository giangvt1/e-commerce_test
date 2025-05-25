package com.retailonboardpro.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    
    @NotBlank(message = "Tên không được để trống")
    @Size(max = 100, message = "Tên không được vượt quá 100 ký tự")
    private String name;
    
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(max = 50, message = "Tên đăng nhập không được vượt quá 50 ký tự")
    private String username;
    
    @NotBlank(message = "Email không được để trống")
    @Size(max = 150, message = "Email không được vượt quá 150 ký tự")
    @Email(message = "Email không hợp lệ")
    private String email;
    
    @NotBlank(message = "Vai trò không được để trống")
    @Size(max = 20, message = "Vai trò không được vượt quá 20 ký tự")
    private String role;
} 