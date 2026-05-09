# Bookings Feature Slice

## Overview
Handles the complete booking workflow between pet owners and pet sitters, including creation, status management, and retrieval of bookings.

## Structure
```
features/bookings/
├── BookingController.java  - REST endpoints
├── BookingService.java     - Business logic
├── BookingDtos.java        - Request/Response DTOs
├── BookingException.java   - Custom exceptions (optional)
└── README.md               - This file
```

## API Endpoints

### GET /api/bookings
List bookings for authenticated owner (optional: filter by upcoming).

**Query Parameters:**
- `upcoming` (boolean, optional): If true, return only future bookings

**Response:**
```json
[
  {
    "bookingId": "uuid",
    "ownerId": "uuid",
    "ownerName": "string",
    "sitterId": "uuid",
    "sitterName": "string",
    "serviceType": "DAYCARE|BOARDING|GROOMING|TRAINING",
    "date": "2026-05-20",
    "startTime": "09:00:00",
    "endTime": "17:00:00",
    "status": "PENDING|CONFIRMED|COMPLETED|CANCELLED",
    "petNames": ["string"],
    "petIds": ["uuid"],
    "totalAmount": 250.00,
    "currency": "PHP"
  }
]
```

### POST /api/bookings
Create a new booking request.

**Request:**
```json
{
  "sitterId": "uuid",
  "petIds": ["uuid", "uuid"],
  "serviceType": "DAYCARE|BOARDING|GROOMING|TRAINING",
  "date": "2026-05-20",
  "startTime": "09:00:00",
  "endTime": "17:00:00",
  "specialInstructions": "string (optional)"
}
```

**Pricing Calculation:**
- durationHours = (endTime - startTime) / 60
- baseAmount = sitterHourlyRate × durationHours
- serviceFee = baseAmount × 10%
- totalAmount = baseAmount + serviceFee

### GET /api/bookings/sitter
List all bookings for authenticated sitter.

### GET /api/bookings/sitter/pending
List pending (awaiting response) bookings for sitter.

### GET /api/bookings/sitter/upcoming
List confirmed bookings from today onwards for sitter.

### GET /api/bookings/sitter/today
List confirmed bookings for today only for sitter.

### PUT /api/bookings/{bookingId}/owner-status
Owner updates booking status (can only cancel).

**Request:**
```json
{
  "status": "CANCELLED"
}
```

### PUT /api/bookings/{bookingId}/sitter-status
Sitter updates booking status.

**Request:**
```json
{
  "status": "CONFIRMED|CANCELLED|COMPLETED"
}
```

## Status Transitions

### Sitter Transitions (from current status):
- PENDING → CONFIRMED (accept booking)
- PENDING → CANCELLED (reject booking)
- CONFIRMED → COMPLETED (mark job as done)

### Owner Transitions (from current status):
- PENDING → CANCELLED
- CONFIRMED → CANCELLED

## Validation Rules
- Owner must be authenticated and have role PET_OWNER
- Sitter must be verified (isVerified = true)
- All selected pets must belong to the owner
- All pets must exist
- End time must be after start time
- Sitter must have an hourly rate configured
- Only valid transitions are allowed

## Business Logic
- Pricing is calculated at booking creation time
- Bookings start in PENDING status
- Completed bookings can be reviewed by owners
- Cancelled bookings cannot be restored

## Dependencies
- `BookingRepository`: Database access
- `UserRepository`: User data access
- `PetRepository`: Pet data access
- `SitterProfileRepository`: Sitter hourly rate

## Error Handling
Custom `BookingException` handles:
- Invalid status transitions
- Authorization violations
- Missing or invalid data
- Validation failures

## Notes
- All times are stored and returned in HH:mm:ss format
- All dates are stored and returned as YYYY-MM-DD
- Currency is always PHP
- Service fee is 10% of base amount
