package com.sasucare.service;

import com.sasucare.model.Category;
import com.sasucare.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing product categories
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    
    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    /**
     * Find all categories
     * @return List of all categories
     */
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
    
    /**
     * Get all categories - alias for findAll for consistent naming across services
     * @return List of all categories
     */
    public List<Category> getAllCategories() {
        return findAll();
    }
    
    /**
     * Find category by ID
     * @param id Category ID
     * @return Category if found, null otherwise
     */
    public Category findById(Long id) {
        if (id == null) {
            return null;
        }
        
        return categoryRepository.findById(id).orElse(null);
    }
    
    /**
     * Create a new category
     * @param category Category to create
     * @return Created category
     */
    public Category save(Category category) {
        return categoryRepository.save(category);
    }
    
    // All sample data code has been removed - now using database directly
}
