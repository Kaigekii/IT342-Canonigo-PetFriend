# Vertical Slicing Quick Reference

## Backend File Locations (New Structure)

### Auth Feature
```
✅ /features/auth/AuthController.java
✅ /features/auth/AuthDtos.java
📖 /features/auth/README.md
```

### Bookings Feature
```
✅ /features/bookings/BookingController.java
✅ /features/bookings/BookingService.java
✅ /features/bookings/BookingDtos.java
📖 /features/bookings/README.md
```

### Pets Feature
```
✅ /features/pets/PetController.java
✅ /features/pets/PetService.java
✅ /features/pets/PetDtos.java
📖 /features/pets/README.md
```

### Ready for Phase 2
```
📁 /features/sitters/ (SitterController, SitterProfileController)
📁 /features/messages/ (MessageController)
📁 /features/reviews/ (ReviewController)
📁 /features/admin/ (AdminController)
```

---

## Frontend Folder Structure (New)

### Features
```
✅ /features/auth/          - AuthContext, LoginForm, RegisterForm, RoleSelector, useAuth hook
✅ /features/booking/       - BookingForm, BookingList, BookingCard, SitterRequests, useBooking hook
✅ /features/pets/          - PetForm, PetList, PetCard, usePets hook
✅ /features/sitters/       - SitterSearch, SitterCard, SitterProfile, useSitters hook
✅ /features/messages/      - MessageThread, MessageList, MessageInput, useMessages hook
```

### Shared
```
📁 /shared/api/             - API client, error handlers
📁 /shared/components/      - Layout, Header, Button, etc.
📁 /shared/hooks/           - useLocalStorage, useFetch, etc.
📁 /shared/utils/           - Formatters, validators, helpers
📁 /shared/constants/       - API endpoints, statuses, roles
📁 /shared/context/         - AuthContext (global state)
```

---

## Key Design Patterns Used

### Backend
1. **Service Layer**: `BookingService`, `PetService` encapsulate business logic
2. **DTOs**: Request/Response DTOs (CreateBookingRequest, BookingResponse, etc.)
3. **Custom Exceptions**: `BookingException`, `PetException` for error handling
4. **Validation**: In service layer using Java validation annotations

### Frontend (Next Phase)
1. **Custom Hooks**: `useAuth()`, `useBooking()`, `usePets()` for state management
2. **Feature Modules**: Each feature self-contained with components, hooks, API calls
3. **Context API**: `AuthContext` for global authentication state
4. **Separation of Concerns**: UI components separate from business logic

---

## API Endpoints Summary

### Auth
```
POST   /api/auth/register
POST   /api/auth/login
```

### Bookings (Owner)
```
GET    /api/bookings
GET    /api/bookings?upcoming=true
POST   /api/bookings
PUT    /api/bookings/{id}/owner-status
```

### Bookings (Sitter)
```
GET    /api/bookings/sitter
GET    /api/bookings/sitter/pending
GET    /api/bookings/sitter/upcoming
GET    /api/bookings/sitter/today
PUT    /api/bookings/{id}/sitter-status
```

### Pets
```
GET    /api/pets
POST   /api/pets
PUT    /api/pets/{id}
DELETE /api/pets/{id}
```

---

## Testing Checklist

- [ ] Auth registration (PET_OWNER)
- [ ] Auth registration (PET_SITTER - unverified)
- [ ] Auth login (valid credentials)
- [ ] Auth login (invalid credentials)
- [ ] Create booking (owner)
- [ ] List bookings (owner)
- [ ] List pending bookings (sitter)
- [ ] Update booking status (sitter - confirm/cancel)
- [ ] Update booking status (owner - cancel)
- [ ] Invalid status transitions (should fail)
- [ ] Create pet (owner)
- [ ] List pets (owner)
- [ ] Update pet (owner)
- [ ] Delete pet (owner)
- [ ] Delete pet (not owner - should fail)

---

## Git Commit Suggestions

```bash
# After each feature slice completion
git add features/auth/
git commit -m "refactor(auth): vertical slice - auth feature organization"

git add features/bookings/
git commit -m "refactor(bookings): vertical slice - bookings service and DTOs"

git add features/pets/
git commit -m "refactor(pets): vertical slice - pet management feature"

# After all core features
git add VERTICAL_SLICING_GUIDE.md REFACTORING_PHASE1_SUMMARY.md
git commit -m "docs: add vertical slicing refactoring documentation"
```

---

## Notes for Next Phase

1. **Don't break old imports yet** - Keep old controller files until everything is tested
2. **Update @ComponentScan if needed** - Verify Spring finds all components
3. **Test thoroughly** - Same logic, different organization
4. **Reuse DTOs** - Use created DTOs across frontend too
5. **Document changes** - Add feature-specific READMEs as you go
6. **Keep it modular** - Each feature should be independently testable

---

**Last Updated**: May 9, 2026  
**Refactoring Status**: Phase 1 Complete ✅
