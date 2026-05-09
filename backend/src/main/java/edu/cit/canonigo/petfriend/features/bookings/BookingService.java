package edu.cit.canonigo.petfriend.features.bookings;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import edu.cit.canonigo.petfriend.model.Booking;
import edu.cit.canonigo.petfriend.model.BookingStatus;
import edu.cit.canonigo.petfriend.model.Pet;
import edu.cit.canonigo.petfriend.model.SitterProfile;
import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.BookingRepository;
import edu.cit.canonigo.petfriend.repository.PetRepository;
import edu.cit.canonigo.petfriend.repository.SitterProfileRepository;
import edu.cit.canonigo.petfriend.repository.UserRepository;

/**
 * Service layer for booking business logic.
 * Handles creation, retrieval, and status management of bookings.
 */
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final SitterProfileRepository sitterProfileRepository;

    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          PetRepository petRepository,
                          SitterProfileRepository sitterProfileRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.sitterProfileRepository = sitterProfileRepository;
    }

    /**
     * Get bookings for the authenticated owner.
     */
    public List<Booking> getOwnerBookings(UUID ownerId, boolean upcomingOnly) {
        if (upcomingOnly) {
            return bookingRepository.findByOwner_UserIdAndDateGreaterThanEqualAndStatusNotOrderByDateAscStartTimeAsc(
                    ownerId,
                    LocalDate.now(),
                    BookingStatus.CANCELLED
            );
        } else {
            return bookingRepository.findByOwner_UserIdOrderByDateAscStartTimeAsc(ownerId);
        }
    }

    /**
     * Get all bookings for the authenticated sitter.
     */
    public List<Booking> getSitterBookings(UUID sitterId) {
        return bookingRepository.findBySitter_UserIdOrderByDateAscStartTimeAsc(sitterId);
    }

    /**
     * Get pending booking requests for the sitter.
     */
    public List<Booking> getSitterPendingRequests(UUID sitterId) {
        return bookingRepository.findBySitter_UserIdAndStatusOrderByDateAscStartTimeAsc(
                sitterId,
                BookingStatus.PENDING
        );
    }

    /**
     * Get upcoming confirmed bookings for the sitter.
     */
    public List<Booking> getSitterUpcomingSessions(UUID sitterId) {
        return bookingRepository.findBySitter_UserIdAndStatusAndDateGreaterThanEqualOrderByDateAscStartTimeAsc(
                sitterId,
                BookingStatus.CONFIRMED,
                LocalDate.now()
        );
    }

    /**
     * Get today's confirmed bookings for the sitter.
     */
    public List<Booking> getSitterTodaySchedule(UUID sitterId) {
        return bookingRepository.findBySitter_UserIdAndStatusAndDateOrderByStartTimeAsc(
                sitterId,
                BookingStatus.CONFIRMED,
                LocalDate.now()
        );
    }

    /**
     * Create a new booking request.
     * Validates all prerequisites and calculates pricing.
     */
    public Booking createBooking(UUID ownerId, BookingDtos.CreateBookingRequest request) throws BookingException {
        // Fetch and validate owner
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new BookingException("Owner not found"));

        if (owner.getRole() != UserRole.PET_OWNER) {
            throw new BookingException("Only pet owners can create bookings");
        }

        // Fetch and validate sitter
        User sitter = userRepository.findById(request.getSitterId())
                .orElseThrow(() -> new BookingException("Sitter not found"));

        if (sitter.getRole() != UserRole.PET_SITTER) {
            throw new BookingException("Invalid sitter");
        }

        // Fetch and validate pets
        List<Pet> pets = petRepository.findAllById(request.getPetIds());
        if (pets.size() != request.getPetIds().size()) {
            throw new BookingException("One or more pets not found");
        }

        // Ensure all pets belong to the owner
        boolean anyNotOwned = pets.stream()
                .anyMatch(p -> !Objects.equals(p.getOwner().getUserId(), owner.getUserId()));
        if (anyNotOwned) {
            throw new BookingException("All pets must belong to you");
        }

        // Validate times
        if (request.getStartTime().compareTo(request.getEndTime()) >= 0) {
            throw new BookingException("End time must be after start time");
        }

        // Fetch sitter profile and validate hourly rate
        SitterProfile sitterProfile = sitterProfileRepository.findByUser_UserId(sitter.getUserId())
                .orElseThrow(() -> new BookingException("Sitter profile not found"));

        if (sitterProfile.getHourlyRate() == null) {
            throw new BookingException("Sitter has not set their hourly rate");
        }

        // Calculate pricing
        BigDecimal totalAmount = calculateTotalAmount(
                request.getStartTime(),
                request.getEndTime(),
                sitterProfile.getHourlyRate()
        );

        // Create and save booking
        Booking booking = new Booking();
        booking.setOwner(owner);
        booking.setSitter(sitter);
        booking.setServiceType(request.getServiceType());
        booking.setDate(request.getDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setSpecialInstructions(request.getSpecialInstructions());
        booking.setStatus(BookingStatus.PENDING);
        booking.setPets(new HashSet<>(pets));
        booking.setTotalAmount(totalAmount);
        booking.setCurrency("PHP");

        return bookingRepository.save(booking);
    }

    /**
     * Update sitter booking status with validation.
     */
    public Booking updateSitterBookingStatus(UUID sitterId, UUID bookingId, BookingStatus newStatus) throws BookingException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingException("Booking not found"));

        if (booking.getSitter() == null || !booking.getSitter().getUserId().equals(sitterId)) {
            throw new BookingException("You are not authorized to update this booking");
        }

        BookingStatus currentStatus = booking.getStatus();
        if (!isAllowedSitterTransition(currentStatus, newStatus)) {
            throw new BookingException("Invalid status transition: " + currentStatus + " -> " + newStatus);
        }

        booking.setStatus(newStatus);
        return bookingRepository.save(booking);
    }

    /**
     * Update owner booking status with validation.
     */
    public Booking updateOwnerBookingStatus(UUID ownerId, UUID bookingId, BookingStatus newStatus) throws BookingException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingException("Booking not found"));

        if (booking.getOwner() == null || !booking.getOwner().getUserId().equals(ownerId)) {
            throw new BookingException("You are not authorized to update this booking");
        }

        BookingStatus currentStatus = booking.getStatus();
        if (!isAllowedOwnerTransition(currentStatus, newStatus)) {
            throw new BookingException("Invalid status transition: " + currentStatus + " -> " + newStatus);
        }

        booking.setStatus(newStatus);
        return bookingRepository.save(booking);
    }

    /**
     * Calculate total amount including service fee.
     * Formula: baseAmount = hourlyRate * durationHours, serviceFee = 10% of baseAmount, total = baseAmount + serviceFee
     */
    private BigDecimal calculateTotalAmount(java.time.LocalTime startTime, java.time.LocalTime endTime, BigDecimal hourlyRate) {
        int startMinutes = startTime.getHour() * 60 + startTime.getMinute();
        int endMinutes = endTime.getHour() * 60 + endTime.getMinute();
        
        BigDecimal durationHours = BigDecimal.valueOf(endMinutes - startMinutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        BigDecimal baseAmount = hourlyRate
                .multiply(durationHours)
                .setScale(2, RoundingMode.HALF_UP);
        
        BigDecimal serviceFee = baseAmount
                .multiply(new BigDecimal("0.10"))
                .setScale(2, RoundingMode.HALF_UP);
        
        return baseAmount.add(serviceFee).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Validates sitter status transitions.
     * Allowed: PENDING -> CONFIRMED, PENDING -> CANCELLED, CONFIRMED -> COMPLETED
     */
    private boolean isAllowedSitterTransition(BookingStatus currentStatus, BookingStatus nextStatus) {
        if (currentStatus == BookingStatus.PENDING) {
            return nextStatus == BookingStatus.CONFIRMED || nextStatus == BookingStatus.CANCELLED;
        }
        return currentStatus == BookingStatus.CONFIRMED && nextStatus == BookingStatus.COMPLETED;
    }

    /**
     * Validates owner status transitions.
     * Allowed: PENDING -> CANCELLED, CONFIRMED -> CANCELLED
     */
    private boolean isAllowedOwnerTransition(BookingStatus currentStatus, BookingStatus nextStatus) {
        return (currentStatus == BookingStatus.PENDING || currentStatus == BookingStatus.CONFIRMED)
                && nextStatus == BookingStatus.CANCELLED;
    }

    /**
     * Custom exception for booking-related errors.
     */
    public static class BookingException extends Exception {
        public BookingException(String message) {
            super(message);
        }
    }
}
