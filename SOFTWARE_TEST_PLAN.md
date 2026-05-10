# PetFriend - Software Test Plan (Part 3)

**Project:** PetFriend - Pet Care Marketplace  
**Version:** 1.0  
**Date:** May 10, 2026  
**Scope:** Full Regression Testing Post-Vertical Slice Refactoring  

---

## 1. Introduction

This Software Test Plan defines the testing strategy, scope, and approach for validating all functional requirements of the PetFriend system after completing the Vertical Slice Architecture refactoring (Phases 1-4).

### 1.1 Purpose
- Ensure all features remain functional after refactoring
- Validate vertical slice separation doesn't break existing functionality
- Document comprehensive test coverage
- Provide baseline for future regression testing

### 1.2 Scope
- **Backend:** Java Spring Boot microservices (Auth, Sitters, Bookings, Messages, Reviews, Pets, Admin)
- **Frontend:** Next.js web application (Pet Owner, Pet Sitter, Admin roles)
- **Mobile:** Android application
- **Database:** PostgreSQL

---

## 2. Functional Requirements Coverage

### 2.1 Authentication Feature Tests

| Req ID | Requirement | Test Case ID | Status |
|--------|-------------|--------------|--------|
| AUTH-1 | User Registration (Pet Owner) | TC-AUTH-001 | ⭕ |
| AUTH-2 | User Registration (Pet Sitter) | TC-AUTH-002 | ⭕ |
| AUTH-3 | User Registration (Admin) | TC-AUTH-003 | ⭕ |
| AUTH-4 | User Login | TC-AUTH-004 | ⭕ |
| AUTH-5 | JWT Token Generation | TC-AUTH-005 | ⭕ |
| AUTH-6 | Get Current User Profile (/api/user/me) | TC-AUTH-006 | ⭕ |
| AUTH-7 | Password Validation (8+ chars, 1 uppercase, 1 number, 1 special) | TC-AUTH-008 | ⭕ |
| AUTH-8 | Duplicate Email Prevention | TC-AUTH-009 | ⭕ |
| AUTH-9 | Role-based Redirect After Login | TC-AUTH-010 | ⭕ |

### 2.2 Pet Management Feature Tests

| Req ID | Requirement | Test Case ID | Status |
|--------|-------------|--------------|--------|
| PET-1 | Add Pet (Pet Owner) | TC-PET-001 | ⭕ |
| PET-2 | View Pet List | TC-PET-002 | ⭕ |
| PET-3 | Edit Pet Details | TC-PET-003 | ⭕ |
| PET-4 | Delete Pet | TC-PET-004 | ⭕ |
| PET-5 | Pet Photo Upload | TC-PET-005 | ⭕ |
| PET-6 | Vaccination Status Tracking | TC-PET-006 | ⭕ |

### 2.3 Sitter Management Feature Tests

| Req ID | Requirement | Test Case ID | Status |
|--------|-------------|--------------|--------|
| SITTER-1 | Create Sitter Profile | TC-SITTER-001 | ⭕ |
| SITTER-2 | Update Sitter Profile | TC-SITTER-002 | ⭕ |
| SITTER-3 | Set Availability Schedule | TC-SITTER-003 | ⭕ |
| SITTER-4 | Set Hourly Rate | TC-SITTER-004 | ⭕ |
| SITTER-5 | Upload Verification Documents | TC-SITTER-005 | ⭕ |
| SITTER-6 | Search Sitters by Location | TC-SITTER-006 | ⭕ |
| SITTER-7 | Filter Sitters by Service Type | TC-SITTER-007 | ⭕ |
| SITTER-8 | View Sitter Details & Reviews | TC-SITTER-008 | ⭕ |

### 2.4 Booking Feature Tests

