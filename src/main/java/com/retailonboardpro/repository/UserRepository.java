package com.retailonboardpro.repository;

import com.retailonboardpro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    
    // Kiểm tra username tồn tại không phân biệt chữ hoa/thường
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.username) = LOWER(:username)")
    Boolean existsByUsernameIgnoreCase(@Param("username") String username);
    
    Boolean existsByEmail(String email);
} 