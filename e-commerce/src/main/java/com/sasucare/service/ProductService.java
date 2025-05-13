package com.sasucare.service;

import com.sasucare.model.Product;
import com.sasucare.repository.ProductRepository;
import com.sasucare.util.SampleDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    private final ProductRepository productRepository;
    
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public List<Product> getAvailableProducts() {
        // Use repository to get products from database
        try {
            return productRepository.findAvailableProducts();
        } catch (Exception e) {
            logger.error("Error fetching products from database", e);
            // If there's an error, return an empty list instead of sample data
            return new ArrayList<>();
        }
    }
    
    /**
     * Get products by category ID
     * @param categoryId The category ID to filter by
     * @return List of products in the specified category
     */
    public List<Product> getProductsByCategory(Long categoryId) {
        if (categoryId == null) {
            return getAvailableProducts();
        }
        
        try {
            // Query for products that match the category ID and are active
            return productRepository.findAll().stream()
                    .filter(p -> p.getCategory() != null && 
                           categoryId.equals(p.getCategory().getId()) && 
                           "ACTIVE".equals(p.getStatus()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching products by category", e);
            return new ArrayList<>();
        }
    }

    public Product findById(Long id) {
        try {
            // Use the optimized method that eagerly loads seller information
            return productRepository.findByIdWithSeller(id).orElse(null);
        } catch (Exception e) {
            logger.warn("Error finding product by ID, using sample data fallback", e);
            // Fallback to sample data for development
            return SampleDataProvider.getSampleProducts().stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        }
    }

    public List<Product> findBySellerEmail(String email) {
        try {
            return productRepository.findBySeller_Email(email);
        } catch (Exception e) {
            logger.warn("Error finding products by seller email, using sample data fallback", e);
            // Using sample data fallback
            List<Product> allProducts = SampleDataProvider.getSampleProducts();
            return allProducts.stream()
                .filter(p -> p.getSeller() != null && email.equals(p.getSeller().getEmail()))
                .collect(Collectors.toList());
        }
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    /**
     * Save or update a product
     * @param product Product to save
     * @return Saved product
     */
    public Product save(Product product) {
        try {
            return productRepository.save(product);
        } catch (Exception e) {
            logger.error("Error saving product, using sample data fallback", e);
            // Fallback for development - simply return the product as if it were saved
            // In a real application, this would throw an exception
            if (product.getId() == null) {
                // Simulate auto-generated ID
                product.setId(System.currentTimeMillis());
            }
            return product;
        }
    }
    
    /**
     * Delete a product by ID
     * @param id Product ID to delete
     */
    public void delete(Long id) {
        try {
            productRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error deleting product", e);
            // For development, simply log the error
            // In production, this might throw an exception or require further handling
        }
    }
    
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }
    
    /**
     * Find products by status
     * @param status Status to filter by (e.g., ACTIVE, PENDING_APPROVAL, REJECTED)
     * @return List of products with the specified status
     */
    public List<Product> findByStatus(String status) {
        try {
            return productRepository.findByStatus(status);
        } catch (Exception e) {
            logger.error("Error finding products by status: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    public boolean updateProductQuantity(Long productId, int newQuantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setStockQuantity(newQuantity);
            productRepository.save(product);
            return true;
        }
        return false;
    }
    
    public int getStock(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        return productOpt.map(Product::getStockQuantity).orElse(0);
    }
}