| Req ID | Requirement | Test Case ID | Status |
|--------|-------------|--------------|--------|
| BOOKING-1 | Create Booking | TC-BOOKING-001 | ⭕ |
| BOOKING-2 | View Booking Details | TC-BOOKING-002 | ⭕ |
| BOOKING-3 | Cancel Booking | TC-BOOKING-003 | ⭕ |
| BOOKING-4 | View Booking History | TC-BOOKING-004 | ⭕ |
| BOOKING-5 | Mark Booking Complete | TC-BOOKING-005 | ⭕ |
| BOOKING-6 | Prevent Double Booking | TC-BOOKING-006 | ⭕ |

### 2.5 Reviews & Ratings Feature Tests

| Req ID | Requirement | Test Case ID | Status |
|--------|-------------|--------------|--------|
| REVIEW-1 | Submit Review (1-5 stars) | TC-REVIEW-001 | ⭕ |
| REVIEW-2 | View Sitter Ratings | TC-REVIEW-002 | ⭕ |
| REVIEW-3 | Calculate Average Rating | TC-REVIEW-003 | ⭕ |
| REVIEW-4 | One Review Per Booking | TC-REVIEW-004 | ⭕ |

### 2.6 Messaging Feature Tests

| Req ID | Requirement | Test Case ID | Status |
|--------|-------------|--------------|--------|
| MESSAGE-1 | Send Message | TC-MESSAGE-001 | ⭕ |
| MESSAGE-2 | View Message Thread | TC-MESSAGE-002 | ⭕ |
| MESSAGE-3 | Receive Messages | TC-MESSAGE-003 | ⭕ |

### 2.7 Admin Panel Feature Tests

| Req ID | Requirement | Test Case ID | Status |
|--------|-------------|--------------|--------|
| ADMIN-1 | View Pending Sitter Applications | TC-ADMIN-001 | ⭕ |
| ADMIN-2 | Approve Sitter Application | TC-ADMIN-002 | ⭕ |
| ADMIN-3 | Reject Sitter Application | TC-ADMIN-003 | ⭕ |
| ADMIN-4 | View All Users | TC-ADMIN-004 | ⭕ |
| ADMIN-5 | View All Bookings | TC-ADMIN-005 | ⭕ |

---

## 3. Test Cases by Feature

### 3.1 Authentication Test Cases

#### TC-AUTH-001: User Registration - Pet Owner
```
Preconditions:
  - User is on the registration page
  - Database contains no user with test email

Steps:
  1. Click "Create Account"
  2. Select "Pet Owner" role
  3. Enter: first name, last name, email, password (with 1 uppercase, 1 number, 1 special char)
  4. Confirm password
  5. Click "Register"

Expected Results:
  - Account created successfully
  - User stored in database with PET_OWNER role
  - isVerified = null (not applicable for owners)
  - User redirected to /petowner/dashboard
  - JWT token generated and stored in localStorage
```

#### TC-AUTH-002: User Registration - Pet Sitter
```
Preconditions:
  - User is on the registration page
  - Database contains no user with test email

Steps:
  1. Click "Create Account"
  2. Select "Pet Sitter" role
  3. Enter: first name, last name, email, password
  4. Click "Register"

Expected Results:
  - Account created successfully
  - User stored with PET_SITTER role
  - isVerified = false (pending admin approval)
  - User redirected to /petsitter/dashboard
  - Sitter appears in Admin pending approval list
```

#### TC-AUTH-004: User Login
```
Preconditions:
  - User account exists
  - User has valid credentials

Steps:
  1. Navigate to /login
  2. Enter valid email
  3. Enter valid password
  4. Click "Login"

Expected Results:
  - HTTP 200 response from /api/auth/login
  - JWT token returned and stored in localStorage
  - User role and data stored in localStorage
  - User redirected to role-specific dashboard:
    * PET_OWNER → /petowner/dashboard
    * PET_SITTER → /petsitter/dashboard
    * ADMIN → /admin/dashboard
```

#### TC-AUTH-006: Get Current User Profile
```
Preconditions:
  - User is logged in
  - Valid JWT token in localStorage

Steps:
  1. Frontend calls GET /api/user/me with Authorization header
  2. Include Bearer token in header

Expected Results:
  - HTTP 200 response
  - Response body contains user data:
    * userId, firstName, lastName, email, role, isVerified
  - No 500 Internal Server Error
```

