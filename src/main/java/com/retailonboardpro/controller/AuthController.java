package com.retailonboardpro.controller;

import com.retailonboardpro.dto.UserDto;
import com.retailonboardpro.dto.auth.JwtResponse;
import com.retailonboardpro.dto.auth.LoginRequest;
import com.retailonboardpro.dto.auth.RegisterRequest;
import com.retailonboardpro.entity.User;
import com.retailonboardpro.security.jwt.JwtUtils;
import com.retailonboardpro.security.service.UserDetailsImpl;
import com.retailonboardpro.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getRole()));
    }

    /**
     * @deprecated Sử dụng AuthViewController#registerUser thay thế cho form-based registration
     */
    @Deprecated
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // Validate password strength (minimum 8 characters)
        if (registerRequest.getPassword() == null || registerRequest.getPassword().length() < 8) {
            return ResponseEntity.badRequest().body("Lỗi: Mật khẩu phải có ít nhất 8 ký tự!");
        }
        
        // Check email uniqueness
        if (userService.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Lỗi: Email đã được sử dụng!");
        }

        // Check username uniqueness
        if (userService.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Lỗi: Tên đăng nhập đã được sử dụng!");
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword()); // Sẽ được mã hóa trong service
        // Always assign Staff role by default for new registrations
        user.setRole("Staff");

        UserDto result = userService.createUser(user);

        return ResponseEntity.ok(result);
    }
} 