package com.sasucare.service;

import com.sasucare.model.CartItem;
import com.sasucare.model.Product;
import com.sasucare.model.ShoppingCart;
import com.sasucare.model.User;
import com.sasucare.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ShoppingCartService {

    @Autowired
    private ProductRepository productRepository;

    private ShoppingCart sessionCart;

    /**
     * Get or create a shopping cart for a user
     */
    @Transactional
    public ShoppingCart getOrCreateCart(User user) {
        // If we already have a cart in the service, return it
        if (sessionCart != null) {
            return sessionCart;
        }
        
        // Otherwise create a new one and store it
        sessionCart = new ShoppingCart();
        sessionCart.setUser(user);
        return sessionCart;
    }

    /**
     * Add a product to the cart with validation
     * 
     * @param user The user adding to cart
     * @param productId The product ID to add
     * @param quantity The quantity to add
     * @return Optional error message if there's an issue, empty if successful
     */
    @Transactional
    public Optional<String> addToCart(User user, Long productId, int quantity) {
        ShoppingCart cart = getOrCreateCart(user);
        
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            
            // Refresh product to get latest stock quantity
            productRepository.refresh(product);
            
            // Add product with validation
            return cart.addProduct(product, quantity);
        } catch (IllegalArgumentException e) {
            return Optional.of(e.getMessage());
        }
    }

    /**
     * Update cart item quantity with validation
     * 
     * @param product The product to update
     * @param quantity The new quantity
     * @return Optional error message if there's an issue, empty if successful
     */
    @Transactional
    public Optional<String> updateCartItemQuantity(Product product, int quantity) {
        ShoppingCart cart = getOrCreateCart(null); // In a real implementation, we'd get the cart from session
        
        // Refresh product to get latest stock quantity
        productRepository.refresh(product);
        
        if (quantity <= 0) {
            cart.removeProduct(product.getId());
            return Optional.empty();
        } else {
            return cart.updateQuantity(product.getId(), quantity);
        }
    }

    /**
     * Remove an item from the cart
     */
    @Transactional
    public void removeFromCart(Long productId) {
        ShoppingCart cart = getOrCreateCart(null); // In a real implementation, we'd get the cart from session
        cart.removeProduct(productId);
    }

    /**
     * Clear the shopping cart
     */
    @Transactional
    public void clearCart(User user) {
        ShoppingCart cart = getOrCreateCart(user);
        cart.clear();
    }

    /**
     * Get the number of items in the cart
     */
    public int getCartItemCount(User user) {
        ShoppingCart cart = getOrCreateCart(user);
        return cart.getItemCount();
    }
    
    /**
     * Check for out-of-stock items in the cart
     * 
     * @param user The user whose cart to check
     * @return List of items that are out of stock or have insufficient quantity
     */
    @Transactional(readOnly = true)
    public List<CartItem> getOutOfStockItems(User user) {
        ShoppingCart cart = getOrCreateCart(user);
        
        // Refresh all products in the cart to get latest stock quantities
        for (var item : cart.getItems()) {
            productRepository.refresh(item.getProduct());
        }
        
        return cart.getOutOfStockItems();
    }
    
    /**
     * Remove all out-of-stock items from the cart
     * 
     * @param user The user whose cart to update
     * @return List of removed items
     */
    @Transactional
    public List<CartItem> removeOutOfStockItems(User user) {
        ShoppingCart cart = getOrCreateCart(user);
        
        // Refresh all products in the cart to get latest stock quantities
        for (var item : cart.getItems()) {
            productRepository.refresh(item.getProduct());
        }
        
        return cart.removeOutOfStockItems();
    }
    
    /**
     * Adjust quantities of items in cart to match available stock
     * 
     * @param user The user whose cart to update
     * @return List of items that were adjusted
     */
    @Transactional
    public List<CartItem> adjustQuantitiesToStock(User user) {
        ShoppingCart cart = getOrCreateCart(user);
        
        // Refresh all products in the cart to get latest stock quantities
        for (var item : cart.getItems()) {
            productRepository.refresh(item.getProduct());
        }
        
        return cart.adjustQuantitiesToStock();
    }
    
    /**
     * Get all sellers with products in the cart
     * 
     * @param user The user whose cart to check
     * @return List of sellers
     */
    public List<User> getCartSellers(User user) {
        ShoppingCart cart = getOrCreateCart(user);
        return cart.getSellers();
    }
    
    /**
     * Refresh all items in a user's cart to reflect current stock levels
     * This is especially useful after a concurrent order exception
     * 
     * @param user The user whose cart to refresh
     */
    @Transactional
    public void refreshCartItems(User user) {
        ShoppingCart cart = getOrCreateCart(user);
        
        // Refresh all products in the cart to get latest stock quantities
        for (var item : cart.getItems()) {
            productRepository.refresh(item.getProduct());
        }
        
        // Remove out-of-stock items
        List<CartItem> outOfStockItems = cart.removeOutOfStockItems();
        
        // Adjust quantities to match available stock
        List<CartItem> adjustedItems = cart.adjustQuantitiesToStock();
    }
}
