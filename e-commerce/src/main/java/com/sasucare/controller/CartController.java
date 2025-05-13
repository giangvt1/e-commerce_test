package com.sasucare.controller;

import com.sasucare.model.Product;
import com.sasucare.model.ShoppingCart;
import com.sasucare.model.User;
import com.sasucare.service.ProductService;
import com.sasucare.service.ShoppingCartService;
import com.sasucare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewCart(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        ShoppingCart cart = shoppingCartService.getOrCreateCart(user);
        model.addAttribute("cart", cart);
        model.addAttribute("cartTotal", cart.getTotal());
        
        return "customer/cart";
    }

    @PostMapping("/add")
    public Object addToCart(
            @RequestParam("productId") Long productId,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {
        
        boolean isAjax = "XMLHttpRequest".equals(requestedWith);
        
        if (authentication == null || !authentication.isAuthenticated()) {
            if (isAjax) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "You must be logged in to add items to cart");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            return "redirect:/login";
        }

        User user = userService.findByEmail(authentication.getName());
        if (user == null) {
            if (isAjax) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            return "redirect:/login";
        }

        try {
            // Get product details for notification
            Product product = productService.findById(productId);
            if (product == null) {
                throw new IllegalArgumentException("Product not found");
            }
            
            // Add to cart
            shoppingCartService.addToCart(user, productId, quantity);
            
            // Get updated cart for count
            ShoppingCart cart = shoppingCartService.getOrCreateCart(user);
            
            if (isAjax) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Product added to cart successfully!");
                response.put("cartCount", cart.getItemCount());
                response.put("productName", product.getName());
                response.put("quantity", quantity);
                return ResponseEntity.ok(response);
            } else {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Product added to cart successfully!");
            }
        } catch (Exception e) {
            if (isAjax) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Error adding product to cart: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Error adding product to cart: " + e.getMessage());
            }
        }

        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCartItem(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") int quantity,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            if (quantity <= 0) {
                shoppingCartService.removeFromCart(productId);
                redirectAttributes.addFlashAttribute("successMessage", "Item removed from cart");
            } else {
                Product product = productService.findById(productId);
                if (product == null) {
                    throw new IllegalArgumentException("Product not found");
                }
                shoppingCartService.updateCartItemQuantity(product, quantity);
                redirectAttributes.addFlashAttribute("successMessage", "Cart updated successfully");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error updating cart: " + e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(
            @RequestParam("productId") Long productId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            shoppingCartService.removeFromCart(productId);
            redirectAttributes.addFlashAttribute("successMessage", "Item removed from cart");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error removing item from cart: " + e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        try {
            shoppingCartService.clearCart(user);
            redirectAttributes.addFlashAttribute("successMessage", "Cart cleared successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error clearing cart: " + e.getMessage());
        }

        return "redirect:/cart";
    }
}
