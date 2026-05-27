# PetFriend

PetFriend is a platform designed to connect pet owners with pet sitters.

The main problem it addresses is the difficulty pet owners face when looking for reliable pet care services. Instead of manually searching or messaging people outside the system, PetFriend provides a centralized platform where pet owners can register, manage their pets, find available sitters, create bookings, pay online, and leave reviews.

The overall goal of the project is to provide a functional pet care booking system while demonstrating proper system integration between multiple platforms and services.

## Project Components

- **Backend:** Spring Boot REST API for authentication, users, pets, sitters, bookings, payments, reviews, messages, uploads, and admin workflows.
- **Web:** Next.js frontend for pet owners, pet sitters, and admins.
- **Mobile:** Android Kotlin application that connects to the same backend API.
- **Database:** PostgreSQL, currently configured for Supabase.
- **External services:** Supabase/Google OAuth, PayMongo checkout, SMTP email, and local file upload storage.

## Main Features

- Email/password authentication with JWT tokens
- Google OAuth through Supabase
- Role-based access for `PET_OWNER`, `PET_SITTER`, and `ADMIN`
- Pet owner dashboard, pet management, sitter search, bookings, messages, and reviews
- Pet sitter dashboard, sitter profile, availability, booking requests, and messages
- Admin dashboard, user management, booking overview, and sitter verification
- Profile photo and pet photo uploads
- Booking price calculation and booking status transitions
- PayMongo sandbox checkout integration
- Payment webhook endpoint for payment status updates
- SMTP welcome email support
- Web and Android clients sharing one backend API

## Tech Stack

### Backend

- Java 17
- Spring Boot 3.5
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL / Supabase
- H2 for tests
- Maven wrapper

### Web

- Next.js 16
- React 19
- Supabase JS client
- Jest
- ESLint

### Mobile

- Android Kotlin
- Retrofit
- OkHttp
- Gson
- Kotlin Coroutines
- Google Sign-In

## Repository Structure

```text
.
├── backend/
│   ├── src/main/java/edu/cit/canonigo/petfriend/
│   │   ├── config/                 # Spring Security, CORS, web resource config
│   │   ├── features/
│   │   │   ├── admin/              # Admin dashboard and user/sitter moderation
│   │   │   ├── auth/               # Register, login, Google auth
│   │   │   ├── bookings/           # Booking creation, status updates, pricing
│   │   │   ├── messages/           # Message threads and messages
│   │   │   ├── payments/           # PayMongo checkout and webhook
│   │   │   ├── pets/               # Pet CRUD
│   │   │   ├── reviews/            # Review submission and summary
│   │   │   └── sitters/            # Sitter search/profile/verification
│   │   ├── model/                  # JPA entities and enums
│   │   ├── repository/             # Spring Data repositories
│   │   ├── security/               # JWT filter/provider/services
│   │   └── shared/                 # Uploads, email, exceptions, constants, utilities
│   ├── src/main/resources/
│   │   └── application.properties  # Local backend configuration
│   ├── src/test/                   # Backend tests
│   ├── DEPLOYMENT.md               # Backend deployment notes
│   └── pom.xml
├── web/
│   ├── src/app/                    # Next.js app router pages
│   ├── src/features/               # Frontend feature APIs/hooks
│   ├── src/shared/                 # Shared frontend utilities/components/constants
│   ├── public/                     # Static assets
│   └── package.json
├── mobile/
│   ├── app/src/main/java/com/example/mobile/
│   │   ├── features/               # Android feature screens
│   │   ├── model/                  # Mobile models
│   │   ├── network/                # Retrofit API and Supabase token exchange
│   │   └── util/                   # Local preferences/session helpers
│   ├── app/src/main/res/           # Android layouts, drawables, strings
│   └── build.gradle.kts
├── doc/                            # Documentation exports
├── uploads/                        # Local uploaded files during development
│
└── README.md

```

## Important Backend Endpoints

