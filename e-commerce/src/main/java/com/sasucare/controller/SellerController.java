package com.sasucare.controller;

import com.sasucare.model.Category;
import com.sasucare.model.Product;
import com.sasucare.model.User;
import com.sasucare.service.CategoryService;
import com.sasucare.service.FileStorageService;
import com.sasucare.service.ProductService;
import com.sasucare.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for seller-specific functionality
 */
@Controller
@RequestMapping("/seller")
public class SellerController {

    private static final Logger logger = LoggerFactory.getLogger(SellerController.class);
    
    private final ProductService productService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final FileStorageService fileStorageService;

    @Autowired
    public SellerController(ProductService productService, UserService userService, 
                          CategoryService categoryService, FileStorageService fileStorageService) {
        this.productService = productService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Display seller dashboard with overview of shop
     */
    @GetMapping("/dashboard")
    @Secured("ROLE_SELLER")
    public String dashboard(Model model) {
        // Get the current logged-in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Debug authentication and roles
        logger.info("Current authentication: {}", auth);
        logger.info("User roles: {}", auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        
        User seller = userService.findByEmail(auth.getName());
        
        if (seller == null) {
            logger.error("Seller not found for email: {}", auth.getName());
            return "redirect:/login";
        }
        
        logger.info("Found seller: {}", seller);
        logger.info("Seller roles: {}", seller.getRoles());
        
        // Add seller data to model
        model.addAttribute("seller", seller);
        
        // Get seller's products
        List<Product> products = productService.findBySellerEmail(seller.getEmail());
        model.addAttribute("products", products);
        
        // Get sales metrics
        model.addAttribute("totalProducts", products.size());
        model.addAttribute("activeProducts", products.stream()
                .filter(p -> "ACTIVE".equals(p.getStatus()))
                .count());
        
        // Calculate total inventory value
        double inventoryValue = products.stream()
                .mapToDouble(p -> p.getPrice().doubleValue() * p.getStockQuantity())
                .sum();
        model.addAttribute("inventoryValue", inventoryValue);
        
        return "seller/dashboard";
    }
    
    /**
     * Display product management page
     */
    @GetMapping("/products")
    @Secured("ROLE_SELLER")
    public String products(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User seller = userService.findByEmail(auth.getName());
        
        if (seller == null) {
            logger.error("Seller not found for email: {}", auth.getName());
            return "redirect:/login";
        }
        
        try {
            // Get seller's products
            List<Product> sellerProducts = productService.findBySellerEmail(seller.getEmail());
            
            // Separate products by status
            List<Product> activeProducts = new ArrayList<>();
            List<Product> pendingProducts = new ArrayList<>();
            List<Product> rejectedProducts = new ArrayList<>();
            List<Product> otherProducts = new ArrayList<>();
            
            for (Product product : sellerProducts) {
                switch (product.getStatus()) {
                    case "ACTIVE":
                        activeProducts.add(product);
                        break;
                    case "PENDING_APPROVAL":
                        pendingProducts.add(product);
                        break;
                    case "REJECTED":
                        rejectedProducts.add(product);
                        break;
                    default:
                        otherProducts.add(product);
                }
            }
            
            // Get all categories for the add product form
            List<Category> categories = categoryService.getAllCategories();
            
            model.addAttribute("seller", seller);
            model.addAttribute("products", sellerProducts);
            model.addAttribute("activeProducts", activeProducts);
            model.addAttribute("pendingProducts", pendingProducts);
            model.addAttribute("rejectedProducts", rejectedProducts);
            model.addAttribute("otherProducts", otherProducts);
            model.addAttribute("categories", categories);
            model.addAttribute("newProduct", new Product());
        } catch (Exception e) {
            logger.error("Error fetching seller products: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Failed to load products: " + e.getMessage());
        }
        
        return "seller/products";
    }

    /**
     * Handle new product creation
     */
    @PostMapping("/products/add")
    @Secured("ROLE_SELLER")
    public String addProduct(@ModelAttribute("newProduct") Product product,
                           @RequestParam(value = "productImage", required = false) MultipartFile productImage,
                           RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User seller = userService.findByEmail(auth.getName());
        
        if (seller == null) {
            logger.error("Seller not found for email: {}", auth.getName());
            return "redirect:/login";
        }
        
        try {
            // Validate price to prevent arithmetic overflow
            if (product.getPrice() != null) {
                BigDecimal maxValue = new BigDecimal("99999999.99");
                if (product.getPrice().compareTo(maxValue) > 0) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Price must be less than 100,000,000");
                    return "redirect:/seller/products";
                }
            }
            
            // Set the product's seller
            product.setSeller(seller);
            
            // Set initial status to pending approval
            product.setStatus("PENDING_APPROVAL");
            
            // Set timestamps
            LocalDateTime now = LocalDateTime.now();
            product.setCreatedAt(now);
            product.setUpdatedAt(now);
            
            // Handle file upload
            if (productImage != null && !productImage.isEmpty()) {
                String fileName = fileStorageService.storeFile(productImage);
                String fileUrl = fileStorageService.getFileUrl(fileName);
                product.setImageUrl(fileUrl);
                product.setPrimaryImageUrl(fileUrl);
            }
            
            // If category ID is provided, load the category
            if (product.getCategory() != null && product.getCategory().getId() != null) {
                Category category = categoryService.findById(product.getCategory().getId());
                product.setCategory(category);
            }
            
            // Save the product
            Product savedProduct = productService.save(product);
            
            logger.info("Product added successfully: ID={}, Name={}, Price={}", savedProduct.getId(), savedProduct.getName(), savedProduct.getPrice());
            redirectAttributes.addFlashAttribute("successMessage", "Product added successfully!");
        } catch (Exception e) {
            logger.error("Error adding product: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add product: " + e.getMessage());
        }
        
        return "redirect:/seller/products";
    }
    
    /**
     * Display edit product form
     */
    @GetMapping("/products/edit/{id}")
    @Secured("ROLE_SELLER")
    public String editProductForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User seller = userService.findByEmail(auth.getName());
        
        if (seller == null) {
            return "redirect:/login";
        }
        
        Product product = productService.findById(id);
        
        if (product == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found");
            return "redirect:/seller/products";
        }
        
        // Verify that this product belongs to the current seller
        if (product.getSeller() == null || !product.getSeller().getId().equals(seller.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You do not have permission to edit this product");
            return "redirect:/seller/products";
        }
        
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("pageTitle", "Edit Product");
        
        return "seller/product-edit";
    }
    
    /**
     * Resubmit a rejected product for approval
     */
    @PostMapping("/products/resubmit/{id}")
    @Secured("ROLE_SELLER")
    public String resubmitProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User seller = userService.findByEmail(auth.getName());
        
        if (seller == null) {
            logger.error("Seller not found for email: {}", auth.getName());
            return "redirect:/login";
        }
        
        try {
            // Get the product
            Product product = productService.findById(id);
            
            // Verify product exists and belongs to this seller
            if (product == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Product not found");
                return "redirect:/seller/products";
            }
            
            if (!seller.getId().equals(product.getSeller().getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "You do not have permission to edit this product");
                return "redirect:/seller/products";
            }
            
            // Check if product is in REJECTED status
            if (!"REJECTED".equals(product.getStatus())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Only rejected products can be resubmitted");
                return "redirect:/seller/products";
            }
            
            // Clear any rejection notes by removing from description
            String description = product.getDescription();
            if (description != null && description.contains("[ADMIN REJECTION NOTE:")) {
                description = description.substring(0, description.indexOf("\n\n[ADMIN REJECTION NOTE:"));
                product.setDescription(description);
            }
            
            // Change status to PENDING_APPROVAL
            product.setStatus("PENDING_APPROVAL");
            
            // Update timestamp
            product.setUpdatedAt(LocalDateTime.now());
            
            // Save product
            productService.save(product);
            
            logger.info("Product resubmitted for approval: {}", product.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Product resubmitted for approval successfully");
        } catch (Exception e) {
            logger.error("Error resubmitting product: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to resubmit product: " + e.getMessage());
        }
        
        return "redirect:/seller/products";
    }

    /**
     * Handle product update
     */
    @PostMapping("/products/update/{id}")
    @Secured("ROLE_SELLER")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute Product product,
                                @RequestParam(value = "productImage", required = false) MultipartFile productImage,
                                RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User seller = userService.findByEmail(auth.getName());
        
        if (seller == null) {
            return "redirect:/login";
        }
        
        // Check if product belongs to this seller
        Product existingProduct = productService.findById(id);
        
        if (existingProduct == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found");
            return "redirect:/seller/products";
        }
        
        if (existingProduct.getSeller() == null || !existingProduct.getSeller().getId().equals(seller.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You do not have permission to update this product");
            return "redirect:/seller/products";
        }
        
        try {
            // Validate price to prevent arithmetic overflow
            if (product.getPrice() != null) {
                BigDecimal maxValue = new BigDecimal("99999999.99");
                if (product.getPrice().compareTo(maxValue) > 0) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Price must be less than 100,000,000");
                    return "redirect:/seller/products";
                }
            }
            
            // Update product fields but preserve seller and creation date
            product.setId(id);
            product.setSeller(existingProduct.getSeller());
            product.setCreatedAt(existingProduct.getCreatedAt());
            product.setUpdatedAt(LocalDateTime.now());
            
            // Handle file upload
            if (productImage != null && !productImage.isEmpty()) {
                String fileName = fileStorageService.storeFile(productImage);
                String fileUrl = fileStorageService.getFileUrl(fileName);
                product.setImageUrl(fileUrl);
                product.setPrimaryImageUrl(fileUrl);
            } else {
                // Preserve existing image URL if no new image is uploaded
                product.setImageUrl(existingProduct.getImageUrl());
                product.setPrimaryImageUrl(existingProduct.getPrimaryImageUrl());
            }
            
            // If category ID is provided, load the category
            if (product.getCategory() != null && product.getCategory().getId() != null) {
                Category category = categoryService.findById(product.getCategory().getId());
                product.setCategory(category);
            }
            
            // Save updated product
            productService.save(product);
            
            redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully");
        } catch (Exception e) {
            logger.error("Error updating product: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update product: " + e.getMessage());
        }
        
        return "redirect:/seller/products";
    }

