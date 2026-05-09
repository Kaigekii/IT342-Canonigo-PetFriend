# Booking Feature - Frontend Documentation

## Overview
The Booking feature handles creating, managing, and tracking pet sitting bookings. Both pet owners and sitters interact with this feature.

## Directory Structure
```
features/booking/
├── api.js           # API client functions
├── constants.js     # Booking-specific constants
├── hooks/
│   ├── useBookings.js      # Booking state management
│   └── useBookingFilter.js # (Optional) Filter logic
├── components/
│   ├── BookingList.js
│   ├── BookingCard.js
│   ├── BookingForm.js
│   ├── BookingFilter.js
│   ├── BookingRequests.js
│   └── README.md
└── README.md
```

## API Client

### Import
```javascript
import { bookingsApi } from "@/features/booking/api";
```

### Methods

#### `bookingsApi.listBookings()`
Get all bookings for current user.
```javascript
const bookings = await bookingsApi.listBookings();
// Returns: [{ bookingId, sitterId, petIds, startDate, endDate, status, ... }, ...]
```

#### `bookingsApi.createBooking(bookingData)`
Create new booking.
```javascript
const booking = await bookingsApi.createBooking({
  sitterId: "uuid",
  petIds: ["pet-1", "pet-2"],
  startDate: "2024-03-20T09:00:00Z",
  endDate: "2024-03-20T17:00:00Z",
  serviceType: "WALKING",
  notes: "Please use leash"
});
```

#### `bookingsApi.cancelBooking(bookingId)`
Cancel a booking.
```javascript
const updated = await bookingsApi.cancelBooking(bookingId);
```

#### `bookingsApi.listRequests()`
Get booking requests (for sitters).
```javascript
const requests = await bookingsApi.listRequests();
// Returns: [{ bookingId, ownerId, petIds, startDate, ... }, ...]
```

#### `bookingsApi.acceptRequest(requestId)` / `declineRequest(requestId)`
Accept or decline booking request.
```javascript
await bookingsApi.acceptRequest(bookingId);
// or
await bookingsApi.declineRequest(bookingId);
```

## Hooks

### useBookings Hook
```javascript
import { useBookings } from "@/features/booking/hooks/useBookings";

const { 
  bookings, 
  loading, 
  error, 
  listBookings, 
  createBooking, 
  cancelBooking 
} = useBookings();
```

## Booking Status

### Status Values
```javascript
import { BOOKING_STATUS, BOOKING_STATUS_LABELS, BOOKING_STATUS_COLORS } from "@/shared/constants/statuses";

// Usage
console.log(BOOKING_STATUS.PENDING);           // "PENDING"
console.log(BOOKING_STATUS_LABELS.PENDING);    // "Pending"
console.log(BOOKING_STATUS_COLORS.PENDING);    // "#FFF9C4"
```

### Available Statuses
- `PENDING` - Awaiting sitter response
- `CONFIRMED` - Sitter accepted
- `IN_PROGRESS` - Booking is active
- `COMPLETED` - Booking finished
- `CANCELLED` - Booking cancelled

## Booking Data Structure
```javascript
{
  bookingId: "uuid",
  ownerId: "uuid",
  sitterId: "uuid",
  petIds: ["pet-1", "pet-2"],
  serviceType: "WALKING",
  startDate: "2024-03-20T09:00:00Z",
  endDate: "2024-03-20T17:00:00Z",
  status: "CONFIRMED",
  totalAmount: 150.00,
  currency: "PHP",
  notes: "Please use leash",
  createdAt: "2024-03-15T10:00:00Z",
  updatedAt: "2024-03-15T14:30:00Z"
}
```

## Example Component: BookingList

```javascript
"use client";

import { useEffect } from "react";
import { useBookings } from "@/features/booking/hooks/useBookings";
import { BOOKING_STATUS_LABELS, BOOKING_STATUS_COLORS } from "@/shared/constants/statuses";
import { LoadingSpinner } from "@/shared/components/Banners";

export default function BookingsList() {
  const { bookings, loading, error, listBookings } = useBookings();

  useEffect(() => {
    listBookings();
  }, []);

  if (loading) return <LoadingSpinner message="Loading bookings..." />;
  if (error) return <p>Error: {error}</p>;

  return (
    <div>
      <h2>My Bookings</h2>
      {bookings.map(booking => (
        <div
          key={booking.bookingId}
          style={{
            borderLeft: `4px solid ${BOOKING_STATUS_COLORS[booking.status]}`,
            padding: "16px",
            marginBottom: "8px",
          }}
        >
          <h3>Booking {booking.bookingId.slice(0, 8)}</h3>
          <p>Status: {BOOKING_STATUS_LABELS[booking.status]}</p>
          <p>From: {new Date(booking.startDate).toLocaleDateString()}</p>
          <p>Amount: ₱{booking.totalAmount}</p>
        </div>
      ))}
    </div>
  );
}
```

## Related Features
- [Sitters Feature](../sitters/README.md)
- [Reviews Feature](../reviews/README.md)
- [Pets Feature](../pets/README.md)

---

**Created**: May 9, 2026  
**Version**: 1.0  
**Status**: Complete
