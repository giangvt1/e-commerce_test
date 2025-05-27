-- =====================================================
-- USEFUL QUERIES FOR RETAILONBOARDPRO DATABASE
-- =====================================================

-- =====================================================
-- 1. USER MANAGEMENT QUERIES
-- =====================================================

-- Get all users with their roles
SELECT id, name, username, email, role 
FROM users 
ORDER BY 
  CASE role 
    WHEN 'CEO' THEN 1 
    WHEN 'Director' THEN 2 
    WHEN 'Manager' THEN 3 
    WHEN 'Staff' THEN 4 
  END, name;

-- Count users by role
SELECT role, COUNT(*) as user_count 
FROM users 
GROUP BY role 
ORDER BY 
  CASE role 
    WHEN 'CEO' THEN 1 
    WHEN 'Director' THEN 2 
    WHEN 'Manager' THEN 3 
    WHEN 'Staff' THEN 4 
  END;

-- Find users by role
SELECT name, username, email 
FROM users 
WHERE role = 'Staff'  -- Change to 'Manager', 'Director', 'CEO' as needed
ORDER BY name;

-- =====================================================
-- 2. REQUEST ANALYSIS QUERIES
-- =====================================================

-- Get all requests with creator information
SELECT 
  r.id,
  r.category,
  r.status,
  u.name as creator_name,
  u.role as creator_role,
  r.created_at,
  r.updated_at
FROM requests r
JOIN users u ON r.creator_id = u.id
ORDER BY r.created_at DESC;

-- Count requests by status
SELECT status, COUNT(*) as request_count 
FROM requests 
GROUP BY status
ORDER BY 
  CASE status
    WHEN 'Pending_Manager' THEN 1
    WHEN 'Pending_Director' THEN 2
    WHEN 'Pending_CEO' THEN 3
    WHEN 'Approved' THEN 4
    WHEN 'Rejected_Manager' THEN 5
    WHEN 'Rejected_Director' THEN 6
    WHEN 'Rejected_CEO' THEN 7
  END;

-- Count requests by category
SELECT category, COUNT(*) as request_count 
FROM requests 
GROUP BY category 
ORDER BY request_count DESC;

-- Get pending requests for each role
-- Pending for Managers
SELECT r.id, r.category, u.name as creator, r.created_at
FROM requests r
JOIN users u ON r.creator_id = u.id
WHERE r.status = 'Pending_Manager'
ORDER BY r.created_at;

-- Pending for Directors
SELECT r.id, r.category, u.name as creator, r.created_at
FROM requests r
JOIN users u ON r.creator_id = u.id
WHERE r.status = 'Pending_Director'
ORDER BY r.created_at;

-- Pending for CEO
SELECT r.id, r.category, u.name as creator, r.created_at
FROM requests r
JOIN users u ON r.creator_id = u.id
WHERE r.status = 'Pending_CEO'
ORDER BY r.created_at;

-- Get approved requests
SELECT r.id, r.category, u.name as creator, r.created_at, r.updated_at
FROM requests r
JOIN users u ON r.creator_id = u.id
WHERE r.status = 'Approved'
ORDER BY r.updated_at DESC;

-- Get rejected requests with rejection level
SELECT 
  r.id, 
  r.category, 
  u.name as creator, 
  r.status,
  CASE 
    WHEN r.status = 'Rejected_Manager' THEN 'Rejected by Manager'
    WHEN r.status = 'Rejected_Director' THEN 'Rejected by Director'
    WHEN r.status = 'Rejected_CEO' THEN 'Rejected by CEO'
  END as rejection_level,
  r.created_at,
  r.updated_at
FROM requests r
JOIN users u ON r.creator_id = u.id
WHERE r.status LIKE 'Rejected%'
ORDER BY r.updated_at DESC;

-- =====================================================
-- 3. APPROVAL WORKFLOW ANALYSIS
-- =====================================================

-- Get complete approval history for a specific request
SELECT 
  ah.action_at,
  u.name as approver_name,
  u.role as approver_role,
  ah.action,
  ah.comments
FROM approval_history ah
JOIN users u ON ah.approver_id = u.id
WHERE ah.request_id = 1  -- Change request ID as needed
ORDER BY ah.action_at;

-- Get approval statistics by user
SELECT 
  u.name,
  u.role,
  COUNT(CASE WHEN ah.action = 'Approve' THEN 1 END) as approvals,
  COUNT(CASE WHEN ah.action = 'Reject' THEN 1 END) as rejections,
  COUNT(CASE WHEN ah.action = 'Create' THEN 1 END) as requests_created
FROM users u
LEFT JOIN approval_history ah ON u.id = ah.approver_id
GROUP BY u.id, u.name, u.role
ORDER BY u.role, u.name;

-- Get average approval time by level
SELECT 
  approver_role,
  AVG(DATEDIFF(hour, request_created, action_time)) as avg_hours_to_approve
FROM (
  SELECT 
    r.id as request_id,
    r.created_at as request_created,
    ah.action_at as action_time,
    u.role as approver_role,
    ah.action
  FROM requests r
  JOIN approval_history ah ON r.id = ah.request_id
  JOIN users u ON ah.approver_id = u.id
  WHERE ah.action IN ('Approve', 'Reject')
) approval_times
GROUP BY approver_role;

-- =====================================================
-- 4. DASHBOARD STATISTICS QUERIES
-- =====================================================

-- Get request counts for dashboard
SELECT 
  COUNT(CASE WHEN status LIKE 'Pending%' THEN 1 END) as pending_count,
  COUNT(CASE WHEN status = 'Approved' THEN 1 END) as approved_count,
  COUNT(CASE WHEN status LIKE 'Rejected%' THEN 1 END) as rejected_count,
  COUNT(*) as total_requests
FROM requests;

-- Get recent requests (last 30 days)
SELECT 
  r.id,
  r.category,
  r.status,
  u.name as creator,
  r.created_at
FROM requests r
JOIN users u ON r.creator_id = u.id
WHERE r.created_at >= DATEADD(day, -30, GETDATE())
ORDER BY r.created_at DESC;

-- Get requests by user (for staff dashboard)
SELECT 
  r.id,
  r.category,
  r.status,
  r.created_at,
  r.updated_at
FROM requests r
WHERE r.creator_id = 7  -- Change to specific user ID
ORDER BY r.created_at DESC;

-- =====================================================
-- 5. REPORTING QUERIES
-- =====================================================

-- Monthly request summary
SELECT 
  YEAR(created_at) as year,
  MONTH(created_at) as month,
  COUNT(*) as total_requests,
  COUNT(CASE WHEN status = 'Approved' THEN 1 END) as approved,
  COUNT(CASE WHEN status LIKE 'Rejected%' THEN 1 END) as rejected,
  COUNT(CASE WHEN status LIKE 'Pending%' THEN 1 END) as pending
FROM requests
GROUP BY YEAR(created_at), MONTH(created_at)
ORDER BY year DESC, month DESC;

-- Category performance report
SELECT 
  category,
  COUNT(*) as total_requests,
  COUNT(CASE WHEN status = 'Approved' THEN 1 END) as approved,
  COUNT(CASE WHEN status LIKE 'Rejected%' THEN 1 END) as rejected,
  ROUND(
    CAST(COUNT(CASE WHEN status = 'Approved' THEN 1 END) AS FLOAT) / 
    CAST(COUNT(*) AS FLOAT) * 100, 2
  ) as approval_rate_percent
FROM requests
GROUP BY category
ORDER BY approval_rate_percent DESC;

-- User activity report
SELECT 
  u.name,
  u.role,
  COUNT(r.id) as requests_created,
  COUNT(ah.id) as approval_actions,
  MAX(r.created_at) as last_request_date,
  MAX(ah.action_at) as last_approval_date
FROM users u
LEFT JOIN requests r ON u.id = r.creator_id
LEFT JOIN approval_history ah ON u.id = ah.approver_id AND ah.action != 'Create'
GROUP BY u.id, u.name, u.role
ORDER BY u.role, requests_created DESC;

-- =====================================================
-- 6. MAINTENANCE QUERIES
-- =====================================================

-- Find orphaned records (should return empty if data integrity is good)
-- Requests without creators
SELECT r.id, r.category 
FROM requests r 
LEFT JOIN users u ON r.creator_id = u.id 
WHERE u.id IS NULL;

-- Approval history without requests
SELECT ah.id, ah.action 
FROM approval_history ah 
LEFT JOIN requests r ON ah.request_id = r.id 
WHERE r.id IS NULL;

-- Approval history without approvers
SELECT ah.id, ah.action 
FROM approval_history ah 
LEFT JOIN users u ON ah.approver_id = u.id 
WHERE u.id IS NULL;

-- =====================================================
-- 7. SEARCH QUERIES
-- =====================================================

-- Search requests by keyword in reason
SELECT 
  r.id,
  r.category,
  r.status,
  u.name as creator,
  r.reason,
  r.created_at
FROM requests r
JOIN users u ON r.creator_id = u.id
WHERE r.reason LIKE '%customer%'  -- Change search term as needed
ORDER BY r.created_at DESC;

-- Search users by name or email
SELECT id, name, username, email, role
FROM users
WHERE name LIKE '%john%' OR email LIKE '%john%'  -- Change search term as needed
ORDER BY name;

-- =====================================================
-- 8. PERFORMANCE QUERIES
-- =====================================================

-- Requests with most approval steps
SELECT 
  r.id,
  r.category,
  r.status,
  u.name as creator,
  COUNT(ah.id) as approval_steps
FROM requests r
JOIN users u ON r.creator_id = u.id
LEFT JOIN approval_history ah ON r.id = ah.request_id
GROUP BY r.id, r.category, r.status, u.name
ORDER BY approval_steps DESC;

-- Fastest approved requests (from creation to final approval)
SELECT 
  r.id,
  r.category,
  u.name as creator,
  r.created_at,
  r.updated_at,
  DATEDIFF(hour, r.created_at, r.updated_at) as hours_to_approve
FROM requests r
JOIN users u ON r.creator_id = u.id
WHERE r.status = 'Approved'
ORDER BY hours_to_approve ASC; 