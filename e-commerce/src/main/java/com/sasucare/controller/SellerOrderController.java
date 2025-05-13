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

@Controller
@RequestMapping("/seller/orders")
@PreAuthorize("hasRole('SELLER')")
public class SellerOrderController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewSellerOrders(
            @RequestParam(value = "status", required = false) String status,
            Model model, 
            Authentication authentication) {
        
        User seller = userService.findByEmail(authentication.getName());
        if (seller == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        List<Booking> orders;
        if (status != null && !status.isEmpty()) {
            orders = bookingService.getSellerBookingsByStatus(seller, status.toUpperCase());
            model.addAttribute("currentStatus", status.toUpperCase());
        } else {
            orders = bookingService.getSellerBookings(seller);
            model.addAttribute("currentStatus", "ALL");
        }
        
        model.addAttribute("orders", orders);
        model.addAttribute("pendingCount", 
            bookingService.getSellerBookingsByStatus(seller, "PENDING").size());
        
        return "seller/orders";
    }

    @GetMapping("/{orderId}")
    public String viewOrderDetails(@PathVariable Long orderId, Model model, Authentication authentication) {
        User seller = userService.findByEmail(authentication.getName());
        if (seller == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        Optional<Booking> orderOpt = bookingService.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new IllegalArgumentException("Order not found");
        }
        Booking order = orderOpt.get();
        
        // Security check - ensure the order is for this seller
        if (!order.getSeller().getId().equals(seller.getId())) {
            return "redirect:/seller/orders?error=unauthorized";
        }
        
        model.addAttribute("order", order);
        return "seller/order-details";
    }

    @PostMapping("/{orderId}/confirm")
    public String confirmOrder(
            @PathVariable Long orderId, 
            Authentication authentication, 
            RedirectAttributes redirectAttributes) {
        
        User seller = userService.findByEmail(authentication.getName());
        if (seller == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        Optional<Booking> orderOpt = bookingService.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new IllegalArgumentException("Order not found");
        }
        Booking order = orderOpt.get();
        
        // Security check - ensure the order is for this seller
        if (!order.getSeller().getId().equals(seller.getId())) {
            return "redirect:/seller/orders?error=unauthorized";
        }
        
        try {
            bookingService.confirmBooking(orderId);
            redirectAttributes.addFlashAttribute("successMessage", "Order confirmed successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/seller/orders/" + orderId;
    }

    @PostMapping("/{orderId}/reject")
    public String rejectOrder(
            @PathVariable Long orderId,
            @RequestParam("rejectionReason") String rejectionReason,
            Authentication authentication, 
            RedirectAttributes redirectAttributes) {
        
        User seller = userService.findByEmail(authentication.getName());
        if (seller == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        Optional<Booking> orderOpt = bookingService.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new IllegalArgumentException("Order not found");
        }
        Booking order = orderOpt.get();
        
        // Security check - ensure the order is for this seller
        if (!order.getSeller().getId().equals(seller.getId())) {
            return "redirect:/seller/orders?error=unauthorized";
        }
        
        if (rejectionReason == null || rejectionReason.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide a reason for rejection");
            return "redirect:/seller/orders/" + orderId;
        }
        
        try {
            bookingService.rejectBooking(orderId, rejectionReason);
            redirectAttributes.addFlashAttribute("successMessage", "Order rejected successfully");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/seller/orders/" + orderId;
    }

    @PostMapping("/{orderId}/complete")
    public String completeOrder(
            @PathVariable Long orderId, 
            Authentication authentication, 
            RedirectAttributes redirectAttributes) {
        
        User seller = userService.findByEmail(authentication.getName());
        if (seller == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        Optional<Booking> orderOpt = bookingService.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new IllegalArgumentException("Order not found");
        }
        Booking order = orderOpt.get();
        
        // Security check - ensure the order is for this seller
        if (!order.getSeller().getId().equals(seller.getId())) {
            return "redirect:/seller/orders?error=unauthorized";
        }
        
        try {
            bookingService.completeBooking(orderId);
            redirectAttributes.addFlashAttribute("successMessage", "Order marked as completed successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/seller/orders/" + orderId;
    }
}