#### TC-AUTH-008: Password Validation
```
Test Cases:
  - TC-AUTH-008a: Password < 8 characters → REJECT
  - TC-AUTH-008b: No uppercase letter → REJECT
  - TC-AUTH-008c: No number → REJECT
  - TC-AUTH-008d: No special character → REJECT
  - TC-AUTH-008e: Valid password (8+, uppercase, number, special) → ACCEPT
```

### 3.2 Pet Management Test Cases

#### TC-PET-001: Add Pet
```
Preconditions:
  - User logged in as Pet Owner
  - On /petowner/pets page

Steps:
  1. Click "Add Pet" button
  2. Enter: pet name, breed, age, weight, special needs
  3. Select vaccination status
  4. Upload pet photo
  5. Click "Save Pet"

Expected Results:
  - POST /api/pets succeeds (HTTP 201)
  - Pet stored in database with owner_id
  - Pet appears in pet list
  - Photo stored/linked properly
```

#### TC-PET-003: Edit Pet Details
```
Preconditions:
  - Pet exists in database
  - User is pet owner

Steps:
  1. Click pet from list
  2. Edit name field
  3. Click "Save Changes"

Expected Results:
  - PUT /api/pets/{id} succeeds (HTTP 200)
  - Database updated
  - Pet list refreshed with changes
```

### 3.3 Sitter Management Test Cases

#### TC-SITTER-001: Create Sitter Profile
```
Preconditions:
  - User logged in as Pet Sitter
  - User on /petsitter/profile page

Steps:
  1. Enter bio (experience description)
  2. Set hourly rate (e.g., $15/hour)
  3. Select services offered (walking, feeding, etc.)
  4. Set availability schedule
  5. Upload verification documents (student ID, references)
  6. Click "Submit Profile"

Expected Results:
  - SitterProfile created in database
  - Services serialized as JSON in servicesJson field
  - Availability serialized in availabilityJson field
  - Profile status = pending verification
  - User appears in admin pending sitters list
```

#### TC-SITTER-006: Search Sitters by Location
```
Preconditions:
  - At least 2 verified sitters exist
  - One sitter has location = "Downtown"
  - One sitter has location = "Campus"

Steps:
  1. Go to /petowner/find-sitter
  2. Enter location filter "Downtown"
  3. Click "Search"

Expected Results:
  - GET /api/sitters/search?location=Downtown succeeds
  - Only sitters in Downtown location returned
  - Unverified sitters filtered out
  - Rating info displayed for each sitter
```

### 3.4 Booking Test Cases

#### TC-BOOKING-001: Create Booking
```
Preconditions:
  - User logged in as Pet Owner
  - At least one pet exists
  - Sitter is verified (isVerified = true)
  - Sitter has available time slot

Steps:
  1. Navigate to sitter detail page
  2. Select service type (e.g., "Dog Walk")
  3. Select date and time
  4. Select pet(s)
  5. Confirm booking

Expected Results:
  - POST /api/bookings succeeds (HTTP 201)
  - Booking created with status = PENDING
  - Time slot marked as unavailable
  - Sitter receives notification
  - Owner receives confirmation email (simulated)
```

#### TC-BOOKING-006: Prevent Double Booking
```
Preconditions:
  - Sitter has booked time slot
  - Another owner tries to book same slot

Steps:
  1. Try to create booking for same time

Expected Results:
  - POST fails with HTTP 409 Conflict
  - Error message: "Time slot unavailable"
  - Booking not created
```

### 3.5 Review Test Cases

#### TC-REVIEW-001: Submit Review
```
Preconditions:
  - Booking is marked complete
  - User is pet owner

Steps:
  1. Navigate to completed booking
  2. Click "Leave Review"
  3. Select 5-star rating
  4. Enter review text (50+ characters)
  5. Click "Submit"

Expected Results:
  - POST /api/reviews succeeds (HTTP 201)
  - Review stored in database
  - Rating linked to booking
  - Sitter average rating updated
```

