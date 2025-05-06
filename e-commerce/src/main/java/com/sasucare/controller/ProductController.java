package com.sasucare.controller;

import com.sasucare.model.Product;
import com.sasucare.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for product viewing functionality
 */
@Controller
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    private final ProductService productService;
    
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    /**
     * Display product details
     */
    @GetMapping("/product/{id}")
    public String viewProduct(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Viewing product details for product ID: {}", id);
        
        Product product = productService.findById(id);
        
        if (product == null) {
            logger.error("Product not found with ID: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found");
            return "redirect:/";
        }
        
        // Get current authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isSeller = auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SELLER"));
        
        // Check if product is active or if current user is admin/seller
        if (!"ACTIVE".equals(product.getStatus()) && !isAdmin && !(isSeller && product.getSeller() != null && 
                product.getSeller().getEmail().equals(auth.getName()))) {
            logger.warn("Attempt to view non-active product by non-admin user. ID: {}, Status: {}", id, product.getStatus());
            redirectAttributes.addFlashAttribute("errorMessage", "This product is not currently available");
            return "redirect:/";
        }
        
        model.addAttribute("product", product);
        model.addAttribute("isAdmin", isAdmin);
        
        // Get related products from the same category if available
        if (product.getCategory() != null) {
            model.addAttribute("relatedProducts", 
                productService.getProductsByCategory(product.getCategory().getId()).stream()
                    .filter(p -> !p.getId().equals(product.getId()) && "ACTIVE".equals(p.getStatus()))
                    .limit(4)
                    .toList());
        }
        
        return "product-detail";
    }
}
