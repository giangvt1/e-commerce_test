-- =====================================================
-- SAMPLE DATA FOR RETAILONBOARDPRO DATABASE
-- =====================================================

-- Clear existing data (optional - uncomment if needed)
-- DELETE FROM approval_history;
-- DELETE FROM requests;
-- DELETE FROM users;

-- =====================================================
-- 1. USERS TABLE
-- =====================================================
-- Insert sample users with different roles
-- Note: Passwords are BCrypt encoded for "123"

INSERT INTO users (name, username, email, password, role) VALUES
-- CEO
('John Smith', 'ceo', 'ceo@retailonboardpro.com', 'ssssssss', 'CEO'),

-- Directors
('Sarah Johnson', 'director1', 'sarah.johnson@retailonboardpro.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Director'),
('Michael Brown', 'director2', 'michael.brown@retailonboardpro.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Director'),

-- Managers
('Emily Davis', 'manager1', 'emily.davis@retailonboardpro.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Manager'),
('David Wilson', 'manager2', 'david.wilson@retailonboardpro.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Manager'),
('Lisa Anderson', 'manager3', 'lisa.anderson@retailonboardpro.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Manager'),

-- Staff
('James Miller', 'staff1', 'james.miller@retailonboardpro.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Staff'),
('Jennifer Garcia', 'staff2', 'jennifer.garcia@retailonboardpro.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Staff'),
('Robert Martinez', 'staff3', 'robert.martinez@retailonboardpro.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Staff'),
('Maria Rodriguez', 'staff4', 'maria.rodriguez@retailonboardpro.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Staff'),
('Christopher Lee', 'staff5', 'christopher.lee@retailonboardpro.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Staff'),
('Amanda Taylor', 'staff6', 'amanda.taylor@retailonboardpro.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Staff');

-- =====================================================
-- 2. REQUESTS TABLE
-- =====================================================
-- Insert sample training requests with various statuses

INSERT INTO requests (creator_id, category, reason, status, created_at, updated_at) VALUES
-- Approved requests
(7, 'Store Operations', 'Need training on new store opening procedures and daily operations management. This includes understanding opening/closing checklists, cash handling procedures, and customer flow management during peak hours.', 'Approved', '2024-01-15 09:00:00', '2024-01-18 16:30:00'),

(8, 'Customer Service', 'Request for advanced customer service training to handle difficult customers and complaint resolution. Need to improve communication skills and learn de-escalation techniques for better customer satisfaction.', 'Approved', '2024-01-20 10:30:00', '2024-01-25 14:20:00'),

-- Pending CEO approval
(9, 'Sales Techniques', 'Training needed on consultative selling techniques and upselling strategies. Want to learn how to identify customer needs and recommend appropriate products to increase sales performance.', 'Pending_CEO', '2024-02-01 11:15:00', '2024-02-05 13:45:00'),

-- Pending Director approval
(10, 'Product Knowledge', 'Comprehensive training on new product lines and technical specifications. Need to understand features, benefits, and competitive advantages to better assist customers and increase sales.', 'Pending_Director', '2024-02-10 14:20:00', '2024-02-12 09:30:00'),

(11, 'Inventory Management', 'Training on inventory tracking systems and stock management procedures. Need to learn how to use the new inventory software and understand reorder points and stock rotation principles.', 'Pending_Director', '2024-02-15 08:45:00', '2024-02-16 11:20:00'),

-- Pending Manager approval
(12, 'Safety & Security', 'Safety training for emergency procedures and security protocols. Need to understand evacuation procedures, theft prevention, and how to handle security incidents in the store.', 'Pending_Manager', '2024-02-20 13:30:00', '2024-02-20 13:30:00'),

(7, 'POS Systems', 'Training on the new point-of-sale system features and troubleshooting. Need to learn advanced functions, payment processing, and how to handle system errors during busy periods.', 'Pending_Manager', '2024-02-22 16:00:00', '2024-02-22 16:00:00'),

-- Rejected requests
(8, 'Company Policies', 'Training on updated company policies and procedures manual. Need clarification on new HR policies, dress code updates, and employee conduct guidelines.', 'Rejected_Manager', '2024-02-05 12:00:00', '2024-02-06 10:15:00'),

(9, 'Customer Service', 'Additional customer service training focusing on phone etiquette and email communication. Want to improve written and verbal communication skills with customers.', 'Rejected_Director', '2024-01-25 15:30:00', '2024-01-28 11:45:00'),

-- More recent requests
(10, 'Store Operations', 'Training needed for weekend shift management and team coordination. Need to learn how to manage staff schedules and handle operational issues during weekend hours.', 'Pending_Manager', '2024-02-25 09:15:00', '2024-02-25 09:15:00'),

(11, 'Sales Techniques', 'Cross-selling and bundling techniques training. Want to learn how to identify opportunities for additional sales and create attractive product bundles for customers.', 'Pending_Manager', '2024-02-26 14:45:00', '2024-02-26 14:45:00'),

(12, 'Product Knowledge', 'Seasonal product training for spring/summer collections. Need detailed knowledge about new arrivals, seasonal trends, and how to merchandise seasonal displays effectively.', 'Pending_Manager', '2024-02-27 11:30:00', '2024-02-27 11:30:00');

-- =====================================================
-- 3. APPROVAL_HISTORY TABLE
-- =====================================================
-- Insert approval history for each request

