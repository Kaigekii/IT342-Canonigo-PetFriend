# Shared Utilities - Frontend Documentation

## Overview
Shared utilities provide common functionality used across all features: API client, hooks, components, constants, and formatting utilities.

## Directory Structure
```
shared/
├── components/
│   ├── PawIcon.js          # PawIcon SVG component
│   ├── Banners.js          # Error, Success, Warning, Loading
│   └── README.md
├── hooks/
│   ├── useFetch.js         # Generic fetch wrapper
│   ├── useAuth.js          # Auth state management
│   └── README.md
├── utils/
│   ├── api.js              # API client singleton
│   ├── formatting.js       # Date, currency formatting
│   └── README.md
├── constants/
│   ├── api.js              # API endpoints
│   ├── statuses.js         # Status enums and labels
│   ├── validation.js       # Validation rules and messages
│   └── README.md
└── README.md
```

## API Utilities (`shared/utils/api.js`)

### ApiClient Class
Provides a singleton API client with automatic error handling and authentication.

```javascript
import { apiClient } from "@/shared/utils/api";

// GET request
const data = await apiClient.get("/api/endpoint");

// POST request
const result = await apiClient.post("/api/endpoint", { key: "value" });

// PUT request
const updated = await apiClient.put("/api/endpoint", { key: "value" });

// DELETE request
await apiClient.delete("/api/endpoint");
```

### Features
- ✅ Automatic Bearer token authentication
- ✅ JSON serialization/deserialization
- ✅ Error handling and logging
- ✅ 401 redirect to login on unauthorized
- ✅ Custom headers support

## Formatting Utilities (`shared/utils/formatting.js`)

```javascript
import {
  formatDate,
  formatDateTime,
  formatTime,
  getRelativeTime,
  formatDuration,
  isPastDate,
  isToday,
  formatCurrency,
  formatRating
} from "@/shared/utils/formatting";

// Date formatting
formatDate(new Date());                    // "Mar 15, 2024"
formatDateTime(new Date());                // "Mar 15, 2024 2:30 PM"
formatTime(new Date());                    // "2:30 PM"

// Relative time
getRelativeTime(new Date());               // "just now"
getRelativeTime(dateOneHourAgo);           // "1 hour ago"

// Duration
formatDuration(startDate, endDate);        // "2 days 3h"

// Date checks
isPastDate(dateInThePast);                 // true
isToday(today);                            // true

// Currency
formatCurrency(150.50, "PHP");             // "₱150.50"

// Rating
formatRating(4.5);                         // "4.5 / 5.0"
```

## Hooks

### useFetch Hook
Generic hook for fetching data on component mount.

```javascript
import { useFetch } from "@/shared/hooks/useFetch";

export default function MyComponent() {
  const { data, loading, error, refetch } = useFetch("/api/endpoint");

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error}</p>;

  return (
    <div>
      {/* render data */}
      <button onClick={refetch}>Refresh</button>
    </div>
  );
}
```

### useApi Hook
Hook for making manual API requests.

```javascript
import { useApi } from "@/shared/hooks/useFetch";

export default function MyForm() {
  const { execute, loading, error } = useApi();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const result = await execute("POST", "/api/endpoint", { data: "value" });
      console.log(result);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {/* form fields */}
      <button type="submit" disabled={loading}>Submit</button>
    </form>
  );
}
```

### useAuth Hook
Manages authentication state.

```javascript
import { useAuth } from "@/shared/hooks/useAuth";

export default function Dashboard() {
  const {
    user,
    loading,
    error,
    isAuthenticated,
    login,
    register,
    logout,
    refetch
  } = useAuth();

  if (loading) return <p>Checking auth...</p>;
  if (!isAuthenticated) return <p>Please log in</p>;

  return (
    <div>
      <p>Welcome, {user.fullName}</p>
      <button onClick={logout}>Logout</button>
    </div>
  );
}
```

## Components

### PawIcon Component
```javascript
import { PawIcon } from "@/shared/components/PawIcon";

// Default (peach, 24px)
<PawIcon />

// Custom color and size
<PawIcon color="#FFD8B9" size={32} />
```

