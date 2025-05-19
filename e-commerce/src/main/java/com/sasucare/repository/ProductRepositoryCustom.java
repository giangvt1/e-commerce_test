package com.sasucare.repository;

import com.sasucare.model.Product;

/**
 * Custom repository interface for Product entity
 */
public interface ProductRepositoryCustom {
    
    /**
     * Refresh a product entity from the database to get the latest data
     * @param product The product entity to refresh
     */
    void refresh(Product product);
}
