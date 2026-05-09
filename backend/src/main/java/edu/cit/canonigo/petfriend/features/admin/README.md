# Admin Feature API

## Overview
The admin feature provides APIs for:
- Viewing system dashboard with statistics and revenue
- Managing sitter verification approvals
- Listing and managing users and bookings

## API Endpoints

### Dashboard
```
GET /api/admin/dashboard
```
**Authentication:** Required (ADMIN role)

**Response:** 200 OK
```json
{
  "totalUsers": 156,
  "petOwners": 98,
  "petSitters": 57,
  "pendingApprovals": 12,
  "totalBookings": 412,
  "monthRevenue": 5280.50,
  "platformFees": 528.05,
  "sitterPayouts": 4752.45,
  "recentActivity": [
    {
      "type": "user",
      "message": "New user registration: John Doe (Pet Sitter)",
      "ago": "2 hours ago"
    },
    {
      "type": "booking",
      "message": "Booking completed: bk_a1b2c3d4",
      "ago": "1 hour ago"
    }
  ]
}
```

**Metrics Included:**
- `totalUsers` - Total registered users
- `petOwners` - Count of pet owner accounts
- `petSitters` - Count of pet sitter accounts
- `pendingApprovals` - Unverified sitters awaiting admin approval
- `totalBookings` - Total bookings in system
- `monthRevenue` - Current month's total revenue (PHP)
- `platformFees` - 10% platform fee from monthly revenue
- `sitterPayouts` - 90% amount available for sitter payouts
- `recentActivity` - Timeline of 5 most recent user registrations and bookings

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - User is not ADMIN

---

### Pending Sitters
```
GET /api/admin/sitters/pending
```
**Authentication:** Required (ADMIN role)

**Response:** 200 OK
```json
[
  {
    "sitterId": "uuid",
    "fullName": "Mary Johnson",
    "email": "mary@example.com",
    "studentId": "STU98765",
    "bio": "Experienced dog walker with 3 years...",
    "experience": "3 years",
    "hourlyRate": 25.00,
    "servicesJson": "[\"WALKING\", \"SITTING\"]",
    "verificationDocumentUrl": "https://...",
    "submittedAt": "2024-03-10T08:30:00Z"
  }
]
```

**Notes:**
- Lists only unverified sitters
- Sorted by submission date (oldest first)
- Includes full profile information for review

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - User is not ADMIN

---

### Approve Sitter
```
POST /api/admin/sitters/{sitterId}/approve
```
**Authentication:** Required (ADMIN role)

**Path Parameters:**
- `sitterId` - UUID of the sitter to approve

**Response:** 200 OK
```json
{
  "message": "Sitter approved successfully."
}
```

**Effects:**
- Sets sitter's `isVerified` flag to `true`
- Sitter can now appear in owner search results
- Sitter receives approval notification (optional)

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - User is not ADMIN
- `404 Not Found` - Sitter not found

---

### Reject Sitter
```
POST /api/admin/sitters/{sitterId}/reject
```
**Authentication:** Required (ADMIN role)

**Path Parameters:**
- `sitterId` - UUID of the sitter to reject

**Request Body:**
```json
{
  "reason": "Background check did not clear - please contact support"
}
```

**Response:** 200 OK
```json
{
  "message": "Sitter application rejected. Reason: Background check did not clear - please contact support"
}
```

**Effects:**
- Sets sitter's `isVerified` flag to `false`
- Sitter cannot appear in owner search results
- Optional reason included in message

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - User is not ADMIN
- `404 Not Found` - Sitter not found

---

### List All Users
```
GET /api/admin/users
```
**Authentication:** Required (ADMIN role)

**Response:** 200 OK
```json
[
  {
    "userId": "uuid",
    "fullName": "John Smith",
    "email": "john@example.com",
    "role": "PET_OWNER",
    "verified": true,
    "createdAt": "2024-02-15T10:00:00Z"
  },
  {
    "userId": "uuid",
    "fullName": "Jane Doe",
    "email": "jane@example.com",
    "role": "PET_SITTER",
    "verified": false,
    "createdAt": "2024-03-10T08:30:00Z"
  }
]
```

