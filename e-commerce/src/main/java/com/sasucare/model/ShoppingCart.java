package com.sasucare.model;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ShoppingCart implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private final Map<Long, CartItem> items = new HashMap<>();

    public void addProduct(Product product, int quantityToAdd) {
        if (items.containsKey(product.getId())) {
            CartItem cartItem = items.get(product.getId());
            int newQuantity = cartItem.getQuantity() + quantityToAdd;
            cartItem.setQuantity(newQuantity); 
        } else {
            int availableQuantity = Math.min(product.getStockQuantity(), quantityToAdd);
            items.put(product.getId(), new CartItem(product, availableQuantity));
        }
    }

    public void removeProduct(Long productId) {
        items.remove(productId);
    }

    public void updateQuantity(Long productId, int quantity) {
        if (items.containsKey(productId)) {
            CartItem cartItem = items.get(productId);
            cartItem.setQuantity(quantity); 
        }
    }

    public ArrayList<CartItem> getItems() {
        return new ArrayList<>(items.values());
    }

    public BigDecimal getTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : items.values()) {
            total = total.add(cartItem.getTotalPrice());
        }
        return total;
    }
    
    public void clear() {
        items.clear();
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    public int getItemCount() {
        return items.size();
    }
}
