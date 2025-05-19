package com.sasucare.model;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ShoppingCart implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private final Map<Long, CartItem> items = new HashMap<>();
    private User user;

    /**
     * Add a product to the cart with stock validation
     * @param product The product to add
     * @param quantityToAdd The quantity to add
     * @return Optional error message if there's an issue, empty if successful
     */
    public Optional<String> addProduct(Product product, int quantityToAdd) {
        // Check if product is in stock
        if (product.getStockQuantity() <= 0) {
            return Optional.of("Sorry, this product is out of stock");
        }
        
        // Check available stock
        if (quantityToAdd <= 0) {
            return Optional.of("Quantity must be greater than zero");
        }
        
        if (items.containsKey(product.getId())) {
            // Update existing item
            CartItem cartItem = items.get(product.getId());
            int newQuantity = cartItem.getQuantity() + quantityToAdd;
            
            // Validate against available stock
            if (newQuantity > product.getStockQuantity()) {
                return Optional.of("Sorry, only " + product.getStockQuantity() + " items available in stock");
            }
            
            cartItem.setQuantity(newQuantity);
        } else {
            // Add new item with stock validation
            if (quantityToAdd > product.getStockQuantity()) {
                return Optional.of("Sorry, only " + product.getStockQuantity() + " items available in stock");
            }
            
            items.put(product.getId(), new CartItem(product, quantityToAdd));
        }
        
        return Optional.empty(); // No errors
    }

    public void removeProduct(Long productId) {
        items.remove(productId);
    }

    /**
     * Update the quantity of a product in the cart with stock validation
     * @param productId The product ID to update
     * @param quantity The new quantity
     * @return Optional error message if there's an issue, empty if successful
     */
    public Optional<String> updateQuantity(Long productId, int quantity) {
        if (items.containsKey(productId)) {
            CartItem cartItem = items.get(productId);
            Product product = cartItem.getProduct();
            
            // Validate against available stock
            if (quantity > product.getStockQuantity()) {
                return Optional.of("Sorry, only " + product.getStockQuantity() + " items available in stock");
            }
            
            cartItem.setQuantity(quantity);
            return Optional.empty(); // No errors
        }
        
        return Optional.of("Product not found in cart");
    }

    public ArrayList<CartItem> getItems() {
        return new ArrayList<>(items.values());
    }
    
    /**
     * Calculate the total price of all items in the cart
     * @return The total price as BigDecimal
     */
    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items.values()) {
            total = total.add(item.getTotalPrice());
        }
        return total;
    }

    public BigDecimal getTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : items.values()) {
            total = total.add(cartItem.getTotalPrice());
        }
        return total;
    }
    
    public int getItemCount() {
        int count = 0;
        for (CartItem item : items.values()) {
            count += item.getQuantity();
        }
        return count;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public void clear() {
        items.clear();
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    /**
     * Get all sellers with products in the cart
     * @return A list of unique sellers
     */
    public List<User> getSellers() {
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        
        return items.values().stream()
                .map(item -> item.getProduct().getSeller())
                .distinct()
                .collect(Collectors.toList());
    }
    
    /**
     * Check for out-of-stock items in the cart
     * @return List of items that are out of stock or have insufficient quantity
     */
    public List<CartItem> getOutOfStockItems() {
        List<CartItem> outOfStockItems = new ArrayList<>();
        
        for (CartItem item : items.values()) {
            Product product = item.getProduct();
            int requestedQuantity = item.getQuantity();
            
            // Check if product is out of stock or has insufficient quantity
            if (product.getStockQuantity() <= 0 || requestedQuantity > product.getStockQuantity()) {
                outOfStockItems.add(item);
            }
        }
        
        return outOfStockItems;
    }
    
    /**
     * Remove all out-of-stock items from the cart
     * @return List of removed items
     */
    public List<CartItem> removeOutOfStockItems() {
        List<CartItem> removedItems = new ArrayList<>();
        List<Long> itemsToRemove = new ArrayList<>();
        
        // First identify items to remove
        for (Map.Entry<Long, CartItem> entry : items.entrySet()) {
            Product product = entry.getValue().getProduct();
            int requestedQuantity = entry.getValue().getQuantity();
            
            if (product.getStockQuantity() <= 0 || requestedQuantity > product.getStockQuantity()) {
                removedItems.add(entry.getValue());
                itemsToRemove.add(entry.getKey());
            }
        }
        
        // Then remove them
        for (Long id : itemsToRemove) {
            items.remove(id);
        }
        
        return removedItems;
    }
    
    /**
     * Adjust quantities of items in cart to match available stock
     * @return List of items that were adjusted
     */
    public List<CartItem> adjustQuantitiesToStock() {
        List<CartItem> adjustedItems = new ArrayList<>();
        
        for (CartItem item : items.values()) {
            Product product = item.getProduct();
            int requestedQuantity = item.getQuantity();
            
            // If product is in stock but quantity exceeds available stock
            if (product.getStockQuantity() > 0 && requestedQuantity > product.getStockQuantity()) {
                item.setQuantity(product.getStockQuantity());
                adjustedItems.add(item);
            }
        }
        
        return adjustedItems;
    }
    
    /**
     * Validate all items in the cart against current stock levels
     * @return Optional error message if there's an issue, empty if all items are valid
     */
    public Optional<String> validateStock() {
        for (CartItem item : items.values()) {
            Product product = item.getProduct();
            if (product.getStockQuantity() <= 0) {
                return Optional.of("Product " + product.getName() + " is out of stock");
            }
            if (product.getStockQuantity() < item.getQuantity()) {
                return Optional.of("Not enough stock for " + product.getName() + 
                                  ". Available: " + product.getStockQuantity() + 
                                  ", Requested: " + item.getQuantity());
            }
        }
        return Optional.empty();
    }
}
