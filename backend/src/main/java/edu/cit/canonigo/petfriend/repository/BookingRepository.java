package edu.cit.canonigo.petfriend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.canonigo.petfriend.model.Booking;
import edu.cit.canonigo.petfriend.model.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByOwner_UserIdOrderByDateAscStartTimeAsc(UUID ownerId);

    List<Booking> findByOwner_UserIdAndDateGreaterThanEqualAndStatusNotOrderByDateAscStartTimeAsc(
            UUID ownerId,
            LocalDate date,
            BookingStatus status
    );

    List<Booking> findBySitter_UserIdOrderByDateAscStartTimeAsc(UUID sitterId);

    List<Booking> findBySitter_UserIdAndStatusOrderByDateAscStartTimeAsc(UUID sitterId, BookingStatus status);

    List<Booking> findBySitter_UserIdAndStatusAndDateGreaterThanEqualOrderByDateAscStartTimeAsc(
        UUID sitterId,
        BookingStatus status,
        LocalDate date
    );

    List<Booking> findBySitter_UserIdAndStatusAndDateOrderByStartTimeAsc(
        UUID sitterId,
        BookingStatus status,
        LocalDate date
    );

    List<Booking> findTop5ByOrderByCreatedAtDesc();

    List<Booking> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

    boolean existsByOwner_UserIdAndSitter_UserId(UUID ownerId, UUID sitterId);
}
