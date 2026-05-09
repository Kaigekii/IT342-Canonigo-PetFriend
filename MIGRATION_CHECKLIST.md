# Migration Checklist & Transition Guide

## Phase 1: Core Features Refactoring ✅ DONE

### Backend Completed
- [x] Auth feature slice created (Controller, DTOs, README)
- [x] Bookings feature slice created (Controller, Service, DTOs, README)
- [x] Pets feature slice created (Controller, Service, DTOs, README)
- [x] Documentation created (VERTICAL_SLICING_GUIDE, PHASE1_SUMMARY, QUICK_REFERENCE)

### Frontend Folder Structure Ready
- [x] `/features/` directory structure created
- [x] `/shared/` directory structure created
- [x] All subdirectories prepared (auth, booking, pets, sitters, messages, admin)

---

## Phase 2: Testing & Validation (TODO)

### Local Testing
- [ ] Run `mvn test` to verify no import errors
- [ ] Test Auth endpoints:
  - [ ] POST /api/auth/register (PET_OWNER)
  - [ ] POST /api/auth/register (PET_SITTER)
  - [ ] POST /api/auth/login
- [ ] Test Booking endpoints:
  - [ ] GET /api/bookings
  - [ ] POST /api/bookings
  - [ ] GET /api/bookings/sitter
  - [ ] PUT /api/bookings/{id}/sitter-status
- [ ] Test Pet endpoints:
  - [ ] GET /api/pets
  - [ ] POST /api/pets
  - [ ] PUT /api/pets/{id}
  - [ ] DELETE /api/pets/{id}

### End-to-End Testing
- [ ] Complete auth flow (register -> login -> token)
- [ ] Complete booking flow (create -> update status -> retrieve)
- [ ] Complete pet flow (create -> list -> update -> delete)
- [ ] Test authorization (verify role checks work)
- [ ] Test validation (invalid input should fail)

---

## Phase 2: Remaining Backend Features (TODO)

### Files to Create

#### Sitters Feature
```
Create:
- /features/sitters/SitterController.java
- /features/sitters/SitterService.java (optional, if complex logic)
- /features/sitters/SitterDtos.java
- /features/sitters/README.md

Refactor from:
- /controller/SitterController.java
- /controller/SitterProfileController.java
```

#### Messages Feature
```
Create:
- /features/messages/MessageController.java
- /features/messages/MessageService.java (optional)
- /features/messages/MessageDtos.java
- /features/messages/README.md

Refactor from:
- /controller/MessageController.java
```

#### Reviews Feature
```
Create:
- /features/reviews/ReviewController.java
- /features/reviews/ReviewService.java (optional)
- /features/reviews/ReviewDtos.java
- /features/reviews/README.md

Refactor from:
- /controller/ReviewController.java
```

#### Admin Feature
```
Create:
- /features/admin/AdminController.java
- /features/admin/AdminService.java (optional)
- /features/admin/AdminDtos.java
- /features/admin/README.md

Refactor from:
- /controller/AdminController.java
```

---

## Phase 3: Frontend Feature Organization (TODO)

### Auth Feature Components
```
Organize into /features/auth/components/:
- LoginForm.js
- RegisterForm.js
- RoleSelector.js

Create:
- /features/auth/hooks/useAuth.js
- /features/auth/api.js (auth API calls)
- /features/auth/types.js (type definitions)
- /features/auth/constants.js (auth constants)
```

### Booking Feature Components
```
Organize into /features/booking/components/:
- BookingForm.js
- BookingList.js
- BookingCard.js
- SitterRequests.js
- BookingStatusBadge.js

Create:
- /features/booking/hooks/useBooking.js
- /features/booking/api.js
- /features/booking/types.js
- /features/booking/constants.js (booking statuses, service types)
```

### Pets Feature Components
```
Organize into /features/pets/components/:
- PetForm.js
- PetList.js
- PetCard.js

Create:
- /features/pets/hooks/usePets.js
- /features/pets/api.js
- /features/pets/types.js
- /features/pets/constants.js (pet species, vaccination status)
```

### Sitters Feature Components
```
Organize into /features/sitters/components/:
- SitterSearch.js
- SitterCard.js
- SitterProfile.js
- SitterDetails.js

Create:
- /features/sitters/hooks/useSitters.js
- /features/sitters/api.js
- /features/sitters/types.js
```

### Messages Feature Components
```
Organize into /features/messages/components/:
- MessageThread.js
- MessageList.js
- MessageInput.js

Create:
- /features/messages/hooks/useMessages.js
- /features/messages/api.js
- /features/messages/types.js
```

