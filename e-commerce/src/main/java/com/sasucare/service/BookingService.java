package com.sasucare.service;

import com.sasucare.exception.ConcurrentOrderException;
import com.sasucare.model.*;
import com.sasucare.repository.BookingItemRepository;
import com.sasucare.repository.BookingRepository;
import com.sasucare.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * Create bookings from a shopping cart, grouping by seller with optimistic locking for concurrent orders
     * 
     * @param customer The customer creating the booking
     * @param shippingAddress The shipping address for the booking
     * @param specialInstructions Any special instructions for the booking
     * @param cart The shopping cart to create the booking from
     * @return List of created bookings (one per seller)
     * @throws IllegalStateException if the cart is empty or if there are stock issues
     * @throws ConcurrentOrderException if there's a concurrent order that took the stock
     */
    @Transactional
    public List<Booking> createBookingsFromCart(User customer, Address shippingAddress, String specialInstructions, ShoppingCart cart) {
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create a booking with an empty cart");
        }

        // First, refresh all products to get latest stock
        Map<Long, Product> refreshedProducts = new HashMap<>();
        List<CartItem> outOfStockItems = new ArrayList<>();
        
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            // Refresh product to get latest stock
            productRepository.refresh(product);
            refreshedProducts.put(product.getId(), product);
            
            if (product.getStockQuantity() <= 0 || product.getStockQuantity() < item.getQuantity()) {
                outOfStockItems.add(item);
            }
        }
        
        // Remove out-of-stock items from cart
        for (CartItem item : outOfStockItems) {
            cart.removeProduct(item.getProduct().getId());
        }
        
        // Check if cart is now empty
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("All items in your cart are out of stock");
        }

        // Group cart items by seller
        Map<User, List<CartItem>> itemsBySeller = cart.getItems().stream()
                .collect(Collectors.groupingBy(item -> item.getProduct().getSeller()));
        
        List<Booking> bookings = new ArrayList<>();
        List<BookingItem> allBookingItems = new ArrayList<>(); // Track all booking items for stock reduction
        
        // Create a booking for each seller
        for (Map.Entry<User, List<CartItem>> entry : itemsBySeller.entrySet()) {
            User seller = entry.getKey();
            List<CartItem> sellerItems = entry.getValue();
            
            Booking booking = new Booking();
            booking.setCustomer(customer);
            booking.setShippingAddress(shippingAddress);
            booking.setSpecialInstructions(specialInstructions);
            booking.setShippingCost(new BigDecimal("5.00")); // Default shipping cost
            booking.setBookingStatus("PENDING");
            booking.setCreatedAt(LocalDateTime.now());
            booking.setSeller(seller);
            
            // Generate a unique booking number
            String bookingNumber = generateBookingNumber();
            booking.setBookingNumber(bookingNumber);

            // Calculate the total before saving
            BigDecimal total = BigDecimal.ZERO;
            
            // Create booking items first (don't save yet)
            List<BookingItem> bookingItems = new ArrayList<>();
            for (CartItem cartItem : sellerItems) {
                Product product = refreshedProducts.get(cartItem.getProduct().getId());
                
                // Double-check stock availability (defensive programming)
                if (product.getStockQuantity() < cartItem.getQuantity()) {
                    // Skip this item if not enough stock
                    continue;
                }
                
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
                allBookingItems.add(bookingItem);
            }
            
            // Skip this seller if no valid items
            if (bookingItems.isEmpty()) {
                continue;
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
            
            bookings.add(booking);
        }
        
        // If no bookings were created, throw an exception
        if (bookings.isEmpty()) {
            throw new IllegalStateException("Could not create any bookings due to stock issues");
        }
        
        try {
            // Now attempt to reduce stock for all products (with optimistic locking)
            for (BookingItem item : allBookingItems) {
                Product product = item.getProduct();
                int newStock = product.getStockQuantity() - item.getQuantity();
                
                if (newStock < 0) {
                    // This should not happen due to our checks, but just in case
                    throw new ConcurrentOrderException("Product " + product.getName() + " is now out of stock");
                }
                
                product.setStockQuantity(newStock);
                try {
                    productRepository.save(product); // This will trigger optimistic locking if version mismatch
                } catch (OptimisticLockingFailureException e) {
                    // Another transaction updated the product concurrently
                    throw new ConcurrentOrderException("Another order was processed at the same time for product: " + product.getName());
                }
            }
        } catch (ConcurrentOrderException e) {
            // If we catch a concurrent order exception, we need to roll back and throw
            // The @Transactional annotation will handle the rollback
            throw e;
        }

        // Clear the shopping cart only after successful stock updates
        shoppingCartService.clearCart(customer);
        
        return bookings;
    }
    
    /**
     * Generate a unique booking number
     * 
     * @return A unique booking number
     */
    private String generateBookingNumber() {
        return "B" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }
    
    /**
     * Create a single booking from a shopping cart (legacy method for backward compatibility)
     * 
     * @param customer The customer creating the booking
     * @param shippingAddress The shipping address for the booking
     * @param specialInstructions Any special instructions for the booking
     * @param cart The shopping cart to create the booking from
     * @return The created booking (first one if multiple sellers)
     * @throws IllegalStateException if the cart is empty or if there are stock issues
     */
    @Transactional
    public Booking createBookingFromCart(User customer, Address shippingAddress, String specialInstructions, ShoppingCart cart) {
        List<Booking> bookings = createBookingsFromCart(customer, shippingAddress, specialInstructions, cart);
        if (bookings.isEmpty()) {
            throw new IllegalStateException("Failed to create any bookings");
        }
        return bookings.get(0);
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
     * When a booking is cancelled, restore the product stock quantity
     */
    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        
        if (!List.of("PENDING", "CONFIRMED").contains(booking.getBookingStatus())) {
            throw new IllegalStateException("Only pending or confirmed bookings can be cancelled");
        }
        
        // Always restore stock quantities when cancelling a booking
        // This ensures stock is restored even for bookings that were just created
        // but haven't been confirmed yet (stock is already reduced at creation time)
        for (BookingItem item : booking.getBookingItems()) {
            Product product = item.getProduct();
            int newQuantity = product.getStockQuantity() + item.getQuantity();
            product.setStockQuantity(newQuantity);
            productRepository.save(product);
        }
        
        booking.setBookingStatus("CANCELLED");
        booking.setCancelledAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }
}
