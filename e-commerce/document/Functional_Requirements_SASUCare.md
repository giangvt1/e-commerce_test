# Functional Requirements Document: SASUCare

## 1. Introduction

SASUCare is a web application that appears to function as an e-commerce and service booking platform. It allows users to register, browse products/services, manage carts, place orders, and make bookings. The system supports different user roles, including Customers, Vendors/Sellers who manage their own shops and offerings, and Administrators who oversee the platform. Key technologies include Spring Boot, Spring Security, JPA (Hibernate), Thymeleaf, and a SQL database.

## 2. Actors

Based on the codebase analysis (User, Role, Product models, and various controllers), the primary actors are:

1.  **Anonymous User / Guest:**
    *   Any user accessing the site without logging in.
2.  **Customer (Authenticated User):**
    *   A registered and logged-in user.
    *   Likely associated with a "ROLE_USER" or similar.
3.  **Vendor / Seller (Authenticated User):**
    *   A registered and logged-in user who owns/manages a shop.
    *   Indicated by the `shopName` attribute in the `User` model and the `seller` association in `Product` and `Booking` models.
    *   Likely associated with a "ROLE_VENDOR" or similar.
4.  **Administrator (Authenticated User):**
    *   A privileged user responsible for system management.
    *   Likely associated with a "ROLE_ADMIN" or similar.

## 3. Database Schema Overview

The following is a high-level overview of the main database entities identified from the `com.sasucare.model` package.

### User Management & Authentication:
*   **`users` Table (from `User.java`)**
    *   `id` (PK, BIGINT, Auto-increment)
    *   `email` (VARCHAR, Unique, Not Null)
    *   `password` (VARCHAR, Not Null) - Expected to be hashed
    *   `first_name` (VARCHAR)
    *   `last_name` (VARCHAR)
    *   `shop_name` (VARCHAR) - For Vendor/Seller users
    *   `is_active` (BOOLEAN, Not Null, Default: true)
    *   `is_verified` (BOOLEAN, Not Null, Default: false)
    *   `verification_token` (VARCHAR)
    *   `img` (VARCHAR) - Profile image URL
    *   `created_at` (TIMESTAMP, Not Null)
    *   `updated_at` (TIMESTAMP, Not Null)
*   **`roles` Table (from `Role.java`)**
    *   `id` (PK, BIGINT, Auto-increment)
    *   `name` (VARCHAR, Unique, Not Null) - e.g., "ROLE_USER", "ROLE_ADMIN", "ROLE_VENDOR"
    *   `description` (VARCHAR)
    *   `created_at` (TIMESTAMP, Not Null)
    *   `updated_at` (TIMESTAMP, Not Null)
*   **`user_roles` Table (Join Table for User-Role Many-to-Many)**
    *   `user_id` (FK to `users.id`)
    *   `role_id` (FK to `roles.id`)
*   **`features` Table (from `Feature.java`)**
    *   `id` (PK, BIGINT, Auto-increment)
    *   `name` (VARCHAR, Unique, Not Null) - User-friendly name of the feature
    *   `description` (VARCHAR, Not Null)
    *   `feature_key` (VARCHAR, Unique, Not Null) - Programmatic key (e.g., "MANAGE_USERS", "CREATE_PRODUCT")
    *   `created_at` (TIMESTAMP, Not Null)
    *   `updated_at` (TIMESTAMP, Not Null)
*   **`role_features` Table (Join Table for Role-Feature Many-to-Many)**
    *   `role_id` (FK to `roles.id`)
    *   `feature_id` (FK to `features.id`)

### Product & Catalog Management:
*   **`products` Table (from `Product.java`)**
    *   `id` (PK, BIGINT, Auto-increment)
    *   `name` (VARCHAR, Not Null)
    *   `description` (TEXT)
    *   `price` (DECIMAL(10,2), Not Null)
    *   `stock_quantity` (INT, Not Null)
    *   `sku` (VARCHAR)
    *   `status` (VARCHAR, Not Null) - e.g., "ACTIVE", "INACTIVE"
    *   `primary_image_url` (VARCHAR)
    *   `version` (BIGINT) - For optimistic locking
    *   `seller_id` (FK to `users.id`, Not Null)
    *   `category_id` (FK to `categories.id`)
    *   `created_at` (TIMESTAMP, Not Null)
    *   `updated_at` (TIMESTAMP, Not Null)
*   **`categories` Table (from `Category.java`)**
    *   `id` (PK, BIGINT, Auto-increment)
    *   `name` (VARCHAR, Unique, Not Null)
    *   `description` (VARCHAR)
    *   `created_at` (TIMESTAMP, Not Null)
    *   `updated_at` (TIMESTAMP, Not Null)
*   **`product_images` Table (from `ProductImage.java`)**
    *   `id` (PK, BIGINT, Auto-increment)
    *   `product_id` (FK to `products.id`, Not Null)
    *   `image_url` (VARCHAR)
    *   `is_primary` (BOOLEAN, Not Null, Default: false)
    *   `created_at` (TIMESTAMP, Not Null)
    *   `updated_at` (TIMESTAMP, Not Null)

### Order Management:
*   **`orders` Table (from `Order.java`)**
    *   `id` (PK, BIGINT, Auto-increment)
    *   `customer_id` (FK to `users.id`, Not Null)
    *   `shipping_address_id` (FK to `addresses.id`, Not Null)
    *   `total_amount` (DECIMAL(10,2), Not Null)
    *   `shipping_cost` (DECIMAL(10,2), Not Null)
    *   `order_status` (VARCHAR, Not Null) - e.g., "PENDING", "PAID", "SHIPPED", "DELIVERED", "CANCELLED"
    *   `created_at` (TIMESTAMP, Not Null)
    *   `updated_at` (TIMESTAMP, Not Null)
*   **`order_items` Table (from `OrderItem.java`)**
    *   `id` (PK, BIGINT, Auto-increment)
    *   `order_id` (FK to `orders.id`, Not Null)
    *   `product_id` (FK to `products.id`, Not Null)
    *   `seller_id` (FK to `users.id`, Not Null)
    *   `quantity` (INT, Not Null)
    *   `price_at_purchase` (DECIMAL(10,2), Not Null)
    *   `created_at` (TIMESTAMP, Not Null)
    *   `updated_at` (TIMESTAMP, Not Null)
*   **`addresses` Table (from `Address.java`)**
    *   `id` (PK, BIGINT, Auto-increment)
    *   `user_id` (FK to `users.id`, Not Null)
    *   `street` (VARCHAR, Not Null)
    *   `street_address` (VARCHAR, Not Null)
    *   `city` (VARCHAR)
    *   `state` (VARCHAR)
    *   `country` (VARCHAR)
    *   `postal_code` (VARCHAR, Not Null)
    *   `is_default` (BOOLEAN, Not Null, Default: false)
    *   `created_at` (TIMESTAMP, Not Null)
    *   `updated_at` (TIMESTAMP, Not Null)
*   **`salecode` Table (from `SaleCode.java`)**
    *   `id` (PK, BIGINT, Auto-increment)
    *   `code` (VARCHAR, Unique, Not Null)
    *   `discount_percent` (DECIMAL(5,2), Not Null)
    *   `start_date` (TIMESTAMP, Not Null)
    *   `end_date` (TIMESTAMP, Not Null)
    *   `quantity` (INT, Not Null) - Number of available uses
    *   `user_id` (FK to `users.id`, Not Null) - The user (admin/vendor) who created the code
    *   `created_at` (TIMESTAMP, Not Null)
    *   `updated_at` (TIMESTAMP, Not Null)
*   **`order_salecode` Table (Join Table for Order-SaleCode Many-to-Many)**
    *   `order_id` (FK to `orders.id`)
    *   `salecode_id` (FK to `salecode.id`)

### Booking Management:
*   **`bookings` Table (from `Booking.java`)**
    *   `id` (PK, BIGINT, Auto-increment)
    *   `booking_number` (VARCHAR, Unique, Not Null)
    *   `customer_id` (FK to `users.id`, Not Null)
    *   `seller_id` (FK to `users.id`, Not Null) - The vendor providing the service/booking
    *   `shipping_address_id` (FK to `addresses.id`, Not Null) - Could be service location
    *   `total_amount` (DECIMAL(10,2), Not Null)
    *   `shipping_cost` (DECIMAL(10,2), Not Null) - Could be service fees
    *   `booking_status` (VARCHAR, Not Null) - e.g., "PENDING", "CONFIRMED", "COMPLETED", "CANCELLED", "REJECTED"
    *   `rejection_reason` (VARCHAR)
    *   `special_instructions` (VARCHAR(500))
    *   `created_at` (TIMESTAMP, Not Null)
    *   `updated_at` (TIMESTAMP, Not Null)
    *   `confirmed_at` (TIMESTAMP)
    *   `completed_at` (TIMESTAMP)
*   **`booking_items` Table (from `BookingItem.java`)**
    *   `id` (PK, BIGINT, Auto-increment)
    *   `booking_id` (FK to `bookings.id`, Not Null)
    *   `product_id` (FK to `products.id`, Not Null) - The product/service being booked
    *   `quantity` (INT, Not Null)
    *   `price` (DECIMAL(10,2), Not Null) - Price at time of booking
    *   `product_name` (VARCHAR, Not Null) - Snapshot of product name
    *   `product_details` (VARCHAR(1000)) - Snapshot of product details

