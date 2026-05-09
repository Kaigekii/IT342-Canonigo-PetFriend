package edu.cit.canonigo.petfriend.features.reviews;

import edu.cit.canonigo.petfriend.model.Booking;
import edu.cit.canonigo.petfriend.model.BookingStatus;
import edu.cit.canonigo.petfriend.model.Review;
import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.BookingRepository;
import edu.cit.canonigo.petfriend.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

/**
 * Service for review business logic
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    public ReviewService(ReviewRepository reviewRepository, BookingRepository bookingRepository) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Submit a review for a completed booking
     */
    public ReviewDtos.ReviewResponse submitReview(User reviewer, UUID bookingId, Integer rating, String comment) {
        // Verify reviewer is pet owner
        if (reviewer.getRole() != UserRole.PET_OWNER) {
            throw new IllegalAccessError("Only pet owners can submit reviews");
        }

        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found");
        }

        // Verify ownership
        if (!booking.getOwner().getUserId().equals(reviewer.getUserId())) {
            throw new IllegalAccessError("You can only review your own completed bookings");
        }

        // Verify booking is completed
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new IllegalStateException("Review is allowed only after booking is completed");
        }

        // Verify sitter exists
        if (booking.getSitter() == null) {
            throw new IllegalStateException("Booking has no sitter assigned");
        }

        // Check for duplicate review
        if (reviewRepository.existsByBooking_BookingId(booking.getBookingId())) {
            throw new IllegalStateException("A review already exists for this booking");
        }

        Review review = new Review();
        review.setBooking(booking);
        review.setSitter(booking.getSitter());
        review.setReviewer(reviewer);
        review.setRating(rating);
        review.setComment(comment.trim());

        Review saved = reviewRepository.save(review);
        return toReviewResponse(saved);
    }

    /**
     * Get all reviews for a sitter
     */
    public List<ReviewDtos.ReviewResponse> listSitterReviews(UUID sitterId) {
        return reviewRepository.findBySitter_UserIdOrderByCreatedAtDesc(sitterId)
                .stream()
                .map(this::toReviewResponse)
                .toList();
    }

    /**
     * Get review summary for a sitter
     */
    public ReviewDtos.ReviewSummaryResponse getSitterReviewSummary(UUID sitterId) {
        long count = reviewRepository.countBySitter_UserId(sitterId);
        Double avg = reviewRepository.findAverageRatingBySitterId(sitterId);
        BigDecimal averageRating = BigDecimal.valueOf(avg == null ? 0.0 : avg)
                .setScale(1, RoundingMode.HALF_UP);

        return new ReviewDtos.ReviewSummaryResponse(averageRating, count);
    }

    /**
     * Get IDs of bookings already reviewed by a user
     */
    public List<UUID> getReviewedBookingIds(UUID userId) {
        return reviewRepository.findReviewedBookingIdsByReviewerId(userId);
    }

    // ========== Helper Methods ==========

    private ReviewDtos.ReviewResponse toReviewResponse(Review review) {
        return new ReviewDtos.ReviewResponse(
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
}
