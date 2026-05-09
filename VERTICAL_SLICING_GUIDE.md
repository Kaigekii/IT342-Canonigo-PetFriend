# Vertical Slicing Refactoring Guide

## Overview

This document outlines the vertical slicing refactoring for the PetFriend application. The refactoring organizes code by features (slices) rather than by technical layers (controllers, services, DTOs, models).

## Backend Architecture

### Before: Horizontal Slicing (Layer-based)
```
backend/src/main/java/edu/cit/canonigo/petfriend/
├── controller/
│   ├── AuthController.java
│   ├── BookingController.java
│   ├── PetController.java
│   └── ...
├── service/          (if existed)
├── dto/
│   └── AuthDtos.java
├── model/
│   ├── User.java
│   ├── Booking.java
│   └── ...
└── repository/
```

### After: Vertical Slicing (Feature-based)
```
backend/src/main/java/edu/cit/canonigo/petfriend/
├── features/
│   ├── auth/
│   │   ├── AuthController.java
│   │   ├── AuthDtos.java
│   │   └── (AuthService if needed)
│   │
│   ├── bookings/
│   │   ├── BookingController.java
│   │   ├── BookingDtos.java
│   │   ├── BookingService.java
│   │   └── (BookingRepository moved to shared if global, or stays in repo/)
│   │
│   ├── pets/
│   │   ├── PetController.java
│   │   ├── PetDtos.java
│   │   └── (services as needed)
│   │
│   ├── sitters/
│   │   ├── SitterController.java
│   │   ├── SitterDtos.java
│   │   └── SitterProfileController.java
│   │
│   ├── messages/
│   │   ├── MessageController.java
│   │   └── MessageDtos.java
│   │
│   ├── reviews/
│   │   ├── ReviewController.java
│   │   └── ReviewDtos.java
│   │
│   └── admin/
│       ├── AdminController.java
│       └── AdminDtos.java
│
├── shared/
│   ├── exceptions/
│   ├── utils/
│   └── constants/
│
├── config/
├── model/          (Shared domain models)
├── repository/     (Shared data access)
├── security/       (Shared security)
└── PetfriendApplication.java
```

## Frontend Architecture

### Before: Route-based Organization
```
web/src/app/
├── login/page.js
├── register/page.js
├── petowner/
│   ├── dashboard/page.js
│   ├── pets/page.js
│   ├── bookings/page.js
│   └── messages/page.js
├── petsitter/
│   ├── dashboard/page.js
│   ├── requests/page.js
│   └── messages/page.js
└── admin/...
```

### After: Feature-based Organization
```
web/src/
├── app/            (App router and layout)
├── features/
│   ├── auth/
│   │   ├── components/
│   │   │   ├── LoginForm.js
│   │   │   ├── RegisterForm.js
│   │   │   └── RoleSelector.js
│   │   ├── hooks/
│   │   │   └── useAuth.js
│   │   ├── api.js
│   │   └── types.js
│   │
│   ├── booking/
│   │   ├── components/
│   │   │   ├── BookingForm.js
│   │   │   ├── BookingList.js
│   │   │   └── BookingCard.js
│   │   ├── hooks/
│   │   │   └── useBooking.js
│   │   ├── api.js
│   │   └── types.js
│   │
│   ├── pets/
│   │   ├── components/
│   │   │   ├── PetForm.js
│   │   │   ├── PetList.js
│   │   │   └── PetCard.js
│   │   ├── hooks/
│   │   │   └── usePets.js
│   │   ├── api.js
│   │   └── types.js
│   │
│   ├── sitters/
│   │   ├── components/
│   │   │   ├── SitterSearch.js
│   │   │   ├── SitterCard.js
│   │   │   ├── SitterProfile.js
│   │   │   └── SitterDetails.js
│   │   ├── hooks/
│   │   │   └── useSitters.js
│   │   ├── api.js
│   │   └── types.js
│   │
│   ├── messages/
│   │   ├── components/
│   │   │   ├── MessageThread.js
│   │   │   ├── MessageList.js
│   │   │   └── MessageInput.js
│   │   ├── hooks/
│   │   │   └── useMessages.js
│   │   ├── api.js
│   │   └── types.js
│   │
│   └── admin/
│       ├── components/
│       └── hooks/
│
├── shared/
│   ├── api/
│   │   ├── client.js       (Axios instance)
│   │   └── errorHandler.js
│   │
│   ├── components/         (Shared UI components)
│   │   ├── Layout.js
│   │   ├── Header.js
│   │   ├── Sidebar.js
│   │   ├── Button.js
│   │   └── ...
│   │
│   ├── hooks/             (Shared custom hooks)
│   │   ├── useLocalStorage.js
│   │   ├── useFetch.js
│   │   └── ...
│   │
│   ├── utils/
│   │   ├── format.js
│   │   └── validators.js
│   │
│   ├── constants/
│   │   ├── api.js
│   │   ├── status.js
│   │   └── roles.js
│   │
│   └── context/
│       └── AuthContext.js
│
└── app/
    ├── layout.js
    ├── page.js
    ├── globals.css
    ├── login/page.js          (Route handler)
    ├── register/
    │   ├── page.js            (Route handler)
    │   ├── owner/page.js
    │   └── sitter/page.js
    ├── petowner/
    │   ├── dashboard/page.js
    │   ├── pets/page.js
    │   ├── bookings/page.js
    │   ├── find-sitter/page.js
    │   └── messages/page.js
    └── ...
```