**Note:** `ShoppingCart.java` and `CartItem.java` are session-scoped beans and are not directly persisted as database entities. Cart data is transient until an order is created.

## 4. Business Flows & Use Case Descriptions

This section outlines the key business flows and use cases for each actor. Controllers like `HomeController`, `AuthController`, `RegistrationController`, `ProductController`, `CartController`, `AccountController`, `CustomerBookingController`, `SellerController`, `SellerOrderController`, `SellerBookingController`, `AdminController`, `AdminOrderController`, and `SearchController` define these interactions.

### 4.1. Common / Public Flows (Anonymous & Authenticated Users)

*   **UC-001: View Homepage**
    *   **Actor:** Anonymous User, Customer, Vendor, Admin
    *   **Description:** User navigates to the homepage. The system displays featured products, categories, promotions, etc.
    *   **Controller(s):** `HomeController`
*   **UC-002: Browse Products/Services**
    *   **Actor:** Anonymous User, Customer, Vendor, Admin
    *   **Description:** User browses products/services by category, searches, or views lists.
    *   **Controller(s):** `ProductController`, `SearchController`
*   **UC-003: View Product/Service Details**
    *   **Actor:** Anonymous User, Customer, Vendor, Admin
    *   **Description:** User selects a product/service to view its details (description, price, images, seller info, stock/availability).
    *   **Controller(s):** `ProductController`
*   **UC-004: Search Products/Services**
    *   **Actor:** Anonymous User, Customer, Vendor, Admin
    *   **Description:** User enters keywords to search for products/services. System displays matching results.
    *   **Controller(s):** `SearchController`

### 4.2. Authentication & Registration Flows

