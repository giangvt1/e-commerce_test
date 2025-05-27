# API Documentation - Role Requests

## Endpoints

### 1. Create Role Request
**Endpoint:** `POST /api/role-requests`  
**Authentication:** Required (Staff role only)  
**Description:** Tạo yêu cầu thay đổi vai trò mới

**Request Body:**
```json
{
  "requestedRole": "Manager|Director|CEO",
  "reason": "string (10-1000 characters)"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Yêu cầu thay đổi vai trò đã được tạo thành công",
  "data": {
    "id": 1,
    "userId": 1,
    "userName": "John Doe",
    "userEmail": "john@example.com", 
    "currentRole": "Staff",
    "requestedRole": {
      "name": "Manager",
      "displayName": "Quản lý"
    },
    "reason": "Tôi muốn được thăng chức...",
    "status": "PENDING",
    "statusDisplayName": "Đang chờ xử lý",
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "Bạn đã có một yêu cầu thay đổi vai trò đang chờ xử lý"
}
```

---

### 2. Get My Role Requests
**Endpoint:** `GET /api/role-requests/my-requests`  
**Authentication:** Required (All roles)  
**Description:** Lấy danh sách yêu cầu thay đổi vai trò của người dùng hiện tại

**Response:**
```json
{
  "success": true,
  "message": "Lấy danh sách yêu cầu thành công",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "userName": "John Doe",
      "userEmail": "john@example.com",
      "currentRole": "Staff", 
      "requestedRole": {
        "name": "Manager",
        "displayName": "Quản lý"
      },
      "reason": "Tôi muốn được thăng chức...",
      "status": "PENDING",
      "statusDisplayName": "Đang chờ xử lý",
      "adminComments": null,
      "reviewedByName": null,
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00",
      "reviewedAt": null
    }
  ]
}
```

---

### 3. Get Role Requests by Status (Admin)
**Endpoint:** `GET /api/role-requests/status/{status}`  
**Authentication:** Required (CEO or Director only)  
**Description:** Lấy danh sách yêu cầu theo trạng thái (cho admin)

**Path Parameters:**
- `status`: PENDING | APPROVED | REJECTED

**Response:**
```json
{
  "success": true,
  "message": "Lấy danh sách yêu cầu theo trạng thái thành công",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "userName": "John Doe",
      "userEmail": "john@example.com",
      "currentRole": "Staff",
      "requestedRole": {
        "name": "Manager", 
        "displayName": "Quản lý"
      },
      "reason": "Tôi muốn được thăng chức...",
      "status": "PENDING",
      "statusDisplayName": "Đang chờ xử lý",
      "adminComments": null,
      "reviewedByName": null,
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00",
      "reviewedAt": null
    }
  ]
}
```

---

### 4. Review Role Request (Admin)
**Endpoint:** `POST /api/role-requests/{id}/review`  
**Authentication:** Required (CEO or Director only)  
**Description:** Phê duyệt hoặc từ chối yêu cầu thay đổi vai trò

**Path Parameters:**
- `id`: ID của role request

**Request Body:**
```json
{
  "action": "APPROVED|REJECTED",
  "comments": "string (optional, max 500 characters)"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Đã phê duyệt yêu cầu thành công",
  "data": {
    "id": 1,
    "userId": 1,
    "userName": "John Doe",
    "userEmail": "john@example.com",
    "currentRole": "Manager", // Updated after approval
    "requestedRole": {
      "name": "Manager",
      "displayName": "Quản lý"
    },
    "reason": "Tôi muốn được thăng chức...",
    "status": "APPROVED",
    "statusDisplayName": "Đã phê duyệt",
    "adminComments": "Phê duyệt do thể hiện tốt",
    "reviewedByName": "Admin User",
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T11:00:00",
    "reviewedAt": "2024-01-01T11:00:00"
  }
}
```

---

### 5. Check Can Create Role Request
**Endpoint:** `GET /api/role-requests/can-create`  
**Authentication:** Required (Staff role only)  
**Description:** Kiểm tra xem người dùng có thể tạo yêu cầu thay đổi vai trò không

**Response:**
```json
{
  "success": true,
  "message": "Kiểm tra quyền tạo yêu cầu thành công",
  "data": {
    "canCreate": true,
    "message": "Có thể tạo yêu cầu"
  }
}
```

---

### 6. Get Pending Role Requests (Admin)
**Endpoint:** `GET /api/role-requests/pending`  
**Authentication:** Required (CEO or Director only)  
**Description:** Lấy danh sách yêu cầu đang chờ xử lý

**Response:**
```json
{
  "success": true,
  "message": "Lấy danh sách yêu cầu chờ xử lý thành công",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "userName": "John Doe",
      "userEmail": "john@example.com",
      "currentRole": "Staff",
      "requestedRole": {
        "name": "Manager",
        "displayName": "Quản lý"
      },
      "reason": "Tôi muốn được thăng chức...",
      "status": "PENDING",
      "statusDisplayName": "Đang chờ xử lý",
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00"
    }
  ]
}
```

## Error Handling

Tất cả endpoints đều có thể trả về các mã lỗi sau:

### 400 Bad Request
- Dữ liệu đầu vào không hợp lệ
- Vi phạm business rules (ví dụ: đã có pending request)

### 401 Unauthorized  
- Chưa đăng nhập
- JWT token không hợp lệ

### 403 Forbidden
- Không có quyền truy cập endpoint
- Vai trò không phù hợp

### 404 Not Found
- Role request không tồn tại

### 500 Internal Server Error
- Lỗi server nội bộ

## Authentication

Tất cả API endpoints yêu cầu JWT token trong header:
```
Authorization: Bearer <jwt_token>
```

## Role-based Access Control

- **Staff**: Có thể tạo và xem yêu cầu của mình
- **Manager**: Chỉ có thể xem yêu cầu của mình (không thể phê duyệt role requests)
- **Director**: Có thể phê duyệt yêu cầu thay đổi vai trò + xem tất cả
- **CEO**: Có thể phê duyệt yêu cầu thay đổi vai trò + xem tất cả 