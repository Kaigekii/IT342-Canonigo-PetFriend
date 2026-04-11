package edu.cit.canonigo.petfriend.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.canonigo.petfriend.model.Booking;
import edu.cit.canonigo.petfriend.model.BookingStatus;
import edu.cit.canonigo.petfriend.model.Review;
import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.BookingRepository;
import edu.cit.canonigo.petfriend.repository.ReviewRepository;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public ReviewController(ReviewRepository reviewRepository,
                            BookingRepository bookingRepository,
                            UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> submitReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateReviewRequest request
    ) {
        User reviewer = getAuthenticatedUser(userDetails);
        if (reviewer == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        if (reviewer.getRole() != UserRole.PET_OWNER) {
            return ResponseEntity.status(403).body("Only pet owners can submit reviews");
        }

        Booking booking = bookingRepository.findById(request.getBookingId()).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(404).body("Booking not found");
        }

        if (!booking.getOwner().getUserId().equals(reviewer.getUserId())) {
            return ResponseEntity.status(403).body("You can only review your own completed bookings");
        }

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            return ResponseEntity.badRequest().body("Review is allowed only after booking is completed");
        }

        if (booking.getSitter() == null) {
            return ResponseEntity.badRequest().body("Booking has no sitter assigned");
        }

        if (reviewRepository.existsByBooking_BookingId(booking.getBookingId())) {
            return ResponseEntity.status(409).body("A review already exists for this booking");
        }

        Review review = new Review();
        review.setBooking(booking);
        review.setSitter(booking.getSitter());
        review.setReviewer(reviewer);
        review.setRating(request.getRating());
        review.setComment(request.getComment().trim());

        Review saved = reviewRepository.save(review);
        return ResponseEntity.ok(ReviewResponse.from(saved));
    }

    @GetMapping("/sitter/{sitterId}")
    public ResponseEntity<?> listSitterReviews(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID sitterId
    ) {
        User current = getAuthenticatedUser(userDetails);
        if (current == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<ReviewResponse> items = reviewRepository.findBySitter_UserIdOrderByCreatedAtDesc(sitterId)
                .stream()
                .map(ReviewResponse::from)
                .toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/sitter/{sitterId}/summary")
    public ResponseEntity<?> getSitterReviewSummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID sitterId
    ) {
        User current = getAuthenticatedUser(userDetails);
        if (current == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        long count = reviewRepository.countBySitter_UserId(sitterId);
        Double avg = reviewRepository.findAverageRatingBySitterId(sitterId);
        BigDecimal averageRating = BigDecimal.valueOf(avg == null ? 0.0 : avg)
                .setScale(1, RoundingMode.HALF_UP);

        return ResponseEntity.ok(new ReviewSummaryResponse(averageRating, count));
    }

    @GetMapping("/me/reviewed-bookings")
    public ResponseEntity<?> getReviewedBookingIds(@AuthenticationPrincipal UserDetails userDetails) {
        User current = getAuthenticatedUser(userDetails);
        if (current == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<UUID> ids = reviewRepository.findReviewedBookingIdsByReviewerId(current.getUserId());
        return ResponseEntity.ok(ids);
    }

    private User getAuthenticatedUser(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }

    public static class CreateReviewRequest {
        @NotNull
        private UUID bookingId;

        @NotNull
        @Min(1)
        @Max(5)
        private Integer rating;

        @NotBlank
        private String comment;

        public UUID getBookingId() {
            return bookingId;
        }

        public void setBookingId(UUID bookingId) {
            this.bookingId = bookingId;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    public static class ReviewResponse {
        private final UUID reviewId;
        private final UUID bookingId;
        private final UUID sitterId;
        private final UUID reviewerId;
        private final String reviewerName;
        private final Integer rating;
        private final String comment;
        private final String createdAt;

        public ReviewResponse(UUID reviewId,
                              UUID bookingId,
                              UUID sitterId,
                              UUID reviewerId,
                              String reviewerName,
                              Integer rating,
                              String comment,
                              String createdAt) {
            this.reviewId = reviewId;
            this.bookingId = bookingId;
            this.sitterId = sitterId;
            this.reviewerId = reviewerId;
            this.reviewerName = reviewerName;
            this.rating = rating;
            this.comment = comment;
            this.createdAt = createdAt;
        }

        public static ReviewResponse from(Review review) {
            return new ReviewResponse(
                    review.getReviewId(),
                    review.getBooking().getBookingId(),
                    review.getSitter().getUserId(),
                    review.getReviewer().getUserId(),
                    review.getReviewer().getFirstName() + " " + review.getReviewer().getLastName(),
                    review.getRating(),
                    review.getComment(),
                    review.getCreatedAt().atOffset(ZoneOffset.UTC).toString()
            );
        }

        public UUID getReviewId() {
            return reviewId;
        }

        public UUID getBookingId() {
            return bookingId;
        }

        public UUID getSitterId() {
            return sitterId;
        }

        public UUID getReviewerId() {
            return reviewerId;
        }

        public String getReviewerName() {
            return reviewerName;
        }

        public Integer getRating() {
            return rating;
        }

        public String getComment() {
            return comment;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }

    public static class ReviewSummaryResponse {
        private final BigDecimal averageRating;
        private final long reviewCount;

        public ReviewSummaryResponse(BigDecimal averageRating, long reviewCount) {
            this.averageRating = averageRating;
            this.reviewCount = reviewCount;
        }

        public BigDecimal getAverageRating() {
            return averageRating;
        }

        public long getReviewCount() {
            return reviewCount;
        }
    }
}