*   **UC-101: User Registration**
    *   **Actor:** Anonymous User
    *   **Description:** User provides necessary details (email, password, name, potentially shop name if registering as vendor) to create an account. System validates input, creates a user record (potentially inactive/unverified), and may send a verification email.
    *   **Controller(s):** `RegistrationController`
    *   **Preconditions:**
        *   User is not logged in.
        *   User has access to a valid email address.
    *   **Main Flow:**
        1.  User navigates to the registration page.
        2.  User fills in the registration form (e.g., first name, last name, email, password, confirm password; optionally `shopName` if a vendor registration option is available).
        3.  User submits the form.
        4.  System validates the input data (e.g., required fields, email format, password strength, password match, email uniqueness).
        5.  System creates a new `User` record in the database with `is_active` potentially set to `false` (or `true` if no email verification) and `is_verified` set to `false`.
        6.  System generates a verification token and stores it with the user record.
        7.  System sends a verification email containing a unique link to the user's provided email address.
        8.  System displays a success message to the user, possibly instructing them to check their email.
    *   **Postconditions (Success):**
        *   A new user account is created in an unverified state.
        *   A verification email is sent to the user.
    *   **Alternative Flows / Error Handling:**
        *   **4a. Validation Error:** If any input validation fails (e.g., email already exists, passwords don't match, required field missing), the system displays an error message next to the relevant field(s) and re-presents the registration form with the user's previous input.
        *   **7a. Email Sending Failed:** If the verification email fails to send, the system might log the error. The user account is still created, and they might be offered an option to resend the verification email later or contact support.
*   **UC-102: User Login**
    *   **Actor:** Anonymous User (intending to become Customer, Vendor, or Admin)
    *   **Description:** User provides email and password. System validates credentials and establishes a session.
    *   **Controller(s):** `AuthController` (likely handles POST for login), Spring Security
    *   **Postcondition:** User is logged in and redirected to their dashboard or homepage.
*   **UC-103: User Logout**
    *   **Actor:** Customer, Vendor, Admin
    *   **Description:** User chooses to log out. System invalidates the session.
    *   **Controller(s):** `AuthController`, Spring Security
    *   **Postcondition:** User is logged out and redirected to the homepage.
*   **UC-104: Email Verification**
    *   **Actor:** Newly Registered User
    *   **Description:** User clicks a verification link sent to their email. System verifies the token and activates the account.
    *   **Controller(s):** `RegistrationController` (or a dedicated verification controller)
*   **UC-105: Forgot/Reset Password (Assumed)**
    *   **Actor:** User (Customer, Vendor, Admin)
    *   **Description:** User requests a password reset. System sends a reset link/code. User follows instructions to set a new password.
    *   **Controller(s):** Likely a dedicated controller or part of `AccountController`/`AuthController`. (This is an assumption as no specific controller was immediately obvious for this).
    *   **Preconditions:**
        *   User has an existing account.
        *   User has forgotten their password.
    *   **Main Flow:**
        1.  User navigates to the "Forgot Password" page.
        2.  User enters their registered email address.
        3.  User submits the request.
        4.  System validates the email format and checks if the email exists in the database.
        5.  System generates a unique password reset token, stores it (with an expiry time) associated with the user account.
        6.  System sends an email to the user with a link containing the reset token.
        7.  User clicks the link in the email.
        8.  System presents a page where the user can enter and confirm their new password.
        9.  User enters and confirms the new password.
        10. System validates the new password (strength, match).
        11. System updates the user's password in the database and invalidates the reset token.
        12. System displays a success message and may redirect the user to the login page.
    *   **Postconditions (Success):**
        *   User's password is changed.
        *   User can log in with the new password.
    *   **Alternative Flows / Error Handling:**
        *   **4a. Email Not Found / Invalid Format:** System displays an error message.
        *   **6a. Email Sending Failed:** System logs the error and may inform the user.
        *   **7a. Invalid/Expired Token:** If the reset link is invalid or expired, the system displays an error message and may prompt the user to request a new reset link.
        *   **10a. Password Validation Failed:** System displays an error message (e.g., passwords don't match, password too weak) and re-presents the password change form.

### 4.3. Customer Flows

*   **UC-201: Manage Account Profile**
    *   **Actor:** Customer
    *   **Description:** Customer views and updates their profile information (name, contact, password, profile image).
    *   **Controller(s):** `AccountController`
*   **UC-202: Manage Addresses**
    *   **Actor:** Customer
    *   **Description:** Customer adds, views, edits, and deletes their shipping addresses. Sets a default address.
    *   **Controller(s):** `AccountController` (or a dedicated `AddressController`)
*   **UC-203: Add Product to Cart**
    *   **Actor:** Customer
    *   **Description:** Customer selects a product and quantity and adds it to their shopping cart. System updates the session-based cart.
    *   **Controller(s):** `CartController`
*   **UC-204: View Shopping Cart**
    *   **Actor:** Customer
    *   **Description:** Customer views the items in their cart, quantities, and total price.
    *   **Controller(s):** `CartController`
*   **UC-205: Update Cart Item Quantity**
    *   **Actor:** Customer
    *   **Description:** Customer changes the quantity of an item in the cart. System updates the cart.
    *   **Controller(s):** `CartController`
*   **UC-206: Remove Product from Cart**
    *   **Actor:** Customer
    *   **Description:** Customer removes an item from their cart. System updates the cart.
    *   **Controller(s):** `CartController`
*   **UC-207: Checkout and Place Order**
    *   **Actor:** Customer
    *   **Description:** Customer proceeds to checkout from the cart. Selects/confirms shipping address, applies sale codes (if any), reviews order, and confirms. System creates an `Order` and `OrderItem` records, potentially processes payment (integration not detailed), and updates stock.
    *   **Controller(s):** `CartController`, potentially an `OrderController` (logic might be split or within `CartController` for checkout)
    *   **Preconditions:**
        *   Customer is logged in.
        *   Customer's shopping cart is not empty.
        *   Customer has at least one valid shipping address (or can add one).
    *   **Main Flow:**
        1.  Customer navigates to the shopping cart page.
        2.  Customer clicks "Proceed to Checkout."
        3.  System displays the checkout page, often a multi-step process:
            a.  Shipping Address: Customer selects an existing address or enters a new one.
            b.  Shipping Method (if applicable): Customer selects a shipping method (details not in current models, assumed fixed or simple).
            c.  Payment Method: Customer selects a payment method (details not in current models, payment processing is external or abstract).
            d.  Order Review: System displays all order details (items, quantities, prices, subtotal, shipping cost, applied discounts, total amount, shipping address). Customer can apply a `SaleCode`.
        4.  Customer reviews the order and clicks "Place Order" (or similar).
        5.  System validates stock for all cart items.
        6.  System (conceptually) processes payment. This step is an abstraction here.
        7.  System creates an `Order` record with status "PENDING" (or "PAID" if payment is successful and synchronous).
        8.  For each item in the cart, system creates an `OrderItem` record, linking it to the Order, Product, and Seller. Records `priceAtPurchase`.
        9.  System decrements `stockQuantity` for each `Product` ordered.
        10. System applies any valid `SaleCode`(s) to the `Order`, potentially updating `totalAmount`.
        11. System clears the `ShoppingCart` from the session.
        12. System displays an order confirmation page with the order number and details.
        13. System may send an order confirmation email to the customer.
    *   **Postcondition:**
        *   Order is placed and stored in the database.
        *   Product stock quantities are updated.
        *   Customer's shopping cart is empty.
        *   Customer receives confirmation.
    *   **Alternative Flows / Error Handling:**
        *   **5a. Item Out of Stock:** If an item becomes out of stock during checkout, the system informs the user, may remove the item from the cart or prevent checkout, and prompts the user to review their cart.
        *   **6a. Payment Failed:** If payment processing fails, the system displays an error message. The order is typically not created, or created with a "PAYMENT_FAILED" status. Cart items remain.
        *   **9a. Stock Update Fails:** System logs the error. May require manual reconciliation.
        *   **10a. Invalid Sale Code:** If a sale code is invalid (expired, incorrect, used up), the system informs the user, and the discount is not applied.
*   **UC-208: View Order History**
    *   **Actor:** Customer
    *   **Description:** Customer views a list of their past and current orders and their statuses.
    *   **Controller(s):** `AccountController` (or a dedicated `OrderController` for customers)
*   **UC-209: View Order Details**
    *   **Actor:** Customer
    *   **Description:** Customer views the details of a specific order (items, prices, shipping, status).
    *   **Controller(s):** `AccountController` (or dedicated `OrderController`)
*   **UC-210: Request Booking for a Service/Product**
    *   **Actor:** Customer
    *   **Description:** Customer selects a bookable product/service, chooses date/time (if applicable), provides details, and submits a booking request.
    *   **Controller(s):** `CustomerBookingController`
    *   **Postcondition:** `Booking` record created with "PENDING" status.
*   **UC-211: View Booking History & Status**
    *   **Actor:** Customer
    *   **Description:** Customer views their past and current bookings and their statuses (Pending, Confirmed, Cancelled, Completed).
    *   **Controller(s):** `CustomerBookingController`
*   **UC-212: Cancel Booking (if allowed by status)**
    *   **Actor:** Customer
    *   **Description:** Customer cancels a pending or confirmed booking. System updates booking status.
    *   **Controller(s):** `CustomerBookingController`

### 4.4. Vendor / Seller Flows

*   **UC-301: Manage Seller Profile / Shop Information**
    *   **Actor:** Vendor
    *   **Description:** Vendor updates their shop name, description, contact details, and other shop-specific settings.
    *   **Controller(s):** `SellerController`, `AccountController`
*   **UC-302: Manage Products (CRUD)**
    *   **Actor:** Vendor
    *   **Description:** Vendor adds new products/services, edits existing ones (details, price, stock, images, category), and deletes products. This use case will focus on **Add Product**.
    *   **Controller(s):** `SellerController` (likely handles product CRUD for vendors)
    *   **Sub-Use Cases:** Add Product, Edit Product, Delete Product, View Product.
    *   **Detailed Flow for Add Product:**
        *   **Preconditions:**
            *   Vendor is logged in and has appropriate permissions (e.g., associated with a "ROLE_VENDOR").
            *   Categories exist in the system (or vendor can create them, though that's a separate UC often for Admins or a shared category management).
        *   **Main Flow (Add Product):**
            1.  Vendor navigates to their "Manage Products" or "Add New Product" screen from the seller dashboard.
            2.  System displays a form for new product details (name, description, price, stock quantity, SKU, status, category selection, primary image upload, additional images).
            3.  Vendor fills in the product details and uploads images.
            4.  Vendor submits the form.
            5.  System validates the input data (e.g., required fields, price format, stock format).
            6.  System saves the `ProductImage`(s) (e.g., using `FileController` logic) and gets their URLs.
            7.  System creates a new `Product` record in the database, associating it with the logged-in Vendor (`seller_id`), selected `Category`, and image URLs.
            8.  System displays a success message and may redirect to the product list or the newly added product's detail page.
        *   **Postconditions (Success - Add Product):**
            *   A new product is created and associated with the vendor.
            *   Product images are saved.
        *   **Alternative Flows / Error Handling (Add Product):**
            *   **5a. Validation Error:** If any input validation fails, the system displays an error message next to the relevant field(s) and re-presents the form with the vendor's previous input.
            *   **6a. Image Upload Failed:** System displays an error message. Product creation might be halted, or the product might be saved without an image (if allowed).
*   **UC-303: View Own Product Listings**
    *   **Actor:** Vendor
    *   **Description:** Vendor views a list of all products/services they offer.
    *   **Controller(s):** `SellerController`
*   **UC-304: Manage Product Images**
    *   **Actor:** Vendor
    *   **Description:** Vendor uploads, updates, and deletes images for their products. Sets a primary image.
    *   **Controller(s):** `SellerController`, `FileController`
*   **UC-305: Manage Orders Received**
    *   **Actor:** Vendor
    *   **Description:** Vendor views orders containing their products. Updates order item statuses (e.g., "PROCESSING", "SHIPPED").
    *   **Controller(s):** `SellerOrderController`
*   **UC-306: View Order Item Details (for their products)**
    *   **Actor:** Vendor
    *   **Description:** Vendor views details of specific order items related to their products.
    *   **Controller(s):** `SellerOrderController`
*   **UC-307: Manage Bookings Received**
    *   **Actor:** Vendor
    *   **Description:** Vendor views incoming booking requests for their services/products.
    *   **Controller(s):** `SellerBookingController`
*   **UC-308: Confirm/Reject Booking Request**
    *   **Actor:** Vendor
    *   **Description:** Vendor reviews a booking request and confirms or rejects it (providing a reason for rejection). System updates booking status and notifies customer.
    *   **Controller(s):** `SellerBookingController`
*   **UC-309: Mark Booking as Completed**
    *   **Actor:** Vendor
    *   **Description:** Vendor marks a confirmed booking as completed after service delivery.
    *   **Controller(s):** `SellerBookingController`
*   **UC-310: View Seller Dashboard/Reports (Assumed)**
    *   **Actor:** Vendor
    *   **Description:** Vendor views sales summaries, popular products, booking statistics, etc.
    *   **Controller(s):** `SellerController`
*   **UC-311: Manage Sale Codes (for their products/shop - if applicable)**
    *   **Actor:** Vendor
    *   **Description:** Vendor creates, views, updates, and deactivates sale codes applicable to their shop or products.
    *   **Controller(s):** `SellerController` or a shared `SaleCodeController`. The `SaleCode` model has a `user_id` which could be the vendor.

### 4.5. Administrator Flows

*   **UC-401: Manage Users (CRUD)**
    *   **Actor:** Admin
    *   **Description:** Admin views, creates, edits (details, roles, active status), and deletes user accounts. This use case will focus on **Create User**.
    *   **Controller(s):** `AdminController`
    *   **Sub-Use Cases:** Create User, View User List, View User Details, Edit User, Delete User.
    *   **Detailed Flow for Create User:**
        *   **Preconditions:**
            *   Admin is logged in and has `MANAGE_ALL_USERS` feature/permission.
            *   Roles are defined in the system.
        *   **Main Flow (Create User):**
            1.  Admin navigates to the "User Management" section and chooses to "Add New User."
            2.  System displays a form for new user details (email, password, first name, last name, shop name (if vendor), roles, active status).
            3.  Admin fills in the user details and assigns roles.
            4.  Admin submits the form.
            5.  System validates the input data (e.g., required fields, email format, email uniqueness).
            6.  System creates a new `User` record. Password should be hashed. `is_verified` might be set to `true` by default for admin-created users, or an option provided.
            7.  System associates the selected `Role`(s) with the new user via the `user_roles` join table.
            8.  System displays a success message and may redirect to the user list or the newly created user's detail page.
        *   **Postconditions (Success - Create User):**
            *   A new user account is created with the specified details and roles.
        *   **Alternative Flows / Error Handling (Create User):**
            *   **5a. Validation Error:** If any input validation fails (e.g., email already exists), the system displays an error message and re-presents the form with the admin's previous input.
*   **UC-402: Manage Roles (CRUD)**
    *   **Actor:** Admin
    *   **Description:** Admin creates, views, edits, and deletes user roles. Assigns features to roles.
    *   **Controller(s):** `AdminController` (or dedicated `RoleController`)
*   **UC-403: Manage Features (CRUD)**
    *   **Actor:** Admin
    *   **Description:** Admin defines and manages system features that can be assigned to roles for authorization.
    *   **Controller(s):** `AdminController` (or dedicated `FeatureController`)
*   **UC-404: Manage Categories (CRUD)**
    *   **Actor:** Admin
    *   **Description:** Admin creates, views, edits, and deletes product/service categories.
    *   **Controller(s):** `AdminController`
*   **UC-405: Manage All Products (View, Edit, Delete - Oversee)**
    *   **Actor:** Admin
    *   **Description:** Admin can view all products on the platform, edit details, and remove inappropriate or problematic listings.
    *   **Controller(s):** `AdminController` (or a dedicated `AdminProductController`)
*   **UC-406: Manage All Orders (View, Update Status - Oversee)**
    *   **Actor:** Admin
    *   **Description:** Admin views all orders in the system, can update order statuses, and handle escalations or issues.
    *   **Controller(s):** `AdminOrderController`
*   **UC-407: Manage All Bookings (View, Update Status - Oversee)**
    *   **Actor:** Admin
    *   **Description:** Admin views all bookings, can update statuses, and manage booking-related issues.
    *   **Controller(s):** `AdminController` (or a dedicated `AdminBookingController`)
*   **UC-408: Manage Platform-Wide Sale Codes**
    *   **Actor:** Admin
    *   **Description:** Admin creates, views, updates, and deactivates platform-wide sale codes.
    *   **Controller(s):** `AdminController` or a shared `SaleCodeController`.
*   **UC-409: View System Reports/Analytics (Assumed)**
    *   **Actor:** Admin
    *   **Description:** Admin views overall platform statistics (sales, user registrations, popular items, etc.).
    *   **Controller(s):** `AdminController`
*   **UC-410: Manage Site Settings (Assumed)**
    *   **Actor:** Admin
    *   **Description:** Admin configures global settings for the application (e.g., payment gateways, shipping options, email templates).
    *   **Controller(s):** `AdminController`

## 5. Screen Authorization (Role-Based Access Control using Features)

Access to screens and functionalities will be controlled by assigning `Feature` entities to `Role` entities. Users are assigned `Role`s. Spring Security, likely with method security annotations (`@PreAuthorize`) or controller-level annotations, will enforce these rules based on the `featureKey` associated with a user's roles.

**Example Screen/Action to Role/Feature Mapping:**

| Screen / Action                      | Feature Key (Example)        | ROLE_CUSTOMER | ROLE_VENDOR | ROLE_ADMIN | Notes                                                                |
| ------------------------------------ | ---------------------------- | ------------- | ----------- | ---------- | -------------------------------------------------------------------- |
| **Public Pages**                     |                              |               |             |            |                                                                      |
| View Homepage                        | - (Public)                   | Yes           | Yes         | Yes        | `HomeController`                                                     |
| Browse Products                      | - (Public)                   | Yes           | Yes         | Yes        | `ProductController`, `SearchController`                              |
| View Product Details                 | - (Public)                   | Yes           | Yes         | Yes        | `ProductController`                                                  |
| **Authentication**                   |                              |               |             |            |                                                                      |
| Register Account                     | - (Public, for Anonymous)    | N/A           | N/A         | N/A        | `RegistrationController`                                               |
| Login                                | - (Public, for Anonymous)    | N/A           | N/A         | N/A        | `AuthController`                                                       |
| Logout                               | - (Authenticated)            | Yes           | Yes         | Yes        | `AuthController`                                                       |
| **Customer Account** (`AccountController`, `CustomerBookingController`) |                              |               |             |            |                                                                      |
| View/Edit Own Profile                | `EDIT_OWN_PROFILE`           | Yes           | Yes         | Yes        | All authenticated users can edit basic profile                       |
| Manage Own Addresses                 | `MANAGE_OWN_ADDRESSES`       | Yes           |             |            |                                                                      |
| View Own Order History               | `VIEW_OWN_ORDERS`            | Yes           |             |            |                                                                      |
| View Own Booking History             | `VIEW_OWN_BOOKINGS`          | Yes           |             |            |                                                                      |
| Place Order (Checkout)               | `PLACE_ORDER`                | Yes           |             |            | `CartController`                                                       |
| Create Booking Request               | `CREATE_BOOKING`             | Yes           |             |            |                                                                      |
| **Vendor Portal** (`SellerController`, `SellerOrderController`, `SellerBookingController`) |                            |               |             |            |                                                                      |
| View Vendor Dashboard                | `VIEW_VENDOR_DASHBOARD`      |               | Yes         |            |                                                                      |
| Manage Own Products (CRUD)           | `MANAGE_OWN_PRODUCTS`        |               | Yes         |            |                                                                      |
| Manage Own Orders                    | `MANAGE_OWN_ORDERS`          |               | Yes         |            |                                                                      |
| Manage Own Bookings                  | `MANAGE_OWN_BOOKINGS`        |               | Yes         |            | (Confirm/Reject/Complete)                                            |
| Manage Shop Profile                  | `MANAGE_SHOP_PROFILE`        |               | Yes         |            |                                                                      |
| **Admin Portal** (`AdminController`, `AdminOrderController`)      |                              |               |             |            |                                                                      |
| Manage All Users                     | `MANAGE_ALL_USERS`           |               |             | Yes        |                                                                      |
| Manage Roles & Features              | `MANAGE_ROLES_FEATURES`      |               |             | Yes        |                                                                      |
| Manage Categories                    | `MANAGE_CATEGORIES`          |               |             | Yes        |                                                                      |
| Manage All Products (Oversee)        | `MANAGE_ALL_PRODUCTS`        |               |             | Yes        |                                                                      |
| Manage All Orders (Oversee)          | `MANAGE_ALL_ORDERS`          |               |             | Yes        |                                                                      |
| Manage All Bookings (Oversee)        | `MANAGE_ALL_BOOKINGS`        |               |             | Yes        |                                                                      |
| Manage Site Settings                 | `MANAGE_SITE_SETTINGS`       |               |             | Yes        |                                                                      |
| Manage Platform Sale Codes           | `MANAGE_PLATFORM_SALECODES`  |               |             | Yes        |                                                                      |

**Authorization Implementation Notes:**
*   Thymeleaf Security Extras (`thymeleaf-extras-springsecurity6`) will be used for conditional rendering in templates based on roles/authorities/features.
*   Spring Security annotations (`@PreAuthorize("hasAuthority('FEATURE_KEY')")` or `@PreAuthorize("hasRole('ROLE_ADMIN') and hasAuthority('FEATURE_KEY')")`) will protect service methods and controller endpoints.
*   The `Feature` entity's `featureKey` should be used as the authority string in Spring Security configurations.
*   A custom `UserDetailsService` will load user details along with their roles and associated features (authorities).

## 6. Screen Flows (Illustrative Examples)

This section describes typical navigation paths or screen flows for key user journeys. It is not exhaustive but aims to illustrate common interactions.

### 6.1. New User Registration and First Purchase

1.  **Homepage (`HomeController`)**: Anonymous user lands on the homepage.
    *   -> Clicks "Register" / "Sign Up".
2.  **Registration Page (`RegistrationController`)**: User fills out the registration form.
    *   -> Submits form.
3.  **Registration Success/Email Verification Prompt**: User sees a message.
    *   -> User checks email, clicks verification link.
4.  **Verification Endpoint (`RegistrationController`)**: System verifies token.
    *   -> Redirects to Login Page or Homepage.
5.  **Login Page (`AuthController`)**: User logs in with new credentials.
    *   -> Redirects to Homepage (as logged-in user) or Account Dashboard.
6.  **Homepage / Product Listing (`HomeController`, `ProductController`)**: User browses products.
    *   -> Clicks on a product.
7.  **Product Detail Page (`ProductController`)**: User views product details.
    *   -> Clicks "Add to Cart".
8.  **Cart Updated (Potentially redirect to Cart Page or stay on Product Page with feedback) (`CartController`)**.
    *   -> User navigates to Cart by clicking "View Cart" / Cart Icon.
9.  **Shopping Cart Page (`CartController`)**: User reviews cart items.
    *   -> Clicks "Proceed to Checkout".
10. **Checkout Process (Multi-step: Address, Shipping, Payment, Review) (`CartController` / `OrderController`)**:
    *   User selects/enters shipping address.
    *   User confirms shipping/payment (details abstracted).
    *   User reviews order summary.
    *   -> Clicks "Place Order".
11. **Order Confirmation Page (`OrderController` / `CartController`)**: Displays order success message and order number.
    *   -> User might navigate to "My Orders" or back to Homepage.

### 6.2. Vendor Adds a New Product

1.  **Login Page (`AuthController`)**: Vendor logs in.
    *   -> Redirects to Vendor Dashboard.
2.  **Vendor Dashboard (`SellerController`)**: Displays overview, links to manage products, orders, etc.
    *   -> Clicks "Manage Products" or "My Products".
3.  **Product List Page (Vendor View) (`SellerController`)**: Shows vendor's existing products.
    *   -> Clicks "Add New Product".
4.  **Add Product Form (`SellerController`)**: Vendor fills in product details (name, description, price, stock, category, images).
    *   -> Clicks "Save Product" / "Submit".
5.  **Product Creation Logic (`SellerService`, `ProductRepository`, `FileController` for images)**: System validates, saves product and images.
    *   -> On success, redirects to Product List Page (Vendor View) or the new Product's Detail Page (Vendor View), with a success message.
    *   -> On failure, re-displays Add Product Form with error messages.

### 6.3. Admin Manages a User

1.  **Login Page (`AuthController`)**: Admin logs in.
    *   -> Redirects to Admin Dashboard.
2.  **Admin Dashboard (`AdminController`)**: Displays overview, links to manage users, products, orders, etc.
    *   -> Clicks "Manage Users".
3.  **User List Page (`AdminController`)**: Shows a list of all users with search/filter options.
    *   -> Admin clicks on a specific user to edit, or clicks "Add New User".
4.  **(If Edit) User Edit Form (`AdminController`)**: Displays user's current details, allows modification of roles, active status, etc.
    *   -> Admin makes changes, clicks "Save User".
5.  **(If Add) Add User Form (`AdminController`)**: Admin fills in new user details.
    *   -> Admin clicks "Create User".
6.  **User Update/Creation Logic (`AdminService`, `UserRepository`, `RoleRepository`)**: System validates and saves user data.
    *   -> On success, redirects to User List Page or the User's Detail Page, with a success message.
    *   -> On failure, re-displays the form with error messages.

This document provides a comprehensive starting point based on the codebase analysis. Further details for each use case (preconditions, postconditions, detailed steps, error handling) would typically be elaborated in a full Software Requirements Specification. 