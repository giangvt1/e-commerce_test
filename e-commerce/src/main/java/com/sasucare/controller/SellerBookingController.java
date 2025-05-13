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
@RequestMapping("/seller/bookings")
@PreAuthorize("hasAuthority('SELLER')")
public class SellerBookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewSellerBookings(
            @RequestParam(value = "status", required = false) String status,
            Model model, 
            Authentication authentication) {
        
        User seller = userService.findByEmail(authentication.getName());
        if (seller == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        List<Booking> bookings;
        if (status != null && !status.isEmpty()) {
            bookings = bookingService.getSellerBookingsByStatus(seller, status.toUpperCase());
            model.addAttribute("currentStatus", status.toUpperCase());
        } else {
            bookings = bookingService.getSellerBookings(seller);
            model.addAttribute("currentStatus", "ALL");
        }
        
        model.addAttribute("bookings", bookings);
        model.addAttribute("pendingCount", 
            bookingService.getSellerBookingsByStatus(seller, "PENDING").size());
        
        return "seller/bookings";
    }

    @GetMapping("/{bookingId}")
    public String viewBookingDetails(@PathVariable Long bookingId, Model model, Authentication authentication) {
        User seller = userService.findByEmail(authentication.getName());
        if (seller == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        Optional<Booking> bookingOpt = bookingService.findById(bookingId);
        if (!bookingOpt.isPresent()) {
            throw new IllegalArgumentException("Booking not found");
        }
        Booking booking = bookingOpt.get();
        
        // Security check - ensure the booking is for this seller
        if (!booking.getSeller().getId().equals(seller.getId())) {
            return "redirect:/seller/bookings?error=unauthorized";
        }
        
        model.addAttribute("booking", booking);
        return "seller/booking-details";
    }

    @PostMapping("/{bookingId}/confirm")
    public String confirmBooking(
            @PathVariable Long bookingId, 
            Authentication authentication, 
            RedirectAttributes redirectAttributes) {
        
        User seller = userService.findByEmail(authentication.getName());
        if (seller == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        Optional<Booking> bookingOpt = bookingService.findById(bookingId);
        if (!bookingOpt.isPresent()) {
            throw new IllegalArgumentException("Booking not found");
        }
        Booking booking = bookingOpt.get();
        
        // Security check - ensure the booking is for this seller
        if (!booking.getSeller().getId().equals(seller.getId())) {
            return "redirect:/seller/bookings?error=unauthorized";
        }
        
        try {
            bookingService.confirmBooking(bookingId);
            redirectAttributes.addFlashAttribute("successMessage", "Booking confirmed successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/seller/bookings/" + bookingId;
    }

    @PostMapping("/{bookingId}/reject")
    public String rejectBooking(
            @PathVariable Long bookingId,
            @RequestParam("rejectionReason") String rejectionReason,
            Authentication authentication, 
            RedirectAttributes redirectAttributes) {
        
        User seller = userService.findByEmail(authentication.getName());
        if (seller == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        Optional<Booking> bookingOpt = bookingService.findById(bookingId);
        if (!bookingOpt.isPresent()) {
            throw new IllegalArgumentException("Booking not found");
        }
        Booking booking = bookingOpt.get();
        
        // Security check - ensure the booking is for this seller
        if (!booking.getSeller().getId().equals(seller.getId())) {
            return "redirect:/seller/bookings?error=unauthorized";
        }
        
        if (rejectionReason == null || rejectionReason.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide a reason for rejection");
            return "redirect:/seller/bookings/" + bookingId;
        }
        
        try {
            bookingService.rejectBooking(bookingId, rejectionReason);
            redirectAttributes.addFlashAttribute("successMessage", "Booking rejected successfully");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/seller/bookings/" + bookingId;
    }

    @PostMapping("/{bookingId}/complete")
    public String completeBooking(
            @PathVariable Long bookingId, 
            Authentication authentication, 
            RedirectAttributes redirectAttributes) {
        
        User seller = userService.findByEmail(authentication.getName());
        if (seller == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        Optional<Booking> bookingOpt = bookingService.findById(bookingId);
        if (!bookingOpt.isPresent()) {
            throw new IllegalArgumentException("Booking not found");
        }
        Booking booking = bookingOpt.get();
        
        // Security check - ensure the booking is for this seller
        if (!booking.getSeller().getId().equals(seller.getId())) {
            return "redirect:/seller/bookings?error=unauthorized";
        }
        
        try {
            bookingService.completeBooking(bookingId);
            redirectAttributes.addFlashAttribute("successMessage", "Booking marked as completed successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/seller/bookings/" + bookingId;
    }
}
