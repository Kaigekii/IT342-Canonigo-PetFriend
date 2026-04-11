Objective

Continue your project by developing the web application and implementing the main feature of your system.

This phase focuses on building the core business functionality of your approved project based on your Software Design Document (SDD).

Development Tasks
1. Main Feature Implementation
Develop the main feature of your application in the web system.

Requirements:

The feature must match your approved project proposal and SDD
It must be functional and connected to the backend and database
It must allow users to perform the main purpose of the system
Apply proper validation and error handling
Display appropriate success/error messages

2. Web Integration
Connect the web frontend to your backend API
Ensure data is saved and retrieved correctly from the database
Show working interaction between frontend, backend, and database

Development Guidelines
Use the same GitHub repository
Place web app in: web/
Follow your approved SDD
Use clear commits for development progress
Apply clean UI, readable code, and proper naming conventions

4. Short Summary (Main Feature: Booking)

Description of the main feature
The main feature of PetFriend is the booking workflow between pet owners and verified pet sitters. A pet owner can find a sitter, select one or more of their pets, choose service details, and submit a booking request. The sitter then manages the request by confirming, cancelling, or completing the booking.

Inputs and validations used
Booking input fields:
- `sitterId`
- `petIds` (one or more)
- `serviceType`
- `date`
- `startTime`
- `endTime`
- `specialInstructions` (optional)

Validations implemented:
- User must be authenticated.
- Only `PET_OWNER` can create bookings.
- `sitterId` must exist and belong to a `PET_SITTER`.
- All selected pets must exist.
- Selected pets must belong to the logged-in owner.
- `endTime` must be later than `startTime`.
- Sitter must have an hourly rate configured.
- Status transitions for sitters are restricted:
	- `PENDING -> CONFIRMED` or `PENDING -> CANCELLED`
	- `CONFIRMED -> COMPLETED`

How the feature works
1. Owner searches for verified sitters using location/service filters.
2. Owner opens sitter details, then submits a booking request.
3. Backend validates the request and computes pricing:
	 - `durationHours = (endTime - startTime) / 60`
	 - `baseAmount = hourlyRate * durationHours`
	 - `serviceFee = 10% of baseAmount`
	 - `totalAmount = baseAmount + serviceFee`
4. Booking is saved with `PENDING` status and `PHP` currency.
5. Sitter sees booking requests and updates status (confirm/cancel/complete).
6. Completed bookings can later be reviewed by owners (supports rating summary for sitters).

API endpoints used
- `GET /api/sitters/search` - Search verified sitters
- `GET /api/sitters/{sitterId}` - View sitter details
- `POST /api/bookings` - Create booking request
- `GET /api/bookings?upcoming=true` - Owner upcoming bookings
- `GET /api/bookings` - Owner booking list
- `GET /api/bookings/sitter` - Sitter booking list
- `GET /api/bookings/sitter/pending` - Sitter pending requests
- `GET /api/bookings/sitter/upcoming` - Sitter upcoming sessions
- `GET /api/bookings/sitter/today` - Sitter today schedule
- `PUT /api/bookings/{bookingId}/sitter-status` - Sitter updates booking status

Database table/s involved
- `bookings` - Main booking record (owner, sitter, date/time, status, total amount, currency)
- `booking_pets` - Join table linking bookings to one or more pets
- `pets` - Owner pet records attached to booking
- `users` - Owner and sitter accounts/roles
- `sitter_profiles` - Sitter hourly rate, services, and profile data used for booking
- `reviews` - Post-booking ratings/comments that feed sitter rating summary