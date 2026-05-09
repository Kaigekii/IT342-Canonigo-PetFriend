# Architecture Overview - Vertical Slicing

## Before Refactoring: Horizontal Layers
```
┌─────────────────────────────────────────────────────────┐
│  REST Layer (Controllers)                               │
├─────────────────────────────────────────────────────────┤
│  AuthController    BookingController    PetController   │
│  MessageController ReviewController     SitterCtrl      │
│  AdminController   UserController                       │
├─────────────────────────────────────────────────────────┤
│  Business Logic (Services)  [optional layer]            │
├─────────────────────────────────────────────────────────┤
│  Data Transfer Objects (DTOs)                           │
│  Only AuthDtos.java                                     │
├─────────────────────────────────────────────────────────┤
│  Domain Models (Entities)                               │
│  User, Booking, Pet, Message, Review, etc.             │
├─────────────────────────────────────────────────────────┤
│  Data Access (Repositories)                             │
│  UserRepository, BookingRepository, PetRepository, etc. │
└─────────────────────────────────────────────────────────┘

Problems:
- Code scattered across multiple layers
- Hard to understand a feature completely
- Changes require updates in multiple places
- Difficult to work on independent features
```

## After Refactoring: Vertical Slices

```
Auth Feature          Bookings Feature      Pets Feature
┌──────────────────┐  ┌─────────────────────┐  ┌────────────────┐
│ AuthController   │  │ BookingController   │  │ PetController  │
├──────────────────┤  ├─────────────────────┤  ├────────────────┤
│ (no service)     │  │ BookingService      │  │ PetService     │
│ (in controller)  │  │ (business logic)    │  │ (business logic)│
├──────────────────┤  ├─────────────────────┤  ├────────────────┤
│ AuthDtos         │  │ BookingDtos         │  │ PetDtos        │
│ - Register       │  │ - CreateBooking     │  │ - CreatePet    │
│ - Login          │  │ - UpdateStatus      │  │ - PetResponse  │
│ - AuthResponse   │  │ - BookingResponse   │  │                │
├──────────────────┤  ├─────────────────────┤  ├────────────────┤
│ User (model)     │  │ Booking (model)     │  │ Pet (model)    │
│ UserRole (enum)  │  │ BookingStatus       │  │ PetSpecies     │
│                  │  │ ServiceType (enum)  │  │ VaccinationStat│
├──────────────────┤  ├─────────────────────┤  ├────────────────┤
│ UserRepository   │  │ BookingRepository   │  │ PetRepository  │
│                  │  │ UserRepository (ref)│  │ UserRepository │
│                  │  │ PetRepository (ref) │  │ (shared)       │
│                  │  │ SitterProfile Repo  │  │                │
├──────────────────┤  ├─────────────────────┤  ├────────────────┤
│ README.md        │  │ README.md           │  │ README.md      │
│ (API docs)       │  │ (API docs)          │  │ (API docs)     │
└──────────────────┘  └─────────────────────┘  └────────────────┘
        ▲                     ▲                        ▲
        └─────────────────────┴────────────────────────┘
                        │
        ┌───────────────┴────────────────┐
        │                                │
    Shared Layer              Remaining Features
 ┌─────────────────┐    ┌──────────────────────┐
 │ Exception       │    │ Sitters Feature      │
 │ Utilities       │    │ Messages Feature     │
 │ Constants       │    │ Reviews Feature      │
 │ Validators      │    │ Admin Feature        │
 │ Config          │    │                      │
 │ Security        │    │ (Ready for Phase 2)  │
 │ Models (shared) │    └──────────────────────┘
 │ Repositories    │
 │ (shared)        │
 └─────────────────┘

Benefits:
✓ Complete feature in one place
✓ Easy to understand and maintain
✓ Independent development
✓ Clear dependencies
✓ Self-documenting
✓ Easy to test
```

## Data Flow: Booking Creation Example

### Request → Response Flow

