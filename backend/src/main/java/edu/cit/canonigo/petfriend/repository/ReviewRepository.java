package edu.cit.canonigo.petfriend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.cit.canonigo.petfriend.model.Review;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    boolean existsByBooking_BookingId(UUID bookingId);

    long countBySitter_UserId(UUID sitterId);

    List<Review> findBySitter_UserIdOrderByCreatedAtDesc(UUID sitterId);

    List<Review> findByReviewer_UserIdOrderByCreatedAtDesc(UUID reviewerId);

    @Query("select avg(r.rating) from Review r where r.sitter.userId = :sitterId")
    Double findAverageRatingBySitterId(@Param("sitterId") UUID sitterId);

    @Query("select r.booking.bookingId from Review r where r.reviewer.userId = :reviewerId")
    List<UUID> findReviewedBookingIdsByReviewerId(@Param("reviewerId") UUID reviewerId);
}
