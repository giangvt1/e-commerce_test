package com.retailonboardpro.config;

import com.retailonboardpro.constant.AppConstants;
import com.retailonboardpro.entity.User;
import com.retailonboardpro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Tạo người dùng mặc định nếu chưa có
        createUserIfNotExists("admin", "Admin User", "admin@retailonboardpro.com", "123", AppConstants.ROLE_CEO);
        createUserIfNotExists("manager", "Manager User", "manager@retailonboardpro.com", "123", AppConstants.ROLE_MANAGER);
        createUserIfNotExists("director", "Director User", "director@retailonboardpro.com", "123", AppConstants.ROLE_DIRECTOR);
        createUserIfNotExists("staff", "Staff User", "staff@retailonboardpro.com", "123", AppConstants.ROLE_STAFF);
    }

    private void createUserIfNotExists(String username, String name, String email, String password, String role) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User();
            user.setUsername(username);
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            userRepository.save(user);
            System.out.println("Đã tạo người dùng: " + username);
        }
    }
} 