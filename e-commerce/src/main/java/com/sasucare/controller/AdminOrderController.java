package com.sasucare.controller;

import com.sasucare.model.Booking;
import com.sasucare.model.User;
import com.sasucare.service.BookingService;
import com.sasucare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewAllOrders(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "sellerId", required = false) Long sellerId,
            Model model, 
            Authentication authentication) {
        
        User admin = userService.findByEmail(authentication.getName());
        if (admin == null) {
            throw new IllegalArgumentException("Admin not found");
        }
        
        // Get all orders (bookings)
        List<Booking> allOrders = bookingService.getAllBookings();
        
        // Filter by status if requested
        if (status != null && !status.isEmpty()) {
            allOrders = allOrders.stream()
                .filter(order -> order.getBookingStatus().equals(status.toUpperCase()))
                .collect(Collectors.toList());
            model.addAttribute("currentStatus", status.toUpperCase());
        } else {
            model.addAttribute("currentStatus", "ALL");
        }
        
        // Filter by seller if requested
        if (sellerId != null) {
            allOrders = allOrders.stream()
                .filter(order -> order.getSeller().getId().equals(sellerId))
                .collect(Collectors.toList());
            
            User seller = userService.findById(sellerId);
            if (seller != null) {
                model.addAttribute("currentSeller", seller);
            }
        }
        
        model.addAttribute("orders", allOrders);
        
        // Get all sellers for the filter dropdown
        List<User> sellers = userService.findSellerUsers();
        model.addAttribute("sellers", sellers);
        
        // Get counts by status for the dashboard
        model.addAttribute("pendingCount", countByStatus(allOrders, "PENDING"));
        model.addAttribute("confirmedCount", countByStatus(allOrders, "CONFIRMED"));
        model.addAttribute("completedCount", countByStatus(allOrders, "COMPLETED"));
        model.addAttribute("cancelledCount", countByStatus(allOrders, "CANCELLED"));
        model.addAttribute("rejectedCount", countByStatus(allOrders, "REJECTED"));
        
        return "admin/orders";
    }
    
    @GetMapping("/{orderId}")
    public String viewOrderDetails(@PathVariable Long orderId, Model model, Authentication authentication) {
        User admin = userService.findByEmail(authentication.getName());
        if (admin == null) {
            throw new IllegalArgumentException("Admin not found");
        }
        
        Optional<Booking> orderOpt = bookingService.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new IllegalArgumentException("Order not found");
        }
        Booking order = orderOpt.get();
        
        model.addAttribute("order", order);
        return "admin/order-details";
    }
    
    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(
            @PathVariable Long orderId,
            Authentication authentication, 
            RedirectAttributes redirectAttributes) {
        
        User admin = userService.findByEmail(authentication.getName());
        if (admin == null) {
            throw new IllegalArgumentException("Admin not found");
        }
        
        Optional<Booking> orderOpt = bookingService.findById(orderId);
        if (!orderOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Order not found");
            return "redirect:/admin/orders";
        }
        
        try {
            bookingService.cancelBooking(orderId);
            redirectAttributes.addFlashAttribute("successMessage", "Order cancelled successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/admin/orders/" + orderId;
    }
    
    /**
     * Helper method to count orders by status
     */
    private int countByStatus(List<Booking> orders, String status) {
        return (int) orders.stream()
            .filter(order -> order.getBookingStatus().equals(status))
            .count();
    }
}
