package com.sasucare.repository;

import com.sasucare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA will automatically implement basic CRUD operations
    // and methods like findById, save, deleteById, etc.
    
    // Custom query methods can be added here
    User findByEmail(String email);
    User findByVerificationToken(String token);
}