---

## Phase 3: Shared Utilities (TODO)

### API Layer
```
/shared/api/
- client.js           (Axios instance with auth headers)
- errorHandler.js     (Error response handling)
- constants.js        (API base URL, endpoints)
```

### Components
```
/shared/components/
- Layout.js
- Header.js
- Sidebar.js
- Button.js
- Card.js
- Modal.js
- Form.js
- Input.js
- etc.
```

### Hooks
```
/shared/hooks/
- useLocalStorage.js
- useFetch.js
- useAsync.js
- useDebounce.js
```

### Utils
```
/shared/utils/
- format.js           (Date, currency, string formatting)
- validators.js       (Form validation)
- helpers.js          (Common utility functions)
```

### Constants
```
/shared/constants/
- api.js              (API endpoints)
- roles.js            (User roles)
- status.js           (Booking, pet, sitter statuses)
- species.js          (Pet species)
```

### Context
```
/shared/context/
- AuthContext.js      (Global auth state)
- AuthProvider.js     (Auth state provider)
```

---

## Phase 4: Old Code Cleanup (TODO)

### After Everything Works
```
Remove old files:
- /controller/AuthController.java (old)
- /controller/BookingController.java (old)
- /controller/PetController.java (old)
- /dto/AuthDtos.java (old - using new one in features/auth/)

Verify:
- [ ] No imports from old locations
- [ ] All tests still pass
- [ ] No dead code references
- [ ] Build succeeds
```

---

## Git Workflow

### Create feature branches per phase:

```bash
# Phase 2: Backend remaining features
git checkout -b refactor/backend-phase2-sitters
git checkout -b refactor/backend-phase2-messages
git checkout -b refactor/backend-phase2-reviews
git checkout -b refactor/backend-phase2-admin

# Phase 3: Frontend features
git checkout -b refactor/frontend-phase3-auth-components
git checkout -b refactor/frontend-phase3-booking-components
git checkout -b refactor/frontend-phase3-shared-utils

# Phase 4: Cleanup
git checkout -b refactor/cleanup-old-code
```

### Commit strategy:

```bash
# Each feature gets a commit
git add features/sitters/
git commit -m "refactor(sitters): vertical slice - sitter discovery feature"

git add features/messages/
git commit -m "refactor(messages): vertical slice - messaging feature"

# After all phases
git commit --allow-empty -m "refactor: vertical slicing complete - all phases"
```

---

## Validation Steps

### Before Each Commit
```bash
# Backend
mvn clean build
mvn test

# Frontend
npm run lint
npm run build
npm test
```

### Before Merging to Main
```bash
# Run full test suite
mvn test
npm test

# Check test coverage
mvn test jacoco:report

# Manual testing
- Test complete booking flow
- Test complete auth flow
- Test complete pet management
- Verify all API endpoints work
```

---

## Troubleshooting

### Issue: Import errors after refactoring
**Solution**: 
- Update all imports to new package path
- Check `@ComponentScan` in Spring config
- Verify package structure matches

### Issue: Tests fail after moving files
**Solution**:
- Tests often use old package names
- Update test imports to new locations
- Verify test dependencies resolve correctly

### Issue: Spring can't find components
**Solution**:
```java
@ComponentScan({
    "edu.cit.canonigo.petfriend.features",
    "edu.cit.canonigo.petfriend.config",
    "edu.cit.canonigo.petfriend.security"
})
```

### Issue: DTO serialization issues
**Solution**:
- Ensure all DTOs have getters/setters
- Check Jackson annotations if needed
- Verify DTO package imports in controllers

---

## Success Criteria ✅

- [x] Phase 1 Backend complete
- [ ] Phase 2 Backend complete (Sitters, Messages, Reviews, Admin)
- [ ] All backend tests passing
- [ ] Phase 3 Frontend organized
- [ ] All frontend tests passing
- [ ] Old code removed
- [ ] No dead imports
- [ ] Build succeeds
- [ ] All tests pass (unit + integration)
- [ ] Manual testing complete
- [ ] Ready to merge to main

---

## Timeline Estimate

- **Phase 1**: ✅ Done (3 hours)
- **Phase 2**: ~2-3 hours (remaining backend)
- **Phase 3**: ~3-4 hours (frontend components)
- **Phase 4**: ~1-2 hours (testing and cleanup)
- **Total**: ~10-12 hours of focused work

---

**Last Updated**: May 9, 2026  
**Status**: Phase 1 Complete, Ready for Phase 2 ✅
