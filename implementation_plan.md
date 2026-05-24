# Mobile Application Feature Completion Plan

This plan outlines the approach to implementing all the remaining features for the **Pet Owner** and **Pet Sitter** flows in the Android app, while ignoring the Admin features as requested.

## User Review Required

> [!WARNING]
> Implementing **all** of these features at once involves creating dozens of classes, fragments, and API interfaces. 
> To ensure quality and prevent the codebase from breaking, it is highly recommended to execute this plan in **Phases (Slices)**.

## Phase 1: Pet Owner Features (Excluding Dashboard)
We will expand the Pet Owner flow to include the remaining tabs from the bottom navigation.

### 1. Find Sitters Slice (`features/sitters`)
- **API**: Add `GET /api/sitters/search` and `GET /api/sitters/{id}`.
- **Models**: Create `SitterSummary` and `SitterDetail` models.
- **UI**: 
  - `FindSitterFragment`: A screen to search and filter sitters by location/service type.
  - `SitterProfileActivity`: A detailed view of the sitter's profile and reviews, with a "Book Now" button.

### 2. Pet Management Slice (`features/pets`)
- **API**: Add `POST /api/pets`, `PUT /api/pets/{id}`, and `DELETE /api/pets/{id}`.
- **UI**: 
  - `PetListFragment`: A dedicated screen showing all pets in detail.
  - `AddEditPetActivity`: A form to add new pets or edit existing ones.

### 3. Bookings Slice (`features/bookings`)
- **API**: Add `POST /api/bookings` and `PUT /api/bookings/{id}/owner-status`.
- **UI**: 
  - `OwnerBookingsFragment`: A tab to view pending, active, and completed bookings.
  - `CreateBookingActivity`: The checkout flow triggered from the Sitter Profile.

## Phase 2: Pet Sitter Features
We will build out the dedicated flow for the Pet Sitter role. Currently, sitters are routed to the legacy dashboard.

### 1. Sitter Main Architecture (`features/dashboard`)
- **UI**: Create `SitterMainActivity` with a bottom navigation bar tailored for sitters (Home, Requests, Messages, Profile).
- **Routing**: Update `LoginActivity` and `MainActivity` to route `PET_SITTER` roles to this new activity.

### 2. Sitter Dashboard & Schedule (`features/dashboard`)
- **API**: Add `GET /api/bookings/sitter/today` and `GET /api/bookings/sitter/upcoming`.
- **UI**: `SitterHomeFragment` displaying today's appointments and upcoming confirmed sessions.

### 3. Sitter Requests (`features/bookings`)
- **API**: Add `GET /api/bookings/sitter/pending` and `PUT /api/bookings/{id}/sitter-status`.
- **UI**: `SitterRequestsFragment` showing incoming booking requests with "Accept" and "Decline" buttons.

## Phase 3: Shared Features
These features apply to both roles and will be implemented last.

### 1. Profiles (`features/profile`)
- **API**: Extend `/api/user/me` logic if necessary to handle updates.
- **UI**: `ProfileFragment` to edit user details, logout, and manage settings.

### 2. Messages (`features/messages`)
- **UI**: `MessageListFragment` and `ChatActivity`. *(Note: Requires backend messaging endpoints to be fully functional or mock data if not yet implemented)*.

## Open Questions
1. **Execution Order**: Are you okay with me starting with **Phase 1: Pet Owner Features** (Find Sitters, Pet Management, Booking Creation) first?
2. **Messages**: Does the backend currently have working `/api/messages` endpoints, or should I stub the UI for Messages for now?
