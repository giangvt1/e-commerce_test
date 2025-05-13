package com.sasucare.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Product entity
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "text")
    private String description;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;
    
    @Column(name = "sku")
    private String sku;
    
    @Column(name = "status", nullable = false)
    private String status;
    
    @Column(name = "primary_image_url")
    private String primaryImageUrl;
    
    // imageUrl column removed and consolidated with primaryImageUrl
    
    // Remove database mapping but keep the field for compatibility
    @Transient
    private String badgeText;
    
    @Column(name = "version")
    private Long version;
    
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    @ToString.Exclude
    private User seller;
    
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    @ToString.Exclude
    private Category category;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<ProductImage> images = new ArrayList<>();
    
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
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
    }
    
    // For compatibility with forms
    public String getImageUrl() {
        return primaryImageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        // Set primaryImageUrl directly since imageUrl field was removed
        this.primaryImageUrl = imageUrl;
    }
    
    public String getBadgeText() {
        return badgeText;
    }

    public void setBadgeText(String badgeText) {
        this.badgeText = badgeText;
    }
    
    // Helper method for availability
    @Transient
    public boolean isAvailable() {
        return "ACTIVE".equals(status) && stockQuantity > 0;
    }
}
