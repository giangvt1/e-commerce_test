package com.sasucare.controller;

import com.sasucare.model.*;
import com.sasucare.service.AddressService;
import com.sasucare.service.BookingService;
import com.sasucare.service.ShoppingCartService;
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
@RequestMapping("/bookings")
public class CustomerBookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private AddressService addressService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    public String viewCustomerBookings(Model model, Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        List<Booking> bookings = bookingService.getCustomerBookings(user);
        model.addAttribute("bookings", bookings);
        
        return "customer/bookings";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{bookingId}")
    public String viewBookingDetails(@PathVariable Long bookingId, Model model, Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        Optional<Booking> bookingOpt = bookingService.findById(bookingId);
        if (!bookingOpt.isPresent()) {
            throw new IllegalArgumentException("Booking not found");
        }
        Booking booking = bookingOpt.get();
        
        // Security check - ensure the booking belongs to this user
        if (!booking.getCustomer().getId().equals(user.getId())) {
            return "redirect:/bookings?error=unauthorized";
        }
        
        model.addAttribute("booking", booking);
        return "customer/booking-details";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/create")
    public String showCreateBookingForm(Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(authentication.getName());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        ShoppingCart cart = shoppingCartService.getOrCreateCart(user);
        if (cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty. Add products before creating a booking.");
            return "redirect:/cart";
        }
        
        // Check if all products are from same seller
        if (cart.getItems().stream().map(item -> item.getProduct().getSeller().getId()).distinct().count() > 1) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Your cart contains products from multiple sellers. Please create separate bookings for each seller.");
            return "redirect:/cart";
        }
        
        List<Address> addresses = addressService.findByUser(user);
        model.addAttribute("addresses", addresses);
        model.addAttribute("cart", cart);
        model.addAttribute("newAddress", new Address());
        
        return "customer/create-booking";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/create")
    public String createBooking(
            @RequestParam(value = "addressId", required = false) Long addressId,
            @RequestParam(value = "street", required = false) String street,
            @RequestParam(value = "streetAddress", required = false) String streetAddress,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "postalCode", required = false) String postalCode,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "specialInstructions", required = false) String specialInstructions,
            Authentication authentication, 
            RedirectAttributes redirectAttributes) {
        
        User user = userService.findByEmail(authentication.getName());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        ShoppingCart cart = shoppingCartService.getOrCreateCart(user);
        if (cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty. Add products before creating a booking.");
            return "redirect:/cart";
        }
        
        Address address;
        
        // Check if user is creating a new address or using an existing one
        if (addressId != null) {
            // Using existing address
            Optional<Address> addressOpt = addressService.findById(addressId);
            if (!addressOpt.isPresent()) {
                throw new IllegalArgumentException("Address not found");
            }
            address = addressOpt.get();
            
            // Security check - ensure the address belongs to this user
            if (!address.getUser().getId().equals(user.getId())) {
                return "redirect:/bookings/create?error=unauthorized";
            }
        } else {
            // Creating a new address
            if (street == null || city == null || state == null || postalCode == null || country == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please provide all required address fields.");
                return "redirect:/bookings/create";
            }
            
            // Create new address
            address = addressService.createAddress(user, street, streetAddress, city, state, postalCode, country);
        }
        
        try {
            Booking booking = bookingService.createBookingFromCart(user, address, specialInstructions, cart);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Booking created successfully! Your booking number is: " + booking.getBookingNumber());
            return "redirect:/bookings/" + booking.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating booking: " + e.getMessage());
            return "redirect:/bookings/create";
        }
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/{bookingId}/cancel")
    public String cancelBooking(@PathVariable Long bookingId, Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(authentication.getName());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        Optional<Booking> bookingOpt = bookingService.findById(bookingId);
        if (!bookingOpt.isPresent()) {
            throw new IllegalArgumentException("Booking not found");
        }
        Booking booking = bookingOpt.get();
        
        // Security check - ensure the booking belongs to this user
        if (!booking.getCustomer().getId().equals(user.getId())) {
            return "redirect:/bookings?error=unauthorized";
        }
        
        try {
            bookingService.cancelBooking(bookingId);
            redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/bookings/" + bookingId;
    }
}
