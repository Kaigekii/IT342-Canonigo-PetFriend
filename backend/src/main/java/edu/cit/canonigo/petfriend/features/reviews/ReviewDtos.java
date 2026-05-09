package edu.cit.canonigo.petfriend.features.reviews;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Data Transfer Objects for Reviews features
 */
public class ReviewDtos {

    /**
     * Request DTO for submitting a new review
     */
    public static class CreateReviewRequest {
        @NotNull
        private UUID bookingId;

        @NotNull
        @Min(1)
        @Max(5)
        private Integer rating;

        @NotBlank
        private String comment;

        public UUID getBookingId() { return bookingId; }
        public void setBookingId(UUID bookingId) { this.bookingId = bookingId; }

        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }

        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }

    /**
     * Response DTO for individual review
     */
    public static class ReviewResponse {
        private final UUID reviewId;
        private final UUID bookingId;
        private final UUID sitterId;
        private final UUID reviewerId;
        private final String reviewerName;
        private final Integer rating;
        private final String comment;
        private final String createdAt;

        public ReviewResponse(UUID reviewId, UUID bookingId, UUID sitterId, UUID reviewerId,
                             String reviewerName, Integer rating, String comment, String createdAt) {
            this.reviewId = reviewId;
            this.bookingId = bookingId;
            this.sitterId = sitterId;
            this.reviewerId = reviewerId;
            this.reviewerName = reviewerName;
            this.rating = rating;
            this.comment = comment;
            this.createdAt = createdAt;
        }

        public UUID getReviewId() { return reviewId; }
        public UUID getBookingId() { return bookingId; }
        public UUID getSitterId() { return sitterId; }
        public UUID getReviewerId() { return reviewerId; }
        public String getReviewerName() { return reviewerName; }
        public Integer getRating() { return rating; }
        public String getComment() { return comment; }
        public String getCreatedAt() { return createdAt; }
    }

    /**
     * Response DTO for sitter review summary
     */
    public static class ReviewSummaryResponse {
        private final BigDecimal averageRating;
        private final long reviewCount;

        public ReviewSummaryResponse(BigDecimal averageRating, long reviewCount) {
            this.averageRating = averageRating;
            this.reviewCount = reviewCount;
        }

        public BigDecimal getAverageRating() { return averageRating; }
        public long getReviewCount() { return reviewCount; }
    }
}
