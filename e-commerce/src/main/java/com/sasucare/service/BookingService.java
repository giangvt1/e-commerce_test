package com.sasucare.service;

import com.sasucare.model.*;
import com.sasucare.repository.BookingItemRepository;
import com.sasucare.repository.BookingRepository;
import com.sasucare.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BookingItemRepository bookingItemRepository;

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * Create a booking from a shopping cart
     */
    @Transactional
    public Booking createBookingFromCart(User customer, Address shippingAddress, String specialInstructions, ShoppingCart cart) {
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create a booking with an empty cart");
        }

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setShippingAddress(shippingAddress);
        booking.setSpecialInstructions(specialInstructions);
        booking.setShippingCost(new BigDecimal("5.00")); // Default shipping cost
        booking.setBookingStatus("PENDING");

        // Group cart items by seller
        if (cart.getItems().stream().map(item -> item.getProduct().getSeller()).distinct().count() > 1) {
            throw new IllegalStateException("All products in a booking must be from the same seller");
        }

        // Set the seller
        User seller = cart.getItems().get(0).getProduct().getSeller();
        booking.setSeller(seller);

        // Calculate the total before saving
        BigDecimal total = BigDecimal.ZERO;
        
        // Create booking items first (don't save yet)
        List<BookingItem> bookingItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            
            BookingItem bookingItem = new BookingItem();
            bookingItem.setBooking(booking);
            bookingItem.setProduct(product);
            bookingItem.setQuantity(cartItem.getQuantity());
            bookingItem.setPrice(product.getPrice());
            bookingItem.setProductName(product.getName());
            bookingItem.setProductDetails(product.getDescription());
            
            // Add to total while we create the items
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            total = total.add(itemTotal);
            
            bookingItems.add(bookingItem);
        }
        
        // Add shipping cost to total
        total = total.add(booking.getShippingCost());
        
        // Set the total amount directly
        booking.setTotalAmount(total);
        
        // Now save the booking with the total amount set
        booking = bookingRepository.save(booking);
        
        // Save all booking items
        bookingItemRepository.saveAll(bookingItems);
        booking.setBookingItems(bookingItems);

        // Clear the shopping cart
        shoppingCartService.clearCart(customer);

        return booking;
    }

    /**
     * Get all bookings for a seller
     */
    public List<Booking> getSellerBookings(User seller) {
        return bookingRepository.findBySeller(seller);
    }

    /**
     * Get seller bookings by status
     */
    public List<Booking> getSellerBookingsByStatus(User seller, String status) {
        return bookingRepository.findBySellerAndBookingStatus(seller, status);
    }

    /**
     * Get all bookings for a customer
     */
    public List<Booking> getCustomerBookings(User customer) {
        return bookingRepository.findByCustomer(customer);
    }

    /**
     * Get customer bookings by status
     */
    public List<Booking> getCustomerBookingsByStatus(User customer, String status) {
        return bookingRepository.findByCustomerAndBookingStatus(customer, status);
    }

    /**
     * Find booking by ID
     */
    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    /**
     * Find booking by booking number
     */
    public Optional<Booking> findByBookingNumber(String bookingNumber) {
        return bookingRepository.findByBookingNumber(bookingNumber);
    }
    
    /**
     * Get all bookings in the system (admin only)
     */
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    /**
     * Confirm a booking (Seller accepts)
     * When a booking is confirmed, decrease the product stock quantity
     */
    @Transactional
    public Booking confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        
        if (!"PENDING".equals(booking.getBookingStatus())) {
            throw new IllegalStateException("Only pending bookings can be confirmed");
        }
        
        // Decrease product stock quantities
        for (BookingItem item : booking.getBookingItems()) {
            Product product = item.getProduct();
            int newQuantity = product.getStockQuantity() - item.getQuantity();
            
            // Validate stock availability
            if (newQuantity < 0) {
                throw new IllegalStateException("Not enough stock for product: " + product.getName());
            }
            
            // Update stock quantity
            product.setStockQuantity(newQuantity);
            productRepository.save(product);
        }
        
        booking.setBookingStatus("CONFIRMED");
        booking.setConfirmedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    /**
     * Reject a booking (Seller rejects)
     * When a booking is rejected, no inventory change is needed as stock was never decreased
     */
    @Transactional
    public Booking rejectBooking(Long bookingId, String rejectionReason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        
        if (!"PENDING".equals(booking.getBookingStatus())) {
            throw new IllegalStateException("Only pending bookings can be rejected");
        }
        
        booking.setBookingStatus("REJECTED");
        booking.setRejectionReason(rejectionReason);
        return bookingRepository.save(booking);
    }

    /**
     * Complete a booking (Seller marks as completed)
     */
    @Transactional
    public Booking completeBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        
        if (!"CONFIRMED".equals(booking.getBookingStatus())) {
            throw new IllegalStateException("Only confirmed bookings can be completed");
        }
        
        booking.setBookingStatus("COMPLETED");
        booking.setCompletedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    /**
     * Cancel a booking (Customer cancels)
     * When a confirmed booking is cancelled, restore the product stock quantity
     */
    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        
        if (!List.of("PENDING", "CONFIRMED").contains(booking.getBookingStatus())) {
            throw new IllegalStateException("Only pending or confirmed bookings can be cancelled");
        }
        
        // If the booking was confirmed, restore stock quantities
        if ("CONFIRMED".equals(booking.getBookingStatus())) {
            for (BookingItem item : booking.getBookingItems()) {
                Product product = item.getProduct();
                int newQuantity = product.getStockQuantity() + item.getQuantity();
                product.setStockQuantity(newQuantity);
                productRepository.save(product);
            }
        }
        
        booking.setBookingStatus("CANCELLED");
        return bookingRepository.save(booking);
    }
}