-- Request 1 (Approved) - Full approval chain
INSERT INTO approval_history (request_id, approver_id, action, comments, action_at) VALUES
(1, 7, 'Create', NULL, '2024-01-15 09:00:00'),
(1, 4, 'Approve', 'Good initiative for improving store operations. Approved for next level review.', '2024-01-16 10:30:00'),
(1, 2, 'Approve', 'Excellent training request. This will benefit the entire team. Moving to CEO for final approval.', '2024-01-17 14:15:00'),
(1, 1, 'Approve', 'Approved. This training aligns with our operational excellence goals. Please proceed with scheduling.', '2024-01-18 16:30:00');

-- Request 2 (Approved) - Full approval chain
INSERT INTO approval_history (request_id, approver_id, action, comments, action_at) VALUES
(2, 8, 'Create', NULL, '2024-01-20 10:30:00'),
(2, 5, 'Approve', 'Customer service improvement is always a priority. Approved.', '2024-01-22 09:45:00'),
(2, 3, 'Approve', 'This training will help reduce customer complaints. Strongly recommend approval.', '2024-01-24 11:20:00'),
(2, 1, 'Approve', 'Customer satisfaction is key to our success. Approved for implementation.', '2024-01-25 14:20:00');

-- Request 3 (Pending CEO) - Approved by Manager and Director
INSERT INTO approval_history (request_id, approver_id, action, comments, action_at) VALUES
(3, 9, 'Create', NULL, '2024-02-01 11:15:00'),
(3, 6, 'Approve', 'Sales training is essential for revenue growth. Approved for director review.', '2024-02-03 13:30:00'),
(3, 2, 'Approve', 'This training will help increase our sales performance. Recommending CEO approval.', '2024-02-05 13:45:00');

-- Request 4 (Pending Director) - Approved by Manager
INSERT INTO approval_history (request_id, approver_id, action, comments, action_at) VALUES
(4, 10, 'Create', NULL, '2024-02-10 14:20:00'),
(4, 4, 'Approve', 'Product knowledge is crucial for customer service. Approved for director review.', '2024-02-12 09:30:00');

-- Request 5 (Pending Director) - Approved by Manager
INSERT INTO approval_history (request_id, approver_id, action, comments, action_at) VALUES
(5, 11, 'Create', NULL, '2024-02-15 08:45:00'),
(5, 5, 'Approve', 'Inventory management skills are important for operational efficiency. Approved.', '2024-02-16 11:20:00');

-- Request 6 (Pending Manager) - Just created
INSERT INTO approval_history (request_id, approver_id, action, comments, action_at) VALUES
(6, 12, 'Create', NULL, '2024-02-20 13:30:00');

-- Request 7 (Pending Manager) - Just created
INSERT INTO approval_history (request_id, approver_id, action, comments, action_at) VALUES
(7, 7, 'Create', NULL, '2024-02-22 16:00:00');

-- Request 8 (Rejected by Manager)
INSERT INTO approval_history (request_id, approver_id, action, comments, action_at) VALUES
(8, 8, 'Create', NULL, '2024-02-05 12:00:00'),
(8, 4, 'Reject', 'This information is already covered in the employee handbook. Please review the existing documentation first. Training not necessary at this time.', '2024-02-06 10:15:00');

-- Request 9 (Rejected by Director)
INSERT INTO approval_history (request_id, approver_id, action, comments, action_at) VALUES
(9, 9, 'Create', NULL, '2024-01-25 15:30:00'),
(9, 6, 'Approve', 'Communication skills training is valuable. Approved for director review.', '2024-01-26 14:20:00'),
(9, 3, 'Reject', 'Similar training was provided last quarter. Please apply the previous training first and reassess in 3 months if additional training is still needed.', '2024-01-28 11:45:00');

-- Request 10 (Pending Manager) - Just created
INSERT INTO approval_history (request_id, approver_id, action, comments, action_at) VALUES
(10, 10, 'Create', NULL, '2024-02-25 09:15:00');

-- Request 11 (Pending Manager) - Just created
INSERT INTO approval_history (request_id, approver_id, action, comments, action_at) VALUES
(11, 11, 'Create', NULL, '2024-02-26 14:45:00');

-- Request 12 (Pending Manager) - Just created
INSERT INTO approval_history (request_id, approver_id, action, comments, action_at) VALUES
(12, 12, 'Create', NULL, '2024-02-27 11:30:00');

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================
-- Use these queries to verify the data was inserted correctly

-- Check users by role
-- SELECT role, COUNT(*) as count FROM users GROUP BY role ORDER BY 
--   CASE role 
--     WHEN 'CEO' THEN 1 
--     WHEN 'Director' THEN 2 
--     WHEN 'Manager' THEN 3 
--     WHEN 'Staff' THEN 4 
--   END;

-- Check requests by status
-- SELECT status, COUNT(*) as count FROM requests GROUP BY status;

-- Check approval history summary
-- SELECT 
--   r.id as request_id,
--   r.category,
--   r.status,
--   COUNT(ah.id) as approval_steps
-- FROM requests r
-- LEFT JOIN approval_history ah ON r.id = ah.request_id
-- GROUP BY r.id, r.category, r.status
-- ORDER BY r.id;

-- =====================================================
-- LOGIN CREDENTIALS FOR TESTING
-- =====================================================
-- All users have password: 123
-- 
-- CEO: ceo / 123
-- Directors: director1 / 123, director2 / 123
-- Managers: manager1 / 123, manager2 / 123, manager3 / 123
-- Staff: staff1 / 123, staff2 / 123, staff3 / 123, staff4 / 123, staff5 / 123, staff6 / 123
-- ===================================================== 