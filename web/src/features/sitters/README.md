# Sitters Feature - Frontend Documentation

## Overview
The Sitters feature handles sitter discovery, search, profile management, and verification. Pet owners search for sitters, and sitters manage their profiles.

## Directory Structure
```
features/sitters/
├── api.js           # API client functions
├── constants.js     # Sitter-specific constants
├── hooks/
│   ├── useSitters.js       # Sitter search and details
│   └── useSitterSearch.js  # (Optional) Search filters
├── components/
│   ├── SitterSearch.js
│   ├── SitterCard.js
│   ├── SitterDetails.js
│   ├── SitterProfile.js
│   └── README.md
└── README.md
```

## API Client

### Import
```javascript
import { sittersApi } from "@/features/sitters/api";
```

### Methods

#### `sittersApi.searchSitters(location, serviceType)`
Search for sitters with filters.
```javascript
const sitters = await sittersApi.searchSitters("Metro Manila", "WALKING");
// Returns: [{ sitterId, fullName, bio, experience, hourlyRate, rating, ... }, ...]
```

#### `sittersApi.getSitterDetails(sitterId)`
Get detailed sitter profile with reviews.
```javascript
const sitter = await sittersApi.getSitterDetails(sitterId);
// Returns: { sitterId, fullName, bio, experience, hourlyRate, reviews[], rating, ... }
```

#### `sittersApi.getMyProfile()`
Get current sitter's profile (for sitters only).
```javascript
const profile = await sittersApi.getMyProfile();
```

#### `sittersApi.updateProfile(profileData)`
Update sitter profile.
```javascript
const updated = await sittersApi.updateProfile({
  bio: "Updated bio",
  hourlyRate: 300,
  servicesJson: JSON.stringify(["WALKING", "SITTING"]),
  scheduleJson: JSON.stringify({ monday: { startTime: "08:00", endTime: "18:00" }, ... })
});
```

#### `sittersApi.submitVerification()`
Submit profile for admin verification.
```javascript
await sittersApi.submitVerification();
```

## Hooks

### useSitters Hook
```javascript
import { useSitters } from "@/features/sitters/hooks/useSitters";

const {
  sitters,
  sitterDetails,
  loading,
  error,
  searchSitters,
  getSitterDetails
} = useSitters();
```

## Service Types

```javascript
import { SERVICE_TYPE, SERVICE_TYPE_LABELS } from "@/shared/constants/statuses";

console.log(SERVICE_TYPE.WALKING);        // "WALKING"
console.log(SERVICE_TYPE_LABELS.WALKING);  // "Walking"
```

### Available Services
- `WALKING` - Dog walking services
- `SITTING` - Pet sitting/boarding
- `GROOMING` - Pet grooming
- `TRAINING` - Pet training
- `VETERINARY` - Veterinary assistance

## Sitter Data Structure
```javascript
{
  sitterId: "uuid",
  fullName: "Jane Smith",
  email: "jane@example.com",
  bio: "Experienced pet sitter...",
  experience: "3 years",
  hourlyRate: 300.00,
  servicesJson: "[\"WALKING\", \"SITTING\"]",
  rating: 4.5,
  reviewCount: 12,
  verified: true,
  scheduleJson: "{ monday: { startTime: \"08:00\", endTime: \"18:00\" }, ... }",
  createdAt: "2024-01-15T10:00:00Z"
}
```

## Example Component: SitterSearch

```javascript
"use client";

import { useState, useEffect } from "react";
import { useSitters } from "@/features/sitters/hooks/useSitters";
import { SERVICE_TYPE_LABELS } from "@/shared/constants/statuses";
import { formatRating } from "@/shared/utils/formatting";

export default function SitterSearch() {
  const { sitters, loading, searchSitters } = useSitters();
  const [location, setLocation] = useState("");
  const [serviceType, setServiceType] = useState("");

  const handleSearch = async (e) => {
    e.preventDefault();
    await searchSitters(location, serviceType);
  };

  return (
    <div>
      <form onSubmit={handleSearch}>
        <input
          type="text"
          placeholder="Location"
          value={location}
          onChange={(e) => setLocation(e.target.value)}
        />
        <select
          value={serviceType}
          onChange={(e) => setServiceType(e.target.value)}
        >
          <option value="">Any service</option>
          {Object.entries(SERVICE_TYPE_LABELS).map(([key, label]) => (
            <option key={key} value={key}>{label}</option>
          ))}
        </select>
        <button type="submit">Search</button>
      </form>

      <div>
        {loading ? (
          <p>Loading sitters...</p>
        ) : (
          sitters.map(sitter => (
            <div key={sitter.sitterId}>
              <h3>{sitter.fullName}</h3>
              <p>{sitter.bio}</p>
              <p>Experience: {sitter.experience}</p>
              <p>Rate: ₱{sitter.hourlyRate}/hour</p>
              <p>Rating: {formatRating(sitter.rating)} ({sitter.reviewCount} reviews)</p>
              {sitter.verified && <p>✓ Verified</p>}
            </div>
          ))
        )}
      </div>
    </div>
  );
}
```

## Related Features
- [Bookings Feature](../booking/README.md)
- [Reviews Feature](../reviews/README.md)
- [Messages Feature](../messages/README.md)

---

**Created**: May 9, 2026  
**Version**: 1.0  
**Status**: Complete
