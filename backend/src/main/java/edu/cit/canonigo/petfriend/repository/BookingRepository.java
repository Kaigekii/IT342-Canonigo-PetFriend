package edu.cit.canonigo.petfriend.repository;

import edu.cit.canonigo.petfriend.model.Booking;
import edu.cit.canonigo.petfriend.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByOwner_UserIdOrderByDateAscStartTimeAsc(UUID ownerId);

    List<Booking> findByOwner_UserIdAndDateGreaterThanEqualAndStatusNotOrderByDateAscStartTimeAsc(
            UUID ownerId,
            LocalDate date,
            BookingStatus status
    );
}
