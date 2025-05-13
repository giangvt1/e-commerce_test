package com.sasucare.controller;

import com.sasucare.model.Booking;
import com.sasucare.model.Category;
import com.sasucare.model.Product;
import com.sasucare.model.SaleCode;
import com.sasucare.model.User;
import com.sasucare.service.BookingService;
import com.sasucare.service.CategoryService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for admin-specific functionality
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    private final ProductService productService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final BookingService bookingService;
    
    @Autowired
    public AdminController(ProductService productService, UserService userService, 
                         CategoryService categoryService, BookingService bookingService) {
        this.productService = productService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.bookingService = bookingService;
    }
    
    /**
     * Display admin dashboard with overview
     */
    @GetMapping("/dashboard")
    @Secured("ROLE_ADMIN")
    public String dashboard(Model model) {
        // Get the current logged-in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Debug authentication and roles
        logger.info("Current authentication: {}", auth);
        logger.info("User roles: {}", auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        
        User admin = userService.findByEmail(auth.getName());
        
        if (admin == null) {
            logger.error("Admin not found for email: {}", auth.getName());
            return "redirect:/login";
        }
        
        logger.info("Found admin: {}", admin);
        
        // Add admin data to model
        model.addAttribute("admin", admin);
        
        // Get all products for admin view
        List<Product> allProducts = productService.getAllProducts();
        model.addAttribute("products", allProducts);
        
        // Get all sellers
        List<User> sellers = userService.findByRoleName("ROLE_SELLER");
        model.addAttribute("sellers", sellers);
        
        // Get all categories
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        
        // Get all bookings (orders)
        List<Booking> allBookings = bookingService.getAllBookings();
        model.addAttribute("recentOrders", allBookings);
        
        // Add statistics
        model.addAttribute("totalProducts", allProducts.size());
        model.addAttribute("totalSellers", sellers.size());
        model.addAttribute("totalCategories", categories.size());
        model.addAttribute("totalOrders", allBookings.size());
        
        // Count orders by status
        model.addAttribute("pendingOrders", countOrdersByStatus(allBookings, "PENDING"));
        model.addAttribute("confirmedOrders", countOrdersByStatus(allBookings, "CONFIRMED"));
        model.addAttribute("completedOrders", countOrdersByStatus(allBookings, "COMPLETED"));
        model.addAttribute("cancelledOrders", countOrdersByStatus(allBookings, "CANCELLED"));
        
        // Calculate revenue data - only from COMPLETED orders
        double totalRevenue = allBookings.stream()
                .filter(booking -> booking.getTotalAmount() != null)
                .filter(booking -> "COMPLETED".equals(booking.getBookingStatus()))
                .mapToDouble(booking -> booking.getTotalAmount().doubleValue())
                .sum();
        model.addAttribute("totalRevenue", totalRevenue);
        
        // Calculate monthly revenue (completed bookings from current month)
        double monthlyRevenue = allBookings.stream()
                .filter(booking -> booking.getCreatedAt() != null && 
                        booking.getCreatedAt().getMonthValue() == java.time.LocalDate.now().getMonthValue() && 
                        booking.getCreatedAt().getYear() == java.time.LocalDate.now().getYear())
                .filter(booking -> booking.getTotalAmount() != null)
                .filter(booking -> "COMPLETED".equals(booking.getBookingStatus()))
                .mapToDouble(booking -> booking.getTotalAmount().doubleValue())
                .sum();
        model.addAttribute("monthlyRevenue", monthlyRevenue);
        
        return "admin/dashboard";
    }
    
    /**
     * Display all products for admin management
     */
    @GetMapping("/products")
    @Secured("ROLE_ADMIN")
    public String manageProducts(Model model) {
        // Get the current logged-in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User admin = userService.findByEmail(auth.getName());
        
        if (admin == null) {
            logger.error("Admin not found for email: {}", auth.getName());
            return "redirect:/login";
        }
        
        // Add admin to model
        model.addAttribute("admin", admin);
        
        // Get all products
        List<Product> allProducts = productService.getAllProducts();
        model.addAttribute("products", allProducts);
        
        // Get all categories for filtering
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        
        return "admin/products";
    }
    
    /**
     * Display products pending approval
     */
    @GetMapping("/products/pending")
    @Secured("ROLE_ADMIN")
    public String pendingProducts(Model model) {
        // Get the current logged-in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User admin = userService.findByEmail(auth.getName());
        
        if (admin == null) {
            logger.error("Admin not found for email: {}", auth.getName());
            return "redirect:/login";
        }
        
        // Add admin to model
        model.addAttribute("admin", admin);
        
        // Get products pending approval using the new method
        List<Product> pendingProducts = productService.findByStatus("PENDING_APPROVAL");
        
        model.addAttribute("pendingProducts", pendingProducts);
        
        return "admin/pending-products";
    }
    
    /**
     * Approve a product
     */
    @PostMapping("/products/approve/{id}")
    @Secured("ROLE_ADMIN")
    public String approveProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Get the product
            Product product = productService.findById(id);
            
            if (product == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Product not found");
                return "redirect:/admin/products/pending";
            }
            
            logger.info("Admin approving product ID: {}, Current status: {}", id, product.getStatus());
            
            // Update product status to ACTIVE
            product.setStatus("ACTIVE");
            productService.save(product);
            
            // Send notification to seller (would be implemented with a message service)
            // For now, we'll just log this
            logger.info("Notification: Product '{}' by seller '{}' has been approved", 
                      product.getName(), product.getSeller().getEmail());
            
            redirectAttributes.addFlashAttribute("successMessage", "Product approved successfully");
        } catch (Exception e) {
            logger.error("Error approving product: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to approve product: " + e.getMessage());
        }
        
        return "redirect:/admin/products/pending";
    }
    
    /**
     * Reject a product
     */
    @PostMapping("/products/reject/{id}")
    @Secured("ROLE_ADMIN")
    public String rejectProduct(@PathVariable Long id, @RequestParam String reason, RedirectAttributes redirectAttributes) {
        try {
            // Get the product
            Product product = productService.findById(id);
            
            if (product == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Product not found");
                return "redirect:/admin/products/pending";
            }
            
            logger.info("Admin rejecting product ID: {}, Reason: {}", id, reason);
            
            // Update product status to REJECTED
            product.setStatus("REJECTED");
            
            // Store the rejection reason in a description field or as a note
            // For now, append it to the product description as a workaround
            String originalDescription = product.getDescription() != null ? product.getDescription() : "";
            String rejectionNote = "\n\n[ADMIN REJECTION NOTE: " + reason + "]";
            product.setDescription(originalDescription + rejectionNote);
            
            productService.save(product);
            
            // Send notification to seller (would be implemented with a message service)
            // For now, we'll just log this
            logger.info("Notification: Product '{}' by seller '{}' has been rejected. Reason: {}", 
                      product.getName(), product.getSeller().getEmail(), reason);
            
            redirectAttributes.addFlashAttribute("successMessage", "Product rejected successfully");
        } catch (Exception e) {
            logger.error("Error rejecting product: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to reject product: " + e.getMessage());
        }
        
        return "redirect:/admin/products/pending";
    }
    
    /**
     * Display all users (admin, customer, seller) for management
     */
    @GetMapping("/users")
    @Secured("ROLE_ADMIN")
    public String manageUsers(Model model) {
        // Get the current logged-in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User admin = userService.findByEmail(auth.getName());
        
        if (admin == null) {
            logger.error("Admin not found for email: {}", auth.getName());
            return "redirect:/login";
        }

        // Add admin to model
        model.addAttribute("admin", admin);
        
        // Get all users
        List<User> allUsers = userService.findAll();
        model.addAttribute("users", allUsers);
        
        return "admin/users";
    }
    
    /**
     * Display all categories for management
     */
    @GetMapping("/categories")
    @Secured("ROLE_ADMIN")
    public String manageCategories(Model model) {
        // Get the current logged-in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User admin = userService.findByEmail(auth.getName());
        
        if (admin == null) {
            logger.error("Admin not found for email: {}", auth.getName());
            return "redirect:/login";
        }
        
        // Add admin to model
        model.addAttribute("admin", admin);
        
        // Get all categories
        List<Category> allCategories = categoryService.getAllCategories();
        model.addAttribute("categories", allCategories);
        
        // Add a new empty category for the add form
        model.addAttribute("newCategory", new Category());
        
        return "admin/categories";
    }
    
    /**
     * Add a new category
     */
    @PostMapping("/categories/add")
    @Secured("ROLE_ADMIN")
    public String addCategory(@ModelAttribute("newCategory") Category category, RedirectAttributes redirectAttributes) {
        try {
            // Save the new category
            categoryService.save(category);
            
            redirectAttributes.addFlashAttribute("successMessage", "Category added successfully");
        } catch (Exception e) {
            logger.error("Error adding category: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add category: " + e.getMessage());
        }
        
        return "redirect:/admin/categories";
    }
     public SaleCode generateSaleCode() {
        SaleCode saleCode = new SaleCode();
        saleCode.setCode(String.format("%06d", (int)(Math.random() * 999999)));
        return saleCode;
     }
     @GetMapping("/sale-codes")
     public ArrayList<SaleCode> createSaleCode(int numberOfCodes) {
        ArrayList<SaleCode> saleCodes = new ArrayList<>();
        for (int i = 0; i < numberOfCodes; i++) {
            SaleCode saleCode = generateSaleCode();
            saleCodes.add(saleCode);
        }
        return saleCodes;
     }
     
     /**
      * Helper method to count orders by status
      */
     private int countOrdersByStatus(List<Booking> orders, String status) {
         return (int) orders.stream()
             .filter(order -> order.getBookingStatus().equals(status))
             .count();
     }
}
