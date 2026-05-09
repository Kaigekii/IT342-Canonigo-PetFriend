# Reviews Feature API

## Overview
The reviews feature provides APIs for:
- Pet owners to submit reviews for completed bookings
- Retrieving sitter reviews and ratings
- Calculating sitter review summaries

## API Endpoints

### Submit Review
```
POST /api/reviews
```
**Authentication:** Required (PET_OWNER role)

**Request Body:**
```json
{
  "bookingId": "uuid",
  "rating": 5,
  "comment": "Excellent service! My dog had a great time."
}
```

**Response:** 200 OK
```json
{
  "reviewId": "uuid",
  "bookingId": "uuid",
  "sitterId": "uuid",
  "reviewerId": "uuid",
  "reviewerName": "Jane Smith",
  "rating": 5,
  "comment": "Excellent service! My dog had a great time.",
  "createdAt": "2024-03-15T14:30:00Z"
}
```

**Validation Rules:**
- User must be PET_OWNER
- Booking must exist
- User must own the booking
- Booking status must be COMPLETED
- Only one review per booking allowed
- Rating must be 1-5
- Comment must not be blank

**Error Responses:**
- `400 Bad Request` - Booking not completed or already reviewed
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - User is not PET_OWNER or doesn't own booking
- `404 Not Found` - Booking not found
- `409 Conflict` - Review already exists for this booking

---

### List Sitter Reviews
```
GET /api/reviews/sitter/{sitterId}
```
**Authentication:** Required (any role)

**Path Parameters:**
- `sitterId` - UUID of the sitter

**Response:** 200 OK
```json
[
  {
    "reviewId": "uuid",
    "bookingId": "uuid",
    "sitterId": "uuid",
    "reviewerId": "uuid",
    "reviewerName": "Jane Smith",
    "rating": 5,
    "comment": "Excellent service!",
    "createdAt": "2024-03-15T14:30:00Z"
  },
  {
    "reviewId": "uuid",
    "bookingId": "uuid",
    "sitterId": "uuid",
    "reviewerId": "uuid",
    "reviewerName": "John Doe",
    "rating": 4,
    "comment": "Good sitter, very reliable",
    "createdAt": "2024-03-12T10:00:00Z"
  }
]
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated

---

### Get Sitter Review Summary
```
GET /api/reviews/sitter/{sitterId}/summary
```
**Authentication:** Required (any role)

**Path Parameters:**
- `sitterId` - UUID of the sitter

**Response:** 200 OK
```json
{
  "averageRating": 4.5,
  "reviewCount": 12
}
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated

---

### Get Reviewed Bookings
```
GET /api/reviews/me/reviewed-bookings
```
**Authentication:** Required (any role)

**Response:** 200 OK
```json
[
  "booking-uuid-1",
  "booking-uuid-2",
  "booking-uuid-3"
]
```

**Notes:**
- Returns list of booking IDs that the current user has already reviewed
- Useful for UI to prevent duplicate reviews

**Error Responses:**
- `401 Unauthorized` - Not authenticated

---

## Data Models

### Review Entity
```
- reviewId: UUID (primary key)
- bookingId: UUID (foreign key to Booking, unique)
- sitterId: UUID (foreign key to User with role PET_SITTER)
- reviewerId: UUID (foreign key to User with role PET_OWNER)
- rating: Integer (1-5)
- comment: String (max 2000 chars)
- createdAt: Instant
- updatedAt: Instant
```

---

## Business Logic

### Review Submission
1. Only pet owners can submit reviews
2. Can only review completed bookings
3. One review per booking maximum
4. Rating must be between 1 and 5
5. Comment must not be blank

### Rating Calculation
- Average rating calculated from all reviews for a sitter
- Rating scaled from 1-5
- Rounded to 1 decimal place
- Updated whenever new review is submitted

### Review Visibility
- All authenticated users can view sitter reviews
- Review includes reviewer name and date

---

## Authorization

| Endpoint | Role | Notes |
|----------|------|-------|
| `POST /api/reviews` | PET_OWNER | Submit review for own booking |
| `GET /api/reviews/sitter/{id}` | Any authenticated | View sitter's reviews |
| `GET /api/reviews/sitter/{id}/summary` | Any authenticated | View sitter's rating summary |
| `GET /api/reviews/me/reviewed-bookings` | Any authenticated | Get own reviewed bookings |

---

## Error Codes

| Code | Message | Cause |
|------|---------|-------|
| 400 | Review is allowed only after booking is completed | Booking not completed |
| 400 | Booking has no sitter assigned | Sitter null on booking |
| 401 | Unauthorized | No authentication token |
| 403 | Only pet owners can submit reviews | User is not PET_OWNER |
| 403 | You can only review your own completed bookings | User doesn't own booking |
| 404 | Booking not found | Booking ID invalid |
| 409 | A review already exists for this booking | Duplicate review attempted |

---

## Created Date
May 9, 2026

## Version
1.0
