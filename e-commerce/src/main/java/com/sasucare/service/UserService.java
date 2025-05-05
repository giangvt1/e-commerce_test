package com.sasucare.service;

import com.sasucare.model.User;
import com.sasucare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public User saveUser(User user) {
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    /**
     * Update user profile information without changing password
     * @param user User to update
     * @return Updated user
     */
    @Transactional
    public User updateUser(User user) {
        // Set updated timestamp
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    /**
     * Update user's password
     * @param user User to update
     * @param newPassword New unencoded password
     * @return Updated user
     */
    @Transactional
    public User updatePassword(User user, String newPassword) {
        // Encode the new password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    /**
     * Register a new user with verification token
     */
    @Transactional
    public User registerUser(User user) {
        // Set timestamps
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        user.setUpdatedAt(LocalDateTime.now());
        
        // Generate verification token
        String token = generateVerificationToken();
        user.setVerificationToken(token);
        user.setVerified(false);
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // TODO: Send verification email with token
        // This would typically call an email service like JavaMailSender
        // sendVerificationEmail(savedUser.getEmail(), token);
        
        return savedUser;
    }
    
    /**
     * Verify a user's email using token
     */
    @Transactional
    public boolean verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token);
        
        if (user == null) {
            return false;
        }
        
        // Update user verification status
        user.setVerified(true);
        user.setVerificationToken(null);  // Clear the token after use
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        return true;
    }
    
    /**
     * Generate a random verification token
     */
    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Authenticate user (check credentials)
     */
    public boolean authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }
}