### Banners
```javascript
import {
  LoadingSpinner,
  ErrorBanner,
  SuccessBanner,
  WarningBanner
} from "@/shared/components/Banners";

// Loading spinner
<LoadingSpinner message="Loading data..." />

// Error banner with close button
<ErrorBanner
  message="An error occurred"
  onClose={() => setShowError(false)}
/>

// Success banner
<SuccessBanner message="Operation successful!" />

// Warning banner
<WarningBanner message="Are you sure?" />
```

## Constants

### API Endpoints (`shared/constants/api.js`)
```javascript
import { API_ENDPOINTS } from "@/shared/constants/api";

API_ENDPOINTS.AUTH.LOGIN          // "http://localhost:8080/api/auth/login"
API_ENDPOINTS.PETS.LIST           // "http://localhost:8080/api/pets"
API_ENDPOINTS.SITTERS.GET(id)     // "http://localhost:8080/api/sitters/{id}"
API_ENDPOINTS.BOOKINGS.CREATE     // "http://localhost:8080/api/bookings"
API_ENDPOINTS.ADMIN.DASHBOARD     // "http://localhost:8080/api/admin/dashboard"
```

### Status Enums (`shared/constants/statuses.js`)
```javascript
import {
  BOOKING_STATUS,
  BOOKING_STATUS_LABELS,
  BOOKING_STATUS_COLORS,
  USER_ROLE,
  SERVICE_TYPE,
  PET_SPECIES
} from "@/shared/constants/statuses";

// Booking status
BOOKING_STATUS.PENDING             // "PENDING"
BOOKING_STATUS_LABELS.PENDING      // "Pending"
BOOKING_STATUS_COLORS.PENDING      // "#FFF9C4"

// User roles
USER_ROLE.PET_OWNER                // "PET_OWNER"
USER_ROLE.PET_SITTER               // "PET_SITTER"

// Service types
SERVICE_TYPE.WALKING               // "WALKING"
SERVICE_TYPE_LABELS.WALKING        // "Walking"

// Pet species
PET_SPECIES.DOG                    // "DOG"
PET_SPECIES_LABELS.DOG             // "Dog"
```

### Validation (`shared/constants/validation.js`)
```javascript
import {
  VALIDATION,
  VALIDATION_MESSAGES,
  validateEmail,
  validatePassword,
  validateName
} from "@/shared/constants/validation";

// Use validation functions
const emailError = validateEmail("test@example.com");   // null
const emailError = validateEmail("invalid");            // "Please enter a valid email address"

// Access validation rules
VALIDATION.EMAIL_PATTERN
VALIDATION.PASSWORD_MIN_LENGTH     // 6
VALIDATION_MESSAGES.PASSWORD_TOO_SHORT
```

## Design System

### Color Palette (from `app/globals.css`)
```css
--color-primary:        #FFD8B9   /* Soft Peach */
--color-secondary:      #FFB6C1   /* Blush Pink */
--color-success:        #B6E5D8   /* Mint Green */
--color-warning:        #FFF9C4   /* Butter Yellow */
--color-error:          #FFCCBC   /* Coral Pink */
--color-neutral-light:  #FFF8F0   /* Cream White */
--color-neutral-medium: #D3D3D3   /* Light Gray */
--color-neutral-dark:   #333333   /* Charcoal Gray */
```

## Best Practices

1. **Use Shared Components**: Always use `LoadingSpinner`, `ErrorBanner`, `SuccessBanner` for consistency
2. **Use Formatting Utils**: Use `formatDate`, `formatCurrency`, etc. for consistent display
3. **Use useAuth**: Check `isAuthenticated` before rendering protected content
4. **Use apiClient**: Never make raw fetch calls; use `apiClient.get|post|put|delete`
5. **Use Constants**: Reference status enums from `shared/constants/statuses` instead of hardcoding strings
6. **Validation**: Use validation functions from `shared/constants/validation`

## Related Documentation
- [Auth Feature](../features/auth/README.md)
- [Pets Feature](../features/pets/README.md)
- [Booking Feature](../features/booking/README.md)
- [Sitters Feature](../features/sitters/README.md)
- [Messages Feature](../features/messages/README.md)
- [Reviews Feature](../features/reviews/README.md)
- [Admin Feature](../features/admin/README.md)

---

**Created**: May 9, 2026  
**Version**: 1.0  
**Status**: Complete
