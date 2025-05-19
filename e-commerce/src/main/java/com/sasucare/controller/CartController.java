package com.sasucare.controller;

import com.sasucare.model.CartItem;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public String viewCart(Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        ShoppingCart cart = shoppingCartService.getOrCreateCart(user);
        
        // Check for out-of-stock items
        List<CartItem> outOfStockItems = shoppingCartService.getOutOfStockItems(user);
        if (!outOfStockItems.isEmpty()) {
            // Add warning message about out-of-stock items
            StringBuilder warningMessage = new StringBuilder("Some items in your cart are no longer available: ");
            for (int i = 0; i < outOfStockItems.size(); i++) {
                CartItem item = outOfStockItems.get(i);
                warningMessage.append(item.getProduct().getName());
                if (i < outOfStockItems.size() - 1) {
                    warningMessage.append(", ");
                }
            }
            model.addAttribute("warningMessage", warningMessage.toString());
            model.addAttribute("outOfStockItems", outOfStockItems);
        }
        
        // Add cart items and total to model
        model.addAttribute("cart", cart);
        model.addAttribute("cartTotal", cart.getTotal());
        
        // Group items by seller for display
        Map<User, List<CartItem>> itemsBySeller = cart.getItems().stream()
                .collect(Collectors.groupingBy(item -> item.getProduct().getSeller()));
        model.addAttribute("itemsBySeller", itemsBySeller);
        
        // Add flag for multiple sellers
        boolean hasMultipleSellers = itemsBySeller.size() > 1;
        model.addAttribute("hasMultipleSellers", hasMultipleSellers);
        
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
            
            // Add to cart with validation
            Optional<String> validationError = shoppingCartService.addToCart(user, productId, quantity);
            
            // Handle validation errors
            if (validationError.isPresent()) {
                if (isAjax) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", validationError.get());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", validationError.get());
                    return "redirect:/product/" + productId;
                }
            }
            
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
                
                // Update with validation
                Optional<String> validationError = shoppingCartService.updateCartItemQuantity(product, quantity);
                
                // Handle validation errors
                if (validationError.isPresent()) {
                    redirectAttributes.addFlashAttribute("errorMessage", validationError.get());
                } else {
                    redirectAttributes.addFlashAttribute("successMessage", "Cart updated successfully");
                }
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
    
    @PostMapping("/remove-out-of-stock")
    public String removeOutOfStockItems(Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }
        
        List<CartItem> removedItems = shoppingCartService.removeOutOfStockItems(user);
        
        if (!removedItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("successMessage", 
                removedItems.size() + " out-of-stock item(s) removed from your cart");
        } else {
            redirectAttributes.addFlashAttribute("infoMessage", "No out-of-stock items found in your cart");
        }
        
        return "redirect:/cart";
    }

    @PostMapping("/adjust-quantities")
    public String adjustQuantitiesToStock(Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }
        
        List<CartItem> adjustedItems = shoppingCartService.adjustQuantitiesToStock(user);
        
        if (!adjustedItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("successMessage", 
                "Quantities adjusted for " + adjustedItems.size() + " item(s) to match available stock");
        } else {
            redirectAttributes.addFlashAttribute("infoMessage", "No quantity adjustments needed");
        }
        
        return "redirect:/cart";
    }
}