    /**
     * Handle product deletion
     */
    @PostMapping("/products/delete/{id}")
    @Secured("ROLE_SELLER")
    public String deleteProduct(@PathVariable("id") Long id,
                                RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User seller = userService.findByEmail(auth.getName());
        
        if (seller == null) {
            return "redirect:/login";
        }
        
        // Check if product belongs to this seller
        Product existingProduct = productService.findById(id);
        
        if (existingProduct == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found");
            return "redirect:/seller/products";
        }
        
        if (existingProduct.getSeller() == null || !existingProduct.getSeller().getId().equals(seller.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You do not have permission to delete this product");
            return "redirect:/seller/products";
        }
        
        try {
            // Delete product
            productService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting product: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete product: " + e.getMessage());
        }
        
        return "redirect:/seller/products";
    }
    
    /**
     * Display shop settings page
     */
    @GetMapping("/settings")
    @Secured("ROLE_SELLER")
    public String shopSettings(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Debug authentication and roles
        logger.info("Current authentication: {}", auth);
        logger.info("User roles: {}", auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        
        User seller = userService.findByEmail(auth.getName());
        
        if (seller == null) {
            logger.error("Seller not found for email: {}", auth.getName());
            return "redirect:/login";
        }
        
        logger.info("Found seller: {}", seller);
        logger.info("Seller roles: {}", seller.getRoles());
        
        model.addAttribute("seller", seller);
        
        return "seller/settings";
    }
    
    /**
     * Handle shop settings update
     */
    @PostMapping("/settings/update")
    @Secured("ROLE_SELLER")
    public String updateShopSettings(@ModelAttribute("seller") User updatedSeller,
                                      RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Debug authentication and roles
        logger.info("Current authentication: {}", auth);
        logger.info("User roles: {}", auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        
        User seller = userService.findByEmail(auth.getName());
        
        if (seller == null) {
            logger.error("Seller not found for email: {}", auth.getName());
            return "redirect:/login";
        }
        
        logger.info("Found seller: {}", seller);
        logger.info("Seller roles: {}", seller.getRoles());
        
        try {
            // Update seller properties
            seller.setShopName(updatedSeller.getShopName());
            // Update other shop-related properties
            
            // Save the updated seller
            userService.saveUser(seller);
            
            redirectAttributes.addFlashAttribute("success", "Shop settings updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update shop settings: " + e.getMessage());
        }
        
        return "redirect:/seller/settings";
    }
}