```
Frontend (Next.js)
     │
     │ POST /api/bookings
     │ { sitterId, petIds, date, startTime, endTime }
     │
     ▼
BookingController (features/bookings/)
     │
     ├─ Get authenticated user
     ├─ Validate user is PET_OWNER
     │
     ▼
BookingService.createBooking()
     │
     ├─ Fetch owner user (UserRepository)
     ├─ Validate owner role
     │
     ├─ Fetch sitter user (UserRepository)
     ├─ Validate sitter role & exists
     │
     ├─ Fetch pets (PetRepository)
     ├─ Validate all pets exist
     ├─ Validate ownership
     │
     ├─ Fetch sitter profile (SitterProfileRepository)
     ├─ Validate hourly rate configured
     │
     ├─ Calculate pricing:
     │  - duration = (endTime - startTime) / 60
     │  - baseAmount = hourlyRate × duration
     │  - serviceFee = baseAmount × 10%
     │  - total = baseAmount + serviceFee
     │
     ├─ Create Booking entity
     │
     ▼
BookingRepository.save()
     │
     ▼ (Persists to database)
     
     ▲
     │
BookingDtos.BookingResponse.from(booking)
     │
     ├─ Extract booking data
     ├─ Extract owner info
     ├─ Extract sitter info
     ├─ Extract pet names
     │
     ▼
Response JSON
     │
     └─► Frontend (React)
```

## Component Dependencies

```
Auth Feature (No dependencies)
     ▲
     │
     ├── BookingController ────┐
     ├── PetController ────────┤
     ├── SitterController ─────┤ Depends on auth for
     ├── MessageController ────┤ JWT tokens
     └── AdminController ──────┘
     
BookingController depends on:
  ├─ UserRepository (get authenticated user)
  ├─ BookingRepository (save/fetch)
  ├─ PetRepository (validate pets)
  ├─ SitterProfileRepository (get hourly rate)
  └─ BookingService (business logic)

PetController depends on:
  ├─ UserRepository (get owner)
  ├─ PetRepository (CRUD)
  └─ PetService (business logic)
```

## Frontend Structure (Next Phase)

```
Web App (Next.js)
│
├─ /app/
│  ├─ layout.js (root layout)
│  ├─ page.js (home)
│  ├─ login/page.js (route handler)
│  ├─ register/page.js
│  ├─ petowner/dashboard/page.js
│  └─ ... (all route handlers)
│
├─ /features/ (FEATURE SLICES)
│  │
│  ├─ /auth/
│  │  ├─ /components/
│  │  │  ├─ LoginForm.js
│  │  │  ├─ RegisterForm.js
│  │  │  └─ RoleSelector.js
│  │  ├─ /hooks/
│  │  │  └─ useAuth.js
│  │  ├─ api.js (auth API calls)
│  │  └─ types.js
│  │
│  ├─ /booking/
│  │  ├─ /components/
│  │  │  ├─ BookingForm.js
│  │  │  ├─ BookingList.js
│  │  │  └─ BookingCard.js
│  │  ├─ /hooks/
│  │  │  └─ useBooking.js
│  │  ├─ api.js
│  │  └─ types.js
│  │
│  ├─ /pets/
│  ├─ /sitters/
│  ├─ /messages/
│  └─ /admin/
│
└─ /shared/ (SHARED UTILITIES)
   ├─ /api/
   │  ├─ client.js (axios instance)
   │  └─ errorHandler.js
   │
   ├─ /components/
   │  ├─ Layout.js
   │  ├─ Header.js
   │  ├─ Button.js
   │  └─ ...
   │
   ├─ /hooks/
   │  ├─ useLocalStorage.js
   │  ├─ useFetch.js
   │  └─ ...
   │
   ├─ /utils/
   │  ├─ format.js
   │  ├─ validators.js
   │  └─ ...
   │
   ├─ /constants/
   │  ├─ api.js
   │  ├─ status.js
   │  └─ roles.js
   │
   └─ /context/
      └─ AuthContext.js
```

## Deployment Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     React Frontend                          │
│                   (Single Page App)                         │
│  /features/ (auth, booking, pets, sitters, messages)       │
│  /shared/ (API client, components, hooks, utils)           │
└────────────────────────────┬────────────────────────────────┘
                             │
                    REST API Calls (JSON)
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│            Spring Boot Backend (Vertical Slices)            │
│                                                              │
│  /features/                                                 │
│  ├─ /auth/       → /api/auth/*                             │
│  ├─ /bookings/   → /api/bookings/*                         │
│  ├─ /pets/       → /api/pets/*                             │
│  ├─ /sitters/    → /api/sitters/*                          │
│  ├─ /messages/   → /api/messages/*                         │
│  ├─ /reviews/    → /api/reviews/*                          │
│  └─ /admin/      → /api/admin/*                            │
│                                                              │
│  /config, /security, /repository (shared)                  │
└────────────────────────────┬────────────────────────────────┘
                             │
                    SQL Queries / ORM
                             │
                             ▼
                    ┌──────────────────┐
                    │  PostgreSQL DB   │
                    │  (Data Storage)  │
                    └──────────────────┘
```

---

**Architecture Diagram Created**: May 9, 2026  
**Status**: Phase 1 Complete - Vertical Slicing Implemented
