package edu.cit.canonigo.petfriend.features.bookings;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.canonigo.petfriend.model.Booking;
import edu.cit.canonigo.petfriend.model.BookingStatus;
import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import edu.cit.canonigo.petfriend.features.bookings.BookingService.BookingException;
import jakarta.validation.Valid;

/**
 * REST Controller for booking operations.
 * Handles endpoints for creating, retrieving, and updating bookings.
 */
@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    public BookingController(BookingService bookingService, UserRepository userRepository) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
    }

    /**
     * GET /api/bookings - List bookings for authenticated owner.
     */
    @GetMapping
    public ResponseEntity<?> listMyBookings(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "upcoming", required = false, defaultValue = "false") boolean upcoming
    ) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        if (user.getRole() != UserRole.PET_OWNER) {
            return ResponseEntity.status(403).body("Only pet owners can view their bookings");
        }

        List<Booking> bookings = bookingService.getOwnerBookings(user.getUserId(), upcoming);
        List<BookingDtos.BookingResponse> responses = bookings.stream()
                .map(BookingDtos.BookingResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    /**
     * POST /api/bookings - Create a new booking request.
     */
    @PostMapping
    public ResponseEntity<?> createBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BookingDtos.CreateBookingRequest request
    ) {
        User owner = getAuthenticatedUser(userDetails);
        if (owner == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            Booking booking = bookingService.createBooking(owner.getUserId(), request);
            return ResponseEntity.ok(BookingDtos.BookingResponse.from(booking));
        } catch (BookingException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * GET /api/bookings/sitter - List all bookings for authenticated sitter.
     */
    @GetMapping("/sitter")
    public ResponseEntity<?> listSitterBookings(@AuthenticationPrincipal UserDetails userDetails) {
        User sitter = getAuthenticatedUser(userDetails);
        if (sitter == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        if (sitter.getRole() != UserRole.PET_SITTER) {
            return ResponseEntity.status(403).body("Only sitters can view their bookings");
        }

        List<Booking> bookings = bookingService.getSitterBookings(sitter.getUserId());
        List<BookingDtos.BookingResponse> responses = bookings.stream()
                .map(BookingDtos.BookingResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/bookings/sitter/pending - List pending booking requests for sitter.
     */
    @GetMapping("/sitter/pending")
    public ResponseEntity<?> listSitterPendingRequests(@AuthenticationPrincipal UserDetails userDetails) {
        User sitter = getAuthenticatedUser(userDetails);
        if (sitter == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        if (sitter.getRole() != UserRole.PET_SITTER) {
            return ResponseEntity.status(403).body("Only sitters can view their requests");
        }

        List<Booking> bookings = bookingService.getSitterPendingRequests(sitter.getUserId());
        List<BookingDtos.BookingResponse> responses = bookings.stream()
                .map(BookingDtos.BookingResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/bookings/sitter/upcoming - List upcoming confirmed bookings for sitter.
     */
    @GetMapping("/sitter/upcoming")
    public ResponseEntity<?> listSitterUpcomingSessions(@AuthenticationPrincipal UserDetails userDetails) {
        User sitter = getAuthenticatedUser(userDetails);
        if (sitter == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        if (sitter.getRole() != UserRole.PET_SITTER) {
            return ResponseEntity.status(403).body("Only sitters can view their sessions");
        }

        List<Booking> bookings = bookingService.getSitterUpcomingSessions(sitter.getUserId());
        List<BookingDtos.BookingResponse> responses = bookings.stream()
                .map(BookingDtos.BookingResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/bookings/sitter/today - List today's confirmed bookings for sitter.
     */
    @GetMapping("/sitter/today")
    public ResponseEntity<?> listSitterTodaySchedule(@AuthenticationPrincipal UserDetails userDetails) {
        User sitter = getAuthenticatedUser(userDetails);
        if (sitter == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        if (sitter.getRole() != UserRole.PET_SITTER) {
            return ResponseEntity.status(403).body("Only sitters can view their schedule");
        }

        List<Booking> bookings = bookingService.getSitterTodaySchedule(sitter.getUserId());
        List<BookingDtos.BookingResponse> responses = bookings.stream()
                .map(BookingDtos.BookingResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    /**
     * PUT /api/bookings/{bookingId}/owner-status - Owner updates booking status.
     */
    @PutMapping("/{bookingId}/owner-status")
    public ResponseEntity<?> updateOwnerBookingStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID bookingId,
            @Valid @RequestBody BookingDtos.UpdateOwnerBookingStatusRequest request
    ) {
        User owner = getAuthenticatedUser(userDetails);
        if (owner == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        if (owner.getRole() != UserRole.PET_OWNER) {
            return ResponseEntity.status(403).body("Only owners can update their bookings");
        }

        try {
            Booking booking = bookingService.updateOwnerBookingStatus(owner.getUserId(), bookingId, request.getStatus());
            return ResponseEntity.ok(BookingDtos.BookingResponse.from(booking));
        } catch (BookingException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * PUT /api/bookings/{bookingId}/sitter-status - Sitter updates booking status.
     */
    @PutMapping("/{bookingId}/sitter-status")
    public ResponseEntity<?> updateSitterBookingStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID bookingId,
            @Valid @RequestBody BookingDtos.UpdateSitterBookingStatusRequest request
    ) {
        User sitter = getAuthenticatedUser(userDetails);
        if (sitter == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        if (sitter.getRole() != UserRole.PET_SITTER) {
            return ResponseEntity.status(403).body("Only sitters can update their bookings");
        }

        try {
            Booking booking = bookingService.updateSitterBookingStatus(sitter.getUserId(), bookingId, request.getStatus());
            return ResponseEntity.ok(BookingDtos.BookingResponse.from(booking));
        } catch (BookingException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private User getAuthenticatedUser(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }
}