#### TC-REVIEW-003: Calculate Average Rating
```
Preconditions:
  - Sitter has 3 reviews: 5 stars, 4 stars, 3 stars

Steps:
  1. Fetch sitter profile with GET /api/sitters/{id}
  2. Check average rating field

Expected Results:
  - Average = (5 + 4 + 3) / 3 = 4.0 stars
  - Displayed as "4.0 (3 reviews)"
```

### 3.6 Admin Test Cases

#### TC-ADMIN-001: View Pending Sitter Applications
```
Preconditions:
  - User is admin
  - At least one sitter has isVerified = false

Steps:
  1. Login as admin
  2. Navigate to /admin/pending-sitters

Expected Results:
  - GET /api/admin/sitters/pending succeeds
  - All unverified sitters displayed
  - Each shows: name, email, submitted date, verification docs
```

#### TC-ADMIN-002: Approve Sitter Application
```
Preconditions:
  - Admin on pending sitters page
  - Sitter application visible

Steps:
  1. Click sitter name
  2. Review documents
  3. Click "Approve"

Expected Results:
  - PUT /api/admin/sitters/{id}/approve succeeds
  - isVerified set to true
  - Sitter removed from pending list
  - Sitter now searchable by pet owners
```

---

## 4. Test Execution Strategy

### 4.1 Test Levels
1. **Unit Tests** - Test individual service methods
2. **Integration Tests** - Test API endpoints with database
3. **System Tests** - Test complete user workflows
4. **Regression Tests** - Validate post-refactoring functionality

### 4.2 Test Environment
- **Backend:** localhost:8080 (Spring Boot)
- **Frontend:** localhost:3000 (Next.js)
- **Mobile:** Android emulator
- **Database:** PostgreSQL (local)

### 4.3 Test Data Setup
```sql
-- Test Users
INSERT INTO users (user_id, email, password_hash, first_name, last_name, role, is_verified)
VALUES 
  ('owner-1', 'owner@test.com', 'hashed_pass', 'John', 'Owner', 'PET_OWNER', NULL),
  ('sitter-1', 'sitter@test.com', 'hashed_pass', 'Jane', 'Sitter', 'PET_SITTER', false),
  ('admin-1', 'admin@test.com', 'hashed_pass', 'Admin', 'User', 'ADMIN', true);

-- Test Pets
INSERT INTO pets (pet_id, owner_id, name, breed, age, special_needs)
VALUES ('pet-1', 'owner-1', 'Buddy', 'Golden Retriever', 5, 'None');

-- Test Sitter Profile
INSERT INTO sitter_profiles (profile_id, user_id, bio, experience, hourly_rate, services_json)
VALUES ('profile-1', 'sitter-1', 'Experienced sitter', 2, 15.00, '["WALKING", "FEEDING"]');
```

### 4.4 Test Automation Tools
- **Backend:** JUnit 5, Mockito, Spring Boot Test
- **Frontend:** Jest, React Testing Library
- **API Testing:** Postman, REST Assured
- **End-to-End:** Selenium, Cypress

---

## 5. Test Deliverables

### 5.1 Automated Test Cases
- Backend JUnit tests in `backend/src/test/`
- Frontend Jest tests in `web/src/__tests__/`
- API integration tests

### 5.2 Test Report
- Test execution summary
- Pass/Fail breakdown
- Regression issues found
- Performance metrics

### 5.3 Regression Test Report
- Complete coverage matrix
- Issue log with severity
- Screenshots/logs of failures
- Recommendations

---

## 6. Acceptance Criteria for Test Plan

✅ All 50+ test cases documented  
✅ Covers 90%+ of functional requirements  
✅ Automated tests for core features  
✅ Clear pass/fail criteria defined  
✅ Test data prepared and available  

---

**Status:** Ready for Part 4 - Regression Testing Execution  
**Next Step:** Execute all test cases and document results in regression test report
