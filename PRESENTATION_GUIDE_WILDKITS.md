Presentation Script & Demo Checklist — PetFriend / Johndaniel Canonigo

Overview
- Project: PetFriend
- Presenter: Johndaniel Canonigo
- Goal: 5–7 minute demo showing core flows: Auth (email + Google), Profile + Upload, Create Booking + Payment, Sitter review flow.

Key code references (file → exact lines)
- Auth (backend): [backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java](backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java#L1-L220)
  - `register` handler: [AuthController.java](backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java#L55-L111)
  - `login` handler: [AuthController.java](backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java#L112-L149)
  - `google` handler (Supabase token exchange): [AuthController.java](backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java#L150-L217)

- File uploads (profile & pet images): [backend/src/main/java/edu/cit/canonigo/petfriend/shared/upload/UploadController.java](backend/src/main/java/edu/cit/canonigo/petfriend/shared/upload/UploadController.java#L1-L120)
  - Profile upload: [UploadController.java](backend/src/main/java/edu/cit/canonigo/petfriend/shared/upload/UploadController.java#L30-L49)
  - Pet photo upload: [UploadController.java](backend/src/main/java/edu/cit/canonigo/petfriend/shared/upload/UploadController.java#L51-L88)

- Payments (PayMongo):
  - Controller endpoints: [backend/src/main/java/edu/cit/canonigo/petfriend/features/payments/PaymentController.java](backend/src/main/java/edu/cit/canonigo/petfriend/features/payments/PaymentController.java#L1-L130)
    - Checkout endpoint: [PaymentController.java](backend/src/main/java/edu/cit/canonigo/petfriend/features/payments/PaymentController.java#L40-L72)
    - Webhook handler: [PaymentController.java](backend/src/main/java/edu/cit/canonigo/petfriend/features/payments/PaymentController.java#L73-L120)
  - Service that creates checkout: [backend/src/main/java/edu/cit/canonigo/petfriend/features/payments/PaymentService.java](backend/src/main/java/edu/cit/canonigo/petfriend/features/payments/PaymentService.java#L1-L160)
    - `createCheckoutSession(...)`: [PaymentService.java](backend/src/main/java/edu/cit/canonigo/petfriend/features/payments/PaymentService.java#L44-L110)

- Booking model & update:
  - Booking entity (payment fields): [backend/src/main/java/edu/cit/canonigo/petfriend/model/Booking.java](backend/src/main/java/edu/cit/canonigo/petfriend/model/Booking.java#L1-L220)
  - BookingService.updatePayment: [backend/src/main/java/edu/cit/canonigo/petfriend/features/bookings/BookingService.java](backend/src/main/java/edu/cit/canonigo/petfriend/features/bookings/BookingService.java#L236-L260)

- Web (frontend) integration points:
  - Login page (email + Google): [web/src/app/login/page.js](web/src/app/login/page.js#L1-L200)
    - Google OAuth redirect: [login/page.js](web/src/app/login/page.js#L10-L19)
  - Auth callback (exchanges Supabase token with backend): [web/src/app/auth/callback/page.js](web/src/app/auth/callback/page.js#L1-L120)
    - Backend exchange call: [auth/callback/page.js](web/src/app/auth/callback/page.js#L28-L38)
  - Payment initiation (create booking then checkout): [web/src/app/petowner/find-sitter/[sitterId]/page.js](web/src/app/petowner/find-sitter/[sitterId]/page.js#L480-L510)

- Mobile (Android):
  - Supabase token exchange: [mobile/app/src/main/java/com/example/mobile/network/SupabaseAuthService.kt](mobile/app/src/main/java/com/example/mobile/network/SupabaseAuthService.kt#L1-L80)
  - API client for checkout: [mobile/app/src/main/java/com/example/mobile/network/ApiService.kt](mobile/app/src/main/java/com/example/mobile/network/ApiService.kt#L1-L160)

Demo script (approx. 6 minutes)
1) 0:00–0:30 — Intro (30s)
   - Quick project elevator pitch (purpose + tech stack)
2) 0:30–1:30 — Auth (1:00)
   - Show login page, quickly demo email login (enter creds) — mention `POST /api/auth/login` ([login/page.js](web/src/app/login/page.js#L24-L36))
   - Show Google sign-in flow: click Google → callback exchanges token with backend (`/api/auth/google`) — point to [auth/callback/page.js](web/src/app/auth/callback/page.js#L28-L38) and [AuthController.java google handler](backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java#L150-L217)
3) 1:30–2:30 — Profile + Upload (1:00)
   - Edit profile photo, upload — backend stores image and sets `profilePhotoUrl` ([UploadController.java](backend/src/main/java/edu/cit/canonigo/petfriend/shared/upload/UploadController.java#L30-L49))
4) 2:30–4:00 — Booking + Payment (1:30)
   - Create booking (show booking form), then click pay — frontend calls `POST /api/payments/paymongo/checkout` ([find-sitter page](web/src/app/petowner/find-sitter/[sitterId]/page.js#L488-L500))
   - Explain backend `PaymentService.createCheckoutSession(...)` builds PayMongo payload and returns `checkoutUrl` ([PaymentService.java](backend/src/main/java/edu/cit/canonigo/petfriend/features/payments/PaymentService.java#L44-L110))
   - Explain webhook updates `booking.paymentStatus` via `BookingService.updatePayment(...)` ([PaymentController.java webhook](backend/src/main/java/edu/cit/canonigo/petfriend/features/payments/PaymentController.java#L73-L94) and [BookingService.updatePayment](backend/src/main/java/edu/cit/canonigo/petfriend/features/bookings/BookingService.java#L236-L260))
5) 4:00–5:00 — Sitter flow & reviews (1:00)
   - Show sitter dashboard, accept booking → confirm status transition in code (`BookingService` transition rules) and where reviews are stored.
6) 5:00–5:30 — Security note (30s)
   - Point out committed keys: `supabase.service-role-key` in backend properties and `NEXT_PUBLIC_SUPABASE_ANON_KEY` in frontend env — recommend rotating and using env variables.
7) 5:30–6:00 — Wrap-up (30s)
   - Callouts: where to find source code for each flow (list of paths above). Offer to run tests or generate screenshots.

Demo checklist (before recording)
- [ ] Rotate any exposed keys or remove sensitive values from commits
- [ ] Start backend on `localhost:8080` (ensure JDK installed for tests)
- [ ] Start web frontend on `localhost:3000`
- [ ] Optionally launch mobile emulator for Android demo
- [ ] Create a test user + test booking ready to pay
- [ ] Have PayMongo sandbox keys configured or simulate webhook payload

Appendix — Important file links
- AuthController: [backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java](backend/src/main/java/edu/cit/canonigo/petfriend/features/auth/AuthController.java#L1-L220)
- UploadController: [backend/src/main/java/edu/cit/canonigo/petfriend/shared/upload/UploadController.java](backend/src/main/java/edu/cit/canonigo/petfriend/shared/upload/UploadController.java#L1-L120)
- PaymentController: [backend/src/main/java/edu/cit/canonigo/petfriend/features/payments/PaymentController.java](backend/src/main/java/edu/cit/canonigo/petfriend/features/payments/PaymentController.java#L1-L130)
- PaymentService: [backend/src/main/java/edu/cit/canonigo/petfriend/features/payments/PaymentService.java](backend/src/main/java/edu/cit/canonigo/petfriend/features/payments/PaymentService.java#L1-L160)
- BookingService.updatePayment: [backend/src/main/java/edu/cit/canonigo/petfriend/features/bookings/BookingService.java](backend/src/main/java/edu/cit/canonigo/petfriend/features/bookings/BookingService.java#L236-L260)
- Booking model: [backend/src/main/java/edu/cit/canonigo/petfriend/model/Booking.java](backend/src/main/java/edu/cit/canonigo/petfriend/model/Booking.java#L1-L220)
- Web login: [web/src/app/login/page.js](web/src/app/login/page.js#L1-L200)
- Web auth callback: [web/src/app/auth/callback/page.js](web/src/app/auth/callback/page.js#L1-L120)
- Web find-sitter payment call: [web/src/app/petowner/find-sitter/[sitterId]/page.js](web/src/app/petowner/find-sitter/[sitterId]/page.js#L480-L510)
- Mobile Supabase exchange: [mobile/app/src/main/java/com/example/mobile/network/SupabaseAuthService.kt](mobile/app/src/main/java/com/example/mobile/network/SupabaseAuthService.kt#L1-L80)