## Vertical Slicing Benefits

1. **Cohesion**: All related code for a feature (UI, logic, API calls) is co-located
2. **Scalability**: Adding a new feature doesn't spread changes across multiple layers
3. **Modularity**: Each slice can be developed and tested independently
4. **Maintainability**: Related changes stay in one place
5. **Reusability**: Shared utilities extracted to `/shared` folder

## Migration Strategy

### Phase 1: Extract Backend Features (IN PROGRESS)
- [x] Create `features/` folder structure
- [x] Extract Bookings feature (BookingController, BookingDtos, BookingService)
- [x] Extract Auth feature (AuthController, AuthDtos)
- [ ] Extract Pets feature
- [ ] Extract Sitters feature
- [ ] Extract Messages feature
- [ ] Extract Reviews feature
- [ ] Extract Admin feature
- [ ] Update all import statements
- [ ] Verify all tests pass
- [ ] Update Spring component scanning if needed

### Phase 2: Organize Frontend Features (IN PROGRESS)
- [x] Create `/features` and `/shared` structure
- [ ] Create Auth feature folder and move components
- [ ] Create Booking feature folder and move components
- [ ] Create Sitters feature folder and move components
- [ ] Create Pets feature folder and move components
- [ ] Create Messages feature folder and move components
- [ ] Extract shared utilities and hooks
- [ ] Update page.js files to use feature components
- [ ] Update imports across the app

### Phase 3: Cross-slice Concerns
- [ ] Move shared models to `/shared/types` or `/shared/models`
- [ ] Move shared repositories to `/shared/repository` if needed
- [ ] Extract common validation logic
- [ ] Create shared error handling utilities
- [ ] Document API contracts

## Important Notes

1. **Repositories**: Can stay in a central location or moved per-feature based on preference
2. **Models**: Currently shared; consider if they should stay central or move to features
3. **Spring Configuration**: May need to update component scanning paths
4. **Imports**: Update all import statements when moving files
5. **Tests**: Ensure test organization mirrors feature structure

## Example: Complete Booking Slice

### Backend
```
features/bookings/
├── BookingController.java
├── BookingService.java
├── BookingDtos.java
├── BookingException.java
└── README.md (slice documentation)
```

### Frontend
```
features/booking/
├── api.js                 (All booking API calls)
├── types.js               (TypeScript-like type definitions)
├── hooks/
│   └── useBooking.js      (Booking state management)
├── components/
│   ├── BookingForm.js     (Create booking)
│   ├── BookingList.js     (Owner's bookings)
│   ├── BookingCard.js     (Booking item)
│   └── SitterRequests.js  (Sitter's requests)
└── README.md
```

## Gradual Migration Path

Since the app is already in production, follow this approach:

1. **Branch Strategy**: Use feature branch per slice
2. **Testing**: Run full test suite after each feature extraction
3. **Backward Compatibility**: Ensure old and new locations work during transition
4. **Gradual Rollout**: Extract one slice, test, merge, repeat

## Configuration Changes Needed

### Spring Boot (if needed):
```java
@ComponentScan({
    "edu.cit.canonigo.petfriend.features",
    "edu.cit.canonigo.petfriend.config",
    "edu.cit.canonigo.petfriend.security"
})
```

### Next.js: No changes needed (uses file-based routing)

---

**Last Updated**: May 9, 2026
**Status**: In Progress - Phase 1 & 2
