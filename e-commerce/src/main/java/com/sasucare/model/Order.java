package com.sasucare.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;
    
    @ManyToOne
    @JoinColumn(name = "shipping_address_id", nullable = false)
    private Address shippingAddress;
    
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "shipping_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal shippingCost;
    
    @Column(name = "order_status", nullable = false)
    private String orderStatus;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    @ManyToMany
    @JoinTable(
        name = "order_salecode",
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "salecode_id")
    )
    private List<SaleCode> saleCodes = new ArrayList<>();
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Helper method to calculate total
    public void calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        
        // Sum all order items
        if (orderItems != null) {
            for (OrderItem item : orderItems) {
                total = total.add(item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }
        
        // Add shipping cost
        if (shippingCost != null) {
            total = total.add(shippingCost);
        }
        
        // Apply discounts from sale codes
        if (saleCodes != null && !saleCodes.isEmpty()) {
            for (SaleCode code : saleCodes) {
                BigDecimal discountAmount = total.multiply(code.getDiscountPercent().divide(BigDecimal.valueOf(100)));
                total = total.subtract(discountAmount);
            }
        }
        
        this.totalAmount = total;
    }
}
