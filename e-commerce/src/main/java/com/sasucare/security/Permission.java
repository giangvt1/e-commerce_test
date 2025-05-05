package com.sasucare.security;

/**
 * Permission enum representing granular permissions in the application
 * These can be assigned to roles to implement fine-grained authorization
 */
public enum Permission {
    // Product permissions
    PRODUCT_READ("product:read"),
    PRODUCT_CREATE("product:create"),
    PRODUCT_UPDATE("product:update"),
    PRODUCT_DELETE("product:delete"),
    
    // Order permissions
    ORDER_READ("order:read"),
    ORDER_CREATE("order:create"),
    ORDER_UPDATE("order:update"),
    ORDER_CANCEL("order:cancel"),
    ORDER_REFUND("order:refund"),
    
    // User permissions
    USER_READ("user:read"),
    USER_CREATE("user:create"),
    USER_UPDATE("user:update"),
    USER_DELETE("user:delete"),
    
    // Category permissions
    CATEGORY_READ("category:read"),
    CATEGORY_CREATE("category:create"),
    CATEGORY_UPDATE("category:update"),
    CATEGORY_DELETE("category:delete"),
    
    // Review permissions
    REVIEW_READ("review:read"),
    REVIEW_CREATE("review:create"),
    REVIEW_UPDATE("review:update"),
    REVIEW_DELETE("review:delete"),
    REVIEW_MODERATE("review:moderate"),
    
    // Admin permissions
    ADMIN_ACCESS("admin:access"),
    ADMIN_DASHBOARD("admin:dashboard"),
    REPORTS_VIEW("reports:view"),
    
    // Sales permissions
    DISCOUNT_MANAGE("discount:manage"),
    PROMOTIONS_MANAGE("promotions:manage");
    
    private final String permission;
    
    Permission(String permission) {
        this.permission = permission;
    }
    
    public String getPermission() {
        return permission;
    }
}
