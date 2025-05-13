package com.sasucare.service;

import com.sasucare.model.Product;
import com.sasucare.model.ShoppingCart;
import com.sasucare.model.User;
import com.sasucare.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * Add a product to the cart
     */
    @Transactional
    public void addToCart(User user, Long productId, int quantity) {
        ShoppingCart cart = getOrCreateCart(user);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        
        cart.addProduct(product, quantity);
    }

    /**
     * Update cart item quantity
     */
    @Transactional
    public void updateCartItemQuantity(Product product, int quantity) {
        ShoppingCart cart = getOrCreateCart(null); // In a real implementation, we'd get the cart from session
        
        if (quantity <= 0) {
            cart.removeProduct(product.getId());
        } else {
            cart.updateQuantity(product.getId(), quantity);
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
}
