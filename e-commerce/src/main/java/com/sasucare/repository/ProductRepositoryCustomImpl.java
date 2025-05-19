package com.sasucare.repository;

import com.sasucare.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of custom repository methods for Product entity
 */
@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Refresh a product entity from the database to get the latest data
     * This is useful to ensure we have the most up-to-date stock quantity
     * 
     * @param product The product entity to refresh
     */
    @Override
    @Transactional(readOnly = true)
    public void refresh(Product product) {
        if (product != null && product.getId() != null) {
            // Detach the entity from the persistence context
            entityManager.detach(product);
            
            // Fetch the latest data from the database
            Product refreshedProduct = entityManager.find(Product.class, product.getId());
            
            if (refreshedProduct != null) {
                // Update the stock quantity and other important fields
                product.setStockQuantity(refreshedProduct.getStockQuantity());
                product.setPrice(refreshedProduct.getPrice());
                product.setStatus(refreshedProduct.getStatus());
            }
        }
    }
}
