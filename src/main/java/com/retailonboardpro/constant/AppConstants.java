package com.retailonboardpro.constant;

import java.util.Arrays;
import java.util.List;

public class AppConstants {
    // User Roles
    public static final String ROLE_STAFF = "Staff";
    public static final String ROLE_MANAGER = "Manager";
    public static final String ROLE_DIRECTOR = "Director";
    public static final String ROLE_CEO = "CEO";
    
    public static final List<String> ROLES = Arrays.asList(
        ROLE_STAFF, ROLE_MANAGER, ROLE_DIRECTOR, ROLE_CEO
    );
    
    // Request Statuses
    public static final String STATUS_PENDING_MANAGER = "Pending_Manager";
    public static final String STATUS_REJECTED_MANAGER = "Rejected_Manager";
    public static final String STATUS_PENDING_DIRECTOR = "Pending_Director";
    public static final String STATUS_REJECTED_DIRECTOR = "Rejected_Director";
    public static final String STATUS_PENDING_CEO = "Pending_CEO";
    public static final String STATUS_REJECTED_CEO = "Rejected_CEO";
    public static final String STATUS_APPROVED = "Approved";
    
    // Approval Actions
    public static final String ACTION_CREATE = "Create";
    public static final String ACTION_APPROVE = "Approve";
    public static final String ACTION_REJECT = "Reject";
    
    // Request Categories
    public static final List<String> REQUEST_CATEGORIES = Arrays.asList(
        "Store Operations",
        "Customer Service",
        "Product Knowledge",
        "Sales Techniques",
        "Inventory Management",
        "Safety & Security",
        "POS Systems",
        "Company Policies"
    );
} 