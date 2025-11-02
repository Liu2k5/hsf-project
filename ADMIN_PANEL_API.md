# Admin Panel Backend API Documentation

This document describes all the backend APIs implemented for the Admin Panel.

## Base URL
All admin endpoints are prefixed with `/admin`

---

## 1. User Management APIs

### 1.1 Get All Users (Paginated)
**Endpoint:** `GET /admin/users`

**Query Parameters:**
- `page` (optional, default: 0) - Page number
- `size` (optional, default: 10) - Number of items per page

**Response:**
```json
{
  "content": [
    {
      "userId": 1,
      "username": "john_doe",
      "roleName": "admin",
      "locked": false
    }
  ],
  "pageable": {...},
  "totalPages": 5,
  "totalElements": 50
}
```

### 1.2 Update User Role
**Endpoint:** `POST /admin/users/role`

**Request Body:**
```json
{
  "userId": 1,
  "newRoleName": "admin"
}
```

**Response:**
```
"Role updated successfully"
```

### 1.3 Lock/Unlock User Account
**Endpoint:** `POST /admin/users/lock`

**Request Body:**
```json
{
  "userId": 1,
  "lock": true
}
```

**Response:**
```
"User lock status changed"
```

---

## 2. Order Management APIs

### 2.1 Get All Orders (Paginated & Filtered)
**Endpoint:** `GET /admin/orders`

**Query Parameters:**
- `page` (optional, default: 0) - Page number
- `size` (optional, default: 10) - Number of items per page
- `status` (optional) - Filter by status: "PENDING", "COMPLETED", "CANCELLED"
- `startDate` (optional) - Filter by date (format: YYYY-MM-DD)

**Response:**
```json
{
  "content": [
    {
      "orderId": 1,
      "username": "john_doe",
      "productName": "Premium License",
      "paidDate": "2025-11-02",
      "status": "COMPLETED"
    }
  ],
  "pageable": {...},
  "totalPages": 10,
  "totalElements": 100
}
```

### 2.2 Get Order Details
**Endpoint:** `GET /admin/orders/{orderId}`

**Response:**
```json
{
  "orderId": 1,
  "username": "john_doe",
  "productName": "Premium License",
  "paidDate": "2025-11-02",
  "status": "COMPLETED",
  "license": {
    "licenseId": 1,
    "licenseKey": "ABC-123-XYZ",
    "username": "john_doe",
    "productName": "Premium License",
    "enabled": true
  }
}
```

---

## 3. License Management APIs

### 3.1 Get All Licenses (Paginated & Searchable)
**Endpoint:** `GET /admin/licenses`

**Query Parameters:**
- `page` (optional, default: 0) - Page number
- `size` (optional, default: 10) - Number of items per page
- `searchKey` (optional) - Search by license key, username, or product name

**Response:**
```json
{
  "content": [
    {
      "licenseId": 1,
      "licenseKey": "ABC-123-XYZ",
      "username": "john_doe",
      "productName": "Premium License",
      "enabled": true
    }
  ],
  "pageable": {...},
  "totalPages": 15,
  "totalElements": 150
}
```

### 3.2 Revoke License
**Endpoint:** `POST /admin/licenses/revoke`

**Request Body:**
```json
{
  "licenseKey": "ABC-123-XYZ",
  "reason": "Violation of terms"
}
```

**Response:**
```
"License revoked"
```

### 3.3 Reset Device (Allow Device Change)
**Endpoint:** `POST /admin/licenses/reset`

**Request Body:**
```json
{
  "licenseKey": "ABC-123-XYZ"
}
```

**Response:**
```
"Device reset for license"
```

**Note:** This only removes the active session, allowing the license to be activated on a different device. The license itself remains enabled.

---

## 4. Dashboard Statistics APIs

### 4.1 Get Summary Statistics
**Endpoint:** `GET /admin/stats/summary`

**Response:**
```json
{
  "totalRevenue": 50000.00,
  "newOrdersToday": 5,
  "activeSessions": 42
}
```

### 4.2 Get Revenue Chart Data
**Endpoint:** `GET /admin/stats/revenue-chart`

**Response:**
```json
[
  {
    "date": "2025-10-01",
    "revenue": 1500.00
  },
  {
    "date": "2025-10-02",
    "revenue": 2300.00
  }
]
```

**Note:** Returns revenue grouped by day for completed orders.

### 4.3 Get Top Products
**Endpoint:** `GET /admin/stats/top-products`

**Response:**
```json
[
  {
    "productName": "Premium License",
    "salesCount": 120
  },
  {
    "productName": "Basic License",
    "salesCount": 85
  }
]
```

**Note:** Returns top 5 products by sales count.

---

## Entity Changes

### Users Entity
Added field:
- `locked` (boolean, default: false) - Indicates if the user account is locked

### Orders Entity
Added field:
- `status` (String, default: "PENDING") - Order status: PENDING, COMPLETED, or CANCELLED

---

## Services Created

1. **OrderService** - Handles order management logic with optimized queries
2. **LicenseService** - Handles license search and filtering with JPA Specifications
3. **DashboardService** - Handles statistics and analytics with database-level aggregations

## Services Updated

1. **UserService** - Added user management methods (pagination, role update, lock toggle)
2. **LicenseSessionService** - Added resetDevice method

## Repository Enhancements

### OrderRepository
Added custom queries for performance optimization:
- `calculateTotalRevenue()` - Calculates total revenue at database level
- `countOrdersByDate()` - Counts orders by date efficiently
- `findCompletedOrdersWithPaidDate()` - Fetches only completed orders with dates
- `findAllWithProduct()` - Fetches only orders with associated products

### LicenseRepository
Added method:
- `findByOrder()` - Finds license by order efficiently without loading all licenses

---

## DTOs Created

### User Management
- `UserDTO` - Safe user data transfer
- `UpdateRoleRequest` - Role update request
- `ToggleLockRequest` - Lock/unlock request

### Order Management
- `OrderSummaryDTO` - Order list item
- `OrderDetailDTO` - Detailed order information

### License Management
- `LicenseDTO` - License information
- `RevokeRequest` - License revocation request
- `ResetRequest` - Device reset request

### Dashboard Statistics
- `StatsSummaryDTO` - Summary statistics
- `RevenueByDayDTO` - Daily revenue data
- `TopProductDTO` - Product sales data

---

## Security Considerations

- All endpoints should be secured with admin role requirement in SecurityConfig
- Password field is never exposed in UserDTO
- All operations are transactional to maintain data consistency

---

## Testing

Unit tests have been added for all DTOs to verify correct object creation and data handling.
Run tests with: `./mvnw test -Dtest=DTOTests`
