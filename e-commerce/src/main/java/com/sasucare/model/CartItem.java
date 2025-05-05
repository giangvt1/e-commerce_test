package com.sasucare.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents an item in the shopping cart
 * This is a session-scoped entity and not persisted to database
 */
@Data
@NoArgsConstructor
public class CartItem {
    
    private Product product;
    private int quantity;
    
    public CartItem(Product product, int quantity) {
        this.product = product;
        setQuantity(quantity);
    }
    
    public void setQuantity(int quantity) {
        if (product != null && quantity > product.getStockQuantity()) {
            this.quantity = product.getStockQuantity();
        } else {
            this.quantity = quantity;
        }
    }
    
    public BigDecimal getTotalPrice() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
