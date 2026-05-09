# Sitters Feature API

## Overview
The sitters feature provides APIs for:
- Pet owners to search and discover verified pet sitters
- Sitters to manage their profiles, services, and availability
- Sitter profile verification workflow

## API Endpoints

### Sitter Search (Pet Owner)

#### Search Verified Sitters
```
GET /api/sitters/search
```
**Authentication:** Required (PET_OWNER role)

**Query Parameters:**
- `location` (optional) - Location filter
- `serviceType` (optional) - Service type filter (e.g., WALKING, SITTING, TRAINING)

**Response:** 200 OK
```json
[
  {
    "sitterId": "uuid",
    "fullName": "John Doe",
    "bio": "Experienced pet sitter with 5 years...",
    "experience": "5 years",
    "hourlyRate": 25.00,
    "servicesOffered": ["WALKING", "SITTING"],
    "rating": 4.8,
    "reviewCount": 15,
    "verified": true,
    "location": "Downtown"
  }
]
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - User is not a pet owner

---

#### Get Sitter Details
```
GET /api/sitters/{sitterId}
```
**Authentication:** Required (PET_OWNER role)

**Path Parameters:**
- `sitterId` - UUID of the sitter

**Response:** 200 OK
```json
{
  "sitterId": "uuid",
  "fullName": "John Doe",
  "bio": "Experienced pet sitter with 5 years of caring for dogs and cats...",
  "experience": "5 years",
  "hourlyRate": 25.00,
  "servicesOffered": ["WALKING", "SITTING", "TRAINING"],
  "availabilitySchedule": {
    "Monday": { "startTime": "09:00", "endTime": "17:00" },
    "Tuesday": { "startTime": "09:00", "endTime": "17:00" }
  },
  "rating": 4.8,
  "reviewCount": 15,
  "verified": true,
  "reviews": [
    {
      "reviewerName": "Jane Smith",
      "date": "2024-03-15",
      "rating": 5,
      "comment": "Great sitter! My dog loved the walks."
    }
  ]
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - User is not a pet owner
- `404 Not Found` - Sitter not found or not verified

---

### Sitter Profile Management

#### Get My Profile
```
GET /api/sitter-profile
GET /api/sitters/profile
```
**Authentication:** Required (PET_SITTER role)

**Response:** 200 OK
```json
{
  "profileId": "uuid",
  "userId": "uuid",
  "profilePhotoUrl": "https://...",
  "bio": "Experienced pet sitter...",
  "experience": "5 years",
  "hourlyRate": 25.00,
  "servicesOffered": ["WALKING", "SITTING"],
  "availabilitySchedule": {
    "Monday": { "startTime": "09:00", "endTime": "17:00" }
  },
  "studentId": "STU12345",
  "referenceContact": "+1234567890",
  "verificationDocumentUrl": "https://...",
  "isVerified": true
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated

---

#### Update My Profile
```
PUT /api/sitter-profile
PUT /api/sitters/profile
```
**Authentication:** Required (PET_SITTER role)

**Request Body:**
```json
{
  "profilePhotoUrl": "https://...",
  "bio": "Updated bio...",
  "experience": "6 years",
  "hourlyRate": 27.00,
  "servicesOffered": ["WALKING", "SITTING", "TRAINING"],
  "availabilitySchedule": {
    "Monday": { "startTime": "09:00", "endTime": "18:00" },
    "Wednesday": { "startTime": "10:00", "endTime": "16:00" }
  },
  "studentId": "STU12345",
  "referenceContact": "+1234567890",
  "verificationDocumentUrl": "https://..."
}
```

**Response:** 200 OK - Updated profile (same structure as GET)

**Error Responses:**
- `400 Bad Request` - Invalid profile payload
- `401 Unauthorized` - Not authenticated

---

#### Submit for Verification
```
POST /api/sitter-profile/submit-verification
POST /api/sitters/profile/submit-verification
```
**Authentication:** Required (PET_SITTER role)

**Response:** 200 OK
```json
"Verification submitted"
```

**Notes:**
- Sets `isVerified` to `false` to indicate pending admin review
- Admin will review and approve or reject the submission

**Error Responses:**
- `401 Unauthorized` - Not authenticated

---

## Data Models

### SitterProfile Entity
```
- profileId: UUID (primary key)
- userId: UUID (foreign key to User)
- bio: String (optional, max 1000 chars)
- experience: String (optional, e.g., "5 years")
- hourlyRate: BigDecimal (required)
- servicesJson: String (JSON array of service types)
- availabilityJson: String (JSON map of day→time slots)
- studentId: String (optional, for student verification)
- referenceContact: String (optional, contact for reference)
- verificationDocumentUrl: String (optional, URL to verification docs)
- createdAt: Instant
- updatedAt: Instant
```

### ServiceType Enum
```
- WALKING
- SITTING
- TRAINING
- GROOMING
- BATHING
- FEEDING
- PLAY_SESSION
```

### DayAvailability
```
- startTime: String (HH:mm format)
- endTime: String (HH:mm format)
```

---

## Business Logic

### Search Filtering
- Only returns **verified** sitters (isVerified = true)
- Optional service type filtering based on offered services
- Returns sitters sorted by creation date (earliest first)
- Includes average rating and review count for each sitter

### Rating Calculation
- Average rating calculated from all reviews by sitter
- Reviews limited to 10 most recent when viewing sitter details
- Rating scaled from 1-5, rounded to 1 decimal place

### Profile Verification Workflow
1. Sitter completes their profile with services and availability
2. Sitter submits for verification via `POST /submit-verification`
3. Admin reviews and approves/rejects in admin dashboard
4. Once approved, sitter's `isVerified` becomes true
5. Sitter appears in search results for pet owners

---

## Authorization

| Endpoint | Role | Notes |
|----------|------|-------|
| `GET /api/sitters/search` | PET_OWNER | Search available sitters |
| `GET /api/sitters/{id}` | PET_OWNER | View sitter profile |
| `GET /api/sitter-profile` | PET_SITTER | Get own profile |
| `PUT /api/sitter-profile` | PET_SITTER | Update own profile |
| `POST /api/sitter-profile/submit-verification` | PET_SITTER | Submit for verification |

---

## Error Codes

| Code | Message | Cause |
|------|---------|-------|
| 401 | Unauthorized | No authentication token or invalid token |
| 403 | Forbidden | User does not have required role |
| 404 | Sitter not found | Sitter ID doesn't exist or not verified |
| 400 | Invalid profile payload | JSON parsing error in services/availability |

---

## Created Date
May 9, 2026

## Version
1.0
