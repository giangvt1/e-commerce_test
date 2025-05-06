package com.sasucare.repository;

import com.sasucare.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Spring Data JPA will automatically implement basic CRUD operations
    
    // Find products with stock quantity greater than zero
    List<Product> findByStockQuantityGreaterThan(int quantity);
    
    // Find products by name containing the search term (case insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // Find products by seller's email
    List<Product> findBySeller_Email(String email);
    
    // Custom query example
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0 AND p.status = 'ACTIVE' ORDER BY p.name")
    List<Product> findAvailableProducts();
    
    // Find products by status
    List<Product> findByStatus(String status);
    
    // Find product by ID with seller information fully loaded
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.seller WHERE p.id = :id")
    Optional<Product> findByIdWithSeller(Long id);
}