### Authentication

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/google`
- `GET /api/user/me`

### Pets And Uploads

- `GET /api/pets`
- `POST /api/pets`
- `PUT /api/pets/{petId}`
- `DELETE /api/pets/{petId}`
- `POST /api/uploads/profile-photo`
- `POST /api/uploads/pets/{petId}/photo`

### Sitters And Bookings

- `GET /api/sitters/search`
- `GET /api/sitters/{sitterId}`
- `POST /api/bookings`
- `GET /api/bookings`
- `PUT /api/bookings/{bookingId}/owner-status`
- `PUT /api/bookings/{bookingId}/sitter-status`

### Payments And Reviews

- `POST /api/payments/paymongo/checkout`
- `POST /api/payments/paymongo/webhook`
- `POST /api/reviews`
- `GET /api/reviews/sitter/{sitterId}`
- `GET /api/reviews/sitter/{sitterId}/summary`

## Prerequisites

- Java 17 or newer
- Node.js 20 or newer
- npm
- Android Studio, if running the mobile app
- PostgreSQL database or Supabase database
- Supabase project for Google OAuth
- PayMongo sandbox account for checkout testing
- SMTP account if testing welcome emails

## Configuration

The backend reads local configuration from:

```text
backend/src/main/resources/application.properties
```

Important settings include:

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `jwt.secret`
- `supabase.url`
- `supabase.service-role-key`
- `spring.mail.username`
- `spring.mail.password`
- `paymongo.secretKey`
- `paymongo.successUrl`
- `paymongo.cancelUrl`


The web app uses:

```text
web/.env.local
```

Example:

```env
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
NEXT_PUBLIC_SUPABASE_URL=https://your-project.supabase.co
NEXT_PUBLIC_SUPABASE_ANON_KEY=your-supabase-anon-key
```

The Android emulator connects to the host machine backend through:

```text
http://10.0.2.2:8080/
```

This is configured in:

```text
mobile/app/src/main/java/com/example/mobile/network/RetrofitClient.kt
```

For a physical Android device, replace `10.0.2.2` with your computer's local network IP address.

## How To Run Locally

### 1. Run The Backend

From the repository root:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Backend URL:

```text
http://localhost:8080
```

To run backend tests:

```powershell
cd backend
.\mvnw.cmd test
```

### 2. Run The Web Frontend

Open a second terminal:

```powershell
cd web
npm install
npm run dev
```

Web URL:

```text
http://localhost:3000
```

To build the frontend:

```powershell
cd web
npm run build
```

To run frontend tests:

```powershell
cd web
npm test
```

### 3. Run The Android Mobile App

1. Open Android Studio.
2. Open the `mobile` folder as the Android project.
3. Wait for Gradle sync to finish.
4. Start the backend on `localhost:8080`.
5. Run the app on an emulator or physical device.

For emulator use, keep:

```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/"
```

For a physical device, change it to:

```kotlin
private const val BASE_URL = "http://YOUR_COMPUTER_IP:8080/"
```

## Recommended Demo Data

Before presenting or testing the full flow, prepare:

- One pet owner account
- One pet sitter account
- At least one pet under the pet owner
- A sitter profile with services, hourly rate, and availability
- PayMongo sandbox key configured
- Optional completed booking for review demonstration

## Typical User Flow

1. Register or log in as a pet owner.
2. Add a pet and optionally upload a pet photo.
3. Search for a sitter.
4. Open the sitter profile and create a booking.
5. Continue to PayMongo checkout.
6. Return to PetFriend after payment.
7. Log in as sitter and accept or manage booking requests.
8. Complete the booking.
9. Pet owner leaves a review.

## Architecture Summary

PetFriend uses a client-server architecture with a layered backend.

```text
Web / Mobile Client
        |
        v
Spring Boot REST Controllers
        |
        v
Service Layer
        |
        v
Repositories
        |
        v
PostgreSQL / Supabase Database
```

External integrations connect through the backend or client where appropriate:

- Google OAuth and Supabase handle identity verification.
- The backend issues the app's JWT token.
- PayMongo handles checkout sessions.
- SMTP sends email notifications.
- Upload endpoints store profile and pet images.

## Key Source Files

- `backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java`
- `backend/src/main/java/edu/cit/canonigo/petfriend/features/bookings/BookingController.java`
- `backend/src/main/java/edu/cit/canonigo/petfriend/features/bookings/BookingService.java`
- `backend/src/main/java/edu/cit/canonigo/petfriend/features/payments/PaymentController.java`
- `backend/src/main/java/edu/cit/canonigo/petfriend/features/payments/PaymentService.java`
- `backend/src/main/java/edu/cit/canonigo/petfriend/features/reviews/ReviewController.java`
- `backend/src/main/java/edu/cit/canonigo/petfriend/features/reviews/ReviewService.java`
- `backend/src/main/java/edu/cit/canonigo/petfriend/shared/upload/UploadController.java`
- `web/src/app/login/page.js`
- `web/src/app/auth/callback/page.js`
- `web/src/app/petowner/find-sitter/[sitterId]/page.js`
- `mobile/app/src/main/java/com/example/mobile/network/ApiService.kt`
- `mobile/app/src/main/java/com/example/mobile/network/SupabaseAuthService.kt`

## Troubleshooting

### Backend cannot connect to database

- Check `spring.datasource.url`, username, and password.
- Make sure the database accepts remote connections.
- If using Supabase, confirm SSL mode and pooler URL.

### Web cannot call backend

- Make sure the backend is running on `localhost:8080`.
- Check `NEXT_PUBLIC_API_BASE_URL`.
- Confirm backend CORS allows `http://localhost:3000`.

### Google sign-in fails

- Confirm Supabase URL and anon/service-role keys are correct.
- Add the correct redirect URI in Supabase Auth settings:

```text
http://localhost:3000/auth/callback
```

- For Android, make sure the Google web client ID in `strings.xml` is correct.

### Mobile cannot connect to backend

- Use `10.0.2.2` for Android emulator.
- Use your computer IP address for a physical device.
- Make sure the phone and computer are on the same network.

### PayMongo does not open

- Make sure the pet owner has at least one pet selected before booking.
- Confirm `paymongo.secretKey` is configured.
- Check that the backend can access `https://api.paymongo.com`.

## Security Notes

This project contains local development configuration. Before deploying or making the repository public:

- Rotate exposed keys and passwords.
- Move secrets to environment variables.
- Use separate sandbox and production credentials.
- Restrict CORS to trusted origins.
- Disable verbose SQL and debug logging in production.

## Additional Documentation

- Architecture notes: `ARCHITECTURE_DIAGRAMS.md`
- Backend deployment notes: `backend/DEPLOYMENT.md`
- Web notes: `web/README.md`
- Mobile notes: `mobile/README.md`
