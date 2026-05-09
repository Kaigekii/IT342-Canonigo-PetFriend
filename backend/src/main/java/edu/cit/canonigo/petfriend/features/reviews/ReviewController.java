package edu.cit.canonigo.petfriend.features.reviews;

import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for reviews management
 */
@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ReviewController {

    private final UserRepository userRepository;
    private final ReviewService reviewService;

    public ReviewController(UserRepository userRepository, ReviewService reviewService) {
        this.userRepository = userRepository;
        this.reviewService = reviewService;
    }

    /**
     * Submit a review for a completed booking
     * 
     * @param userDetails Authenticated user (must be PET_OWNER)
     * @param request Review request with booking ID, rating, and comment
     * @return Created review
     */
    @PostMapping
    public ResponseEntity<?> submitReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ReviewDtos.CreateReviewRequest request
    ) {
        User reviewer = getAuthenticatedUser(userDetails);
        if (reviewer == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            ReviewDtos.ReviewResponse review = reviewService.submitReview(
                    reviewer, request.getBookingId(), request.getRating(), request.getComment());
            return ResponseEntity.ok(review);
        } catch (IllegalAccessError e) {
            if ("Only pet owners can submit reviews".equals(e.getMessage())) {
                return ResponseEntity.status(403).body("Only pet owners can submit reviews");
            }
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Booking not found");
        } catch (IllegalStateException e) {
            if ("A review already exists for this booking".equals(e.getMessage())) {
                return ResponseEntity.status(409).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get all reviews for a sitter
     * 
     * @param userDetails Authenticated user
     * @param sitterId ID of the sitter
     * @return List of reviews for the sitter
     */
    @GetMapping("/sitter/{sitterId}")
    public ResponseEntity<?> listSitterReviews(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID sitterId
    ) {
        User current = getAuthenticatedUser(userDetails);
        if (current == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<ReviewDtos.ReviewResponse> reviews = reviewService.listSitterReviews(sitterId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get review summary for a sitter
     * 
     * @param userDetails Authenticated user
     * @param sitterId ID of the sitter
     * @return Review summary with average rating and count
     */
    @GetMapping("/sitter/{sitterId}/summary")
    public ResponseEntity<?> getSitterReviewSummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID sitterId
    ) {
        User current = getAuthenticatedUser(userDetails);
        if (current == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        ReviewDtos.ReviewSummaryResponse summary = reviewService.getSitterReviewSummary(sitterId);
        return ResponseEntity.ok(summary);
    }

    /**
     * Get IDs of bookings already reviewed by the current user
     * 
     * @param userDetails Authenticated user
     * @return List of booking IDs that have been reviewed
     */
    @GetMapping("/me/reviewed-bookings")
    public ResponseEntity<?> getReviewedBookingIds(@AuthenticationPrincipal UserDetails userDetails) {
        User current = getAuthenticatedUser(userDetails);
        if (current == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<UUID> ids = reviewService.getReviewedBookingIds(current.getUserId());
        return ResponseEntity.ok(ids);
    }

    // ========== Helper Methods ==========

    private User getAuthenticatedUser(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }
}
