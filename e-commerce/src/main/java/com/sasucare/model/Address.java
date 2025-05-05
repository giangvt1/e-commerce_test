package com.sasucare.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "street", nullable = false)
    private String street;
    
    @Column(name = "street_address", nullable = false)
    private String streetAddress;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "state")
    private String state;
    
    @Column(name = "country")
    private String country;
    
    @Column(name = "postal_code", nullable = false)
    private String postalCode;
    
    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "shippingAddress")
    private List<Order> orders = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Helper method to get formatted address
    @Transient
    public String getFormattedAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(street).append(", ");
        sb.append(streetAddress);
        
        if (city != null && !city.isEmpty()) {
            sb.append(", ").append(city);
        }
        
        if (state != null && !state.isEmpty()) {
            sb.append(", ").append(state);
        }
        
        if (country != null && !country.isEmpty()) {
            sb.append(", ").append(country);
        }
        
        sb.append(" ").append(postalCode);
        
        return sb.toString();
    }
}
