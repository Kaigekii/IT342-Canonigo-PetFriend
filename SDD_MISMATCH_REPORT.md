SDD vs Implementation — Mismatch Report

Summary: concise list of SDD claims that differ from the current codebase. Each item includes the SDD statement, actual implementation, and exact file:line references.

1) API prefix / versioning
- SDD: Base URL uses `/api/v1` and versioned API paths.
- Implementation: Controllers use `/api` (no `/v1`).
  - Example: `AuthController` mapping: [backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java](backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java#L1-L10) and class-level `@RequestMapping("/api/auth")` (see [AuthController.java](backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java#L18-L24)).

2) Upload endpoints
- SDD: `POST /api/uploads/users/{userId}/photo` (userId in path).
- Implementation: profile photo endpoint is `POST /api/uploads/profile-photo` (authenticated user inferred) and `POST /api/uploads/pets/{petId}/photo` for pet photos.
  - Profile upload: [backend/src/main/java/edu/cit/canonigo/petfriend/shared/upload/UploadController.java#L40-L47](backend/src/main/java/edu/cit/canonigo/petfriend/shared/upload/UploadController.java#L40-L47)
  - Pet photo upload: [backend/src/main/java/edu/cit/canonigo/petfriend/shared/upload/UploadController.java#L61-L70](backend/src/main/java/edu/cit/canonigo/petfriend/shared/upload/UploadController.java#L61-L70)

3) Auth endpoints (logout / refresh)
- SDD: lists `POST /api/auth/logout` and `POST /api/auth/refresh`.
- Implementation: Not implemented. `AuthController` exposes `POST /api/auth/register`, `POST /api/auth/login`, and `POST /api/auth/google` only.
  - `register`: [backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java#L73-L80](backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java#L73-L80)
  - `login`: [backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java#L124-L131](backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java#L124-L131)
  - `google`: [backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java#L170-L177](backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java#L170-L177)

4) Refresh tokens & logout handling (persistence)
- SDD: includes `refresh_tokens` table and refresh-token lifecycle (revoke on logout).
- Implementation: No `RefreshToken` entity or related controller found in codebase — refresh/token endpoints absent.

5) Secrets handling (committed secrets)
- SDD: claims secrets are stored in environment variables for production (`PAYMONGO_SECRET_KEY`, `SUPABASE_SERVICE_ROLE_KEY`).
- Implementation: Secrets are present in `application.properties` (committed). Examples:
  - Supabase service role key: [backend/src/main/resources/application.properties#L38](backend/src/main/resources/application.properties#L38)
  - DB password: [backend/src/main/resources/application.properties#L6](backend/src/main/resources/application.properties#L6)
  - JWT secret: [backend/src/main/resources/application.properties#L11-L12](backend/src/main/resources/application.properties#L11-L12)

6) Payment handling (sandbox vs live)
- SDD: Payment processing described as sandbox/test mode (simulated payments only).
- Implementation: Code calls PayMongo API (`https://api.paymongo.com/v1/checkout_sessions`) and requires a `paymongo.secretKey` — this is an actual provider integration (not pure local simulation). See:
  - PayMongo checkout URL constant: [backend/src/main/java/edu/cit/canonigo/petfriend/features/payments/PaymentService.java#L29](backend/src/main/java/edu/cit/canonigo/petfriend/features/payments/PaymentService.java#L29)
  - Checkout creation method: [backend/src/main/java/edu/cit/canonigo/petfriend/features/payments/PaymentService.java#L48-L56](backend/src/main/java/edu/cit/canonigo/petfriend/features/payments/PaymentService.java#L48-L56)
  - Controller uses the service: [backend/src/main/java/edu/cit/canonigo/petfriend/features/payments/PaymentController.java#L60-L68](backend/src/main/java/edu/cit/canonigo/petfriend/features/payments/PaymentController.java#L60-L68)

7) Payment endpoints listed but missing
- SDD: `GET /api/payments/{paymentId}` is included in SDD.
- Implementation: No GET handler for `/api/payments/{paymentId}` found in `PaymentController`.

8) JWT expiration mismatch
- SDD: access token TTL specified as 2 hours.
- Implementation: `application.properties` sets `jwt.expiration-ms=86400000` (24 hours). See: [backend/src/main/resources/application.properties#L19](backend/src/main/resources/application.properties#L19)

9) Password hashing rounds
- SDD: bcrypt with 12 salt rounds required.
- Implementation: `PasswordEncoder` bean uses default `new BCryptPasswordEncoder()` (default strength 10) in [backend/src/main/java/edu/cit/canonigo/petfriend/config/SecurityConfig.java#L68-L74](backend/src/main/java/edu/cit/canonigo/petfriend/config/SecurityConfig.java#L68-L74).

10) Rate limiting & API envelope
- SDD: rate limiting (100 req/min) and standardized envelope `{ success,data,error,timestamp }` for all responses.
- Implementation: No rate-limiting filter configured; controllers return plain responses or DTOs (no universal envelope). Example: plaintext error returns in [AuthController.java](backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java#L86-L95).

11) OAuth verification: Supabase token verification uses service role key
- Note: SDD describes Supabase used as a helper for Google OAuth. Implementation verifies Supabase `/auth/v1/user` by adding `Authorization: Bearer {token}` and `apikey: {service-role-key}` (see [AuthController.java google handler](backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java#L180-L198) and property binding [backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java#L48-L52](backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java#L48-L52)).

Recommendations (high priority first)
- Remove committed secrets from the repo immediately; rotate affected keys (DB, Supabase, SMTP, JWT). See file: [backend/src/main/resources/application.properties#L1-L40](backend/src/main/resources/application.properties#L1-L40).
- Decide whether payments are simulated or real. If simulated, replace PayMongo calls with a sandbox simulator or mock; if real, ensure `paymongo.secretKey` is provided via environment variables and document expected behavior.
- Add missing auth endpoints if required by SDD (`/auth/logout`, `/auth/refresh`) or update SDD to match current token strategy.
- Align API versioning (either adopt `/v1` or update SDD to reflect current `/api` paths).
- Implement a consistent API response envelope (or update SDD to reflect current mixed responses).
- Implement refresh token persistence if refresh workflow is required by product.
- Add rate-limiting middleware or remove the requirement from SDD.

If you want, I can: (pick one)
- Update PETFRIEND_SSD.md to match the code (I can produce a new SDD reflecting current endpoints/behaviour).
- Implement small code changes (e.g., add `GET /api/payments/{paymentId}`, move secrets to env, or add logout/refresh endpoints).
- Create a PR that strips secrets from `application.properties` and adds a `.env.example` with placeholders.

---
Generated by GitHub Copilot (GPT-5 mini)