**Notes:**
- Lists all users in the system
- Sorted by creation date (newest first)
- Includes verification status for sitters

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - User is not ADMIN

---

### List All Bookings
```
GET /api/admin/bookings
```
**Authentication:** Required (ADMIN role)

**Response:** 200 OK
```json
[
  {
    "bookingId": "uuid",
    "ownerName": "John Smith",
    "sitterName": "Jane Doe",
    "serviceType": "WALKING",
    "date": "2024-03-15",
    "status": "COMPLETED",
    "totalAmount": 75.00,
    "currency": "PHP"
  },
  {
    "bookingId": "uuid",
    "ownerName": "Sarah Johnson",
    "sitterName": "Mike Wilson",
    "serviceType": "SITTING",
    "date": "2024-03-20",
    "status": "PENDING",
    "totalAmount": 150.00,
    "currency": "PHP"
  }
]
```

**Notes:**
- Lists all bookings in the system
- Sorted by date (newest first), then by start time
- Includes owner, sitter, service type, status, and amount

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - User is not ADMIN

---

## Data Models

### Dashboard Statistics
```
- totalUsers: Long
- petOwners: Long
- petSitters: Long
- pendingApprovals: Long (unverified sitters)
- totalBookings: Long
- monthRevenue: BigDecimal (PHP)
- platformFees: BigDecimal (10% of monthRevenue)
- sitterPayouts: BigDecimal (90% of monthRevenue)
- recentActivity: List<ActivityItem> (up to 5 items)
```

### Activity Item
```
- type: String ("user" or "booking")
- message: String (descriptive activity message)
- ago: String (relative time like "2 hours ago")
```

---

## Business Logic

### Dashboard Calculations
- Revenue: Sum of all non-cancelled bookings in current month
- Platform Fees: 10% of monthly revenue
- Sitter Payouts: 90% of monthly revenue
- Activity Timeline: Most recent 5 user registrations and bookings

### Sitter Verification Workflow
1. New sitters submit profile for verification
2. Admin views pending sitters in queue
3. Admin approves or rejects each sitter
4. Approval sets `isVerified=true`, appears in search results
5. Rejection sets `isVerified=false` or remains rejected

### User Management
- View all users with roles and verification status
- Track creation dates and account status

### Booking Management
- View all bookings system-wide
- Track booking statuses and financial amounts
- Monitor service distribution

---

## Authorization

| Endpoint | Role | Notes |
|----------|------|-------|
| `GET /api/admin/dashboard` | ADMIN | View system stats and metrics |
| `GET /api/admin/sitters/pending` | ADMIN | View pending sitter approvals |
| `POST /api/admin/sitters/{id}/approve` | ADMIN | Approve pending sitter |
| `POST /api/admin/sitters/{id}/reject` | ADMIN | Reject pending sitter |
| `GET /api/admin/users` | ADMIN | List all users |
| `GET /api/admin/bookings` | ADMIN | List all bookings |

---

## Error Codes

| Code | Message | Cause |
|------|---------|-------|
| 401 | Unauthorized | No authentication token |
| 403 | Forbidden | User is not ADMIN |
| 404 | Sitter not found | Sitter ID invalid or not a sitter |

---

## Financial Model

### Revenue Calculation
**Monthly Revenue Formula:**
```
monthlyRevenue = SUM(booking.totalAmount) 
                 WHERE booking.status != CANCELLED 
                 AND booking.date IN current month
```

### Platform Fees & Payouts
```
platformFees = monthlyRevenue × 0.10 (10%)
sitterPayouts = monthlyRevenue × 0.90 (90%)
```

### Example
- Total bookings in month: $5,000
- Platform fees (10%): $500
- Available for sitter payouts (90%): $4,500

---

## Created Date
May 9, 2026

## Version
1.0
