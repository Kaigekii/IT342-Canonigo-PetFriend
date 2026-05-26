package edu.cit.canonigo.petfriend.features.payments;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cit.canonigo.petfriend.features.bookings.BookingDtos;
import edu.cit.canonigo.petfriend.features.bookings.BookingService;
import edu.cit.canonigo.petfriend.features.bookings.BookingService.BookingException;
import edu.cit.canonigo.petfriend.model.Booking;
import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.UserRepository;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public PaymentController(PaymentService paymentService,
                             BookingService bookingService,
                             UserRepository userRepository,
                             ObjectMapper objectMapper) {
        this.paymentService = paymentService;
        this.bookingService = bookingService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/paymongo/checkout")
    public ResponseEntity<?> createCheckout(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PaymentDtos.CheckoutRequest request
    ) {
        User owner = getAuthenticatedUser(userDetails);
        if (owner == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        if (owner.getRole() != UserRole.PET_OWNER) {
            return ResponseEntity.status(403).body("Only pet owners can pay for bookings");
        }

        if (request == null || request.getBookingId() == null) {
            return ResponseEntity.badRequest().body("Booking ID is required");
        }

        try {
            Booking booking = bookingService.getOwnerBooking(owner.getUserId(), request.getBookingId());
            PaymentService.CheckoutResult result = paymentService.createCheckoutSession(booking);
            bookingService.updatePayment(booking.getBookingId(), "PAYMONGO", result.getPaymentId(), "PENDING");
            return ResponseEntity.ok(new PaymentDtos.CheckoutResponse(result.getCheckoutUrl(), result.getPaymentId()));
        } catch (BookingException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (PaymentService.PaymentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/paymongo/webhook")
    public ResponseEntity<?> handleWebhook(@RequestBody String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            String eventType = firstText(
                    root.at("/data/attributes/type").asText(),
                    root.path("data").path("type").asText()
            );

            String bookingId = firstText(
                    root.at("/data/attributes/metadata/bookingId").asText(),
                    root.at("/data/attributes/data/attributes/metadata/bookingId").asText(),
                    root.at("/data/attributes/data/attributes/metadata/booking_id").asText()
            );

            if (bookingId == null || bookingId.isBlank()) {
                return ResponseEntity.ok("ignored");
            }

            String paymentStatus = mapPaymentStatus(eventType);
            if (paymentStatus == null) {
                return ResponseEntity.ok("ignored");
            }

            bookingService.updatePayment(UUID.fromString(bookingId), null, null, paymentStatus);
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid payload");
        }
    }

    private String mapPaymentStatus(String eventType) {
        if (eventType == null) return null;
        String normalized = eventType.toLowerCase();
        if (normalized.contains("paid") || normalized.contains("succeeded") || normalized.contains("success")) {
            return "PAID";
        }
        if (normalized.contains("failed") || normalized.contains("cancel")) {
            return "FAILED";
        }
        return null;
    }

    private String firstText(String... values) {
        if (values == null) return null;
        for (String value : values) {
            if (value != null && !value.isBlank()) return value;
        }
        return null;
    }

    private User getAuthenticatedUser(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }
}
