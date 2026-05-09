package edu.cit.canonigo.petfriend.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
import edu.cit.canonigo.petfriend.model.Pet;
import edu.cit.canonigo.petfriend.model.ServiceType;
import edu.cit.canonigo.petfriend.model.SitterProfile;
import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.BookingRepository;
import edu.cit.canonigo.petfriend.repository.PetRepository;
import edu.cit.canonigo.petfriend.repository.SitterProfileRepository;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final SitterProfileRepository sitterProfileRepository;

    public BookingController(BookingRepository bookingRepository,
                             UserRepository userRepository,
                             PetRepository petRepository,
                             SitterProfileRepository sitterProfileRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.sitterProfileRepository = sitterProfileRepository;
    }

    @GetMapping
    public ResponseEntity<?> listMyBookings(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "upcoming", required = false, defaultValue = "false") boolean upcoming
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        if (user.getRole() != UserRole.PET_OWNER) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        List<Booking> bookings;
        if (upcoming) {
            bookings = bookingRepository
                    .findByOwner_UserIdAndDateGreaterThanEqualAndStatusNotOrderByDateAscStartTimeAsc(
                            user.getUserId(),
                            LocalDate.now(),
                            BookingStatus.CANCELLED
                    );
        } else {
            bookings = bookingRepository.findByOwner_UserIdOrderByDateAscStartTimeAsc(user.getUserId());
        }

        List<BookingResponse> responses = bookings.stream().map(BookingResponse::from).toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<?> createBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateBookingRequest request
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User owner = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (owner == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        if (owner.getRole() != UserRole.PET_OWNER) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        User sitter = userRepository.findById(request.getSitterId()).orElse(null);
        if (sitter == null || sitter.getRole() != UserRole.PET_SITTER) {
            return ResponseEntity.badRequest().body("Invalid sitterId");
        }

        List<Pet> pets = petRepository.findAllById(request.getPetIds());
        if (pets.size() != request.getPetIds().size()) {
            return ResponseEntity.badRequest().body("One or more pets not found");
        }

        boolean anyNotOwned = pets.stream().anyMatch(p -> !Objects.equals(p.getOwner().getUserId(), owner.getUserId()));
        if (anyNotOwned) {
            return ResponseEntity.status(403).body("You can only book using your own pets");
        }

        if (request.getStartTime().compareTo(request.getEndTime()) >= 0) {
            return ResponseEntity.badRequest().body("End time must be later than start time");
        }

        SitterProfile sitterProfile = sitterProfileRepository.findByUser_UserId(sitter.getUserId()).orElse(null);
        if (sitterProfile == null || sitterProfile.getHourlyRate() == null) {
            return ResponseEntity.badRequest().body("Sitter hourly rate is not configured");
        }

        int startMinutes = request.getStartTime().getHour() * 60 + request.getStartTime().getMinute();
        int endMinutes = request.getEndTime().getHour() * 60 + request.getEndTime().getMinute();
        BigDecimal durationHours = BigDecimal.valueOf(endMinutes - startMinutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        BigDecimal baseAmount = sitterProfile.getHourlyRate()
                .multiply(durationHours)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal serviceFee = baseAmount.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = baseAmount.add(serviceFee).setScale(2, RoundingMode.HALF_UP);

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

        Booking saved = bookingRepository.save(booking);
        return ResponseEntity.ok(BookingResponse.from(saved));
    }

    @GetMapping("/sitter")
    public ResponseEntity<?> listSitterBookings(@AuthenticationPrincipal UserDetails userDetails) {
        User sitter = getAuthenticatedUser(userDetails);
        if (sitter == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        if (sitter.getRole() != UserRole.PET_SITTER) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        List<BookingResponse> responses = bookingRepository
                .findBySitter_UserIdOrderByDateAscStartTimeAsc(sitter.getUserId())
                .stream()
                .map(BookingResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/sitter/pending")
    public ResponseEntity<?> listSitterPendingRequests(@AuthenticationPrincipal UserDetails userDetails) {
        User sitter = getAuthenticatedUser(userDetails);
        if (sitter == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        if (sitter.getRole() != UserRole.PET_SITTER) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        List<BookingResponse> responses = bookingRepository
                .findBySitter_UserIdAndStatusOrderByDateAscStartTimeAsc(sitter.getUserId(), BookingStatus.PENDING)
                .stream()
                .map(BookingResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/sitter/upcoming")
    public ResponseEntity<?> listSitterUpcomingSessions(@AuthenticationPrincipal UserDetails userDetails) {
        User sitter = getAuthenticatedUser(userDetails);
        if (sitter == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        if (sitter.getRole() != UserRole.PET_SITTER) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        List<BookingResponse> responses = bookingRepository
                .findBySitter_UserIdAndStatusAndDateGreaterThanEqualOrderByDateAscStartTimeAsc(
                        sitter.getUserId(),
                        BookingStatus.CONFIRMED,
                        LocalDate.now()
                )
                .stream()
                .map(BookingResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/sitter/today")
    public ResponseEntity<?> listSitterTodaySchedule(@AuthenticationPrincipal UserDetails userDetails) {
        User sitter = getAuthenticatedUser(userDetails);
        if (sitter == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        if (sitter.getRole() != UserRole.PET_SITTER) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        List<BookingResponse> responses = bookingRepository
                .findBySitter_UserIdAndStatusAndDateOrderByStartTimeAsc(
                        sitter.getUserId(),
                        BookingStatus.CONFIRMED,
                        LocalDate.now()
                )
                .stream()
                .map(BookingResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{bookingId}/owner-status")
    public ResponseEntity<?> updateOwnerBookingStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID bookingId,
            @Valid @RequestBody UpdateOwnerBookingStatusRequest request
    ) {
        User owner = getAuthenticatedUser(userDetails);
        if (owner == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        if (owner.getRole() != UserRole.PET_OWNER) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(404).body("Booking not found");
        }

        if (booking.getOwner() == null || !booking.getOwner().getUserId().equals(owner.getUserId())) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        BookingStatus nextStatus = request.getStatus();
        BookingStatus currentStatus = booking.getStatus();

        if (!isAllowedOwnerTransition(currentStatus, nextStatus)) {
            return ResponseEntity.badRequest().body("Invalid status transition");
        }

        booking.setStatus(nextStatus);
        Booking updated = bookingRepository.save(booking);
        return ResponseEntity.ok(BookingResponse.from(updated));
    }

    @PutMapping("/{bookingId}/sitter-status")
    public ResponseEntity<?> updateSitterBookingStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID bookingId,
            @Valid @RequestBody UpdateSitterBookingStatusRequest request
    ) {
        User sitter = getAuthenticatedUser(userDetails);
        if (sitter == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        if (sitter.getRole() != UserRole.PET_SITTER) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(404).body("Booking not found");
        }

        if (booking.getSitter() == null || !booking.getSitter().getUserId().equals(sitter.getUserId())) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        BookingStatus nextStatus = request.getStatus();
        BookingStatus currentStatus = booking.getStatus();

        if (!isAllowedSitterTransition(currentStatus, nextStatus)) {
            return ResponseEntity.badRequest().body("Invalid status transition");
        }

        booking.setStatus(nextStatus);
        Booking updated = bookingRepository.save(booking);
        return ResponseEntity.ok(BookingResponse.from(updated));
    }

    private User getAuthenticatedUser(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }

    private boolean isAllowedSitterTransition(BookingStatus currentStatus, BookingStatus nextStatus) {
        if (currentStatus == BookingStatus.PENDING && (nextStatus == BookingStatus.CONFIRMED || nextStatus == BookingStatus.CANCELLED)) {
            return true;
        }
        return currentStatus == BookingStatus.CONFIRMED && nextStatus == BookingStatus.COMPLETED;
    }

    private boolean isAllowedOwnerTransition(BookingStatus currentStatus, BookingStatus nextStatus) {
        return (currentStatus == BookingStatus.PENDING || currentStatus == BookingStatus.CONFIRMED)
                && nextStatus == BookingStatus.CANCELLED;
    }

    public static class CreateBookingRequest {
        @NotNull
        private UUID sitterId;

        @NotEmpty
        private List<UUID> petIds;

        @NotNull
        private ServiceType serviceType;

        @NotNull
        private LocalDate date;

        @NotNull
        private java.time.LocalTime startTime;

        @NotNull
        private java.time.LocalTime endTime;

        private String specialInstructions;

        public UUID getSitterId() {
            return sitterId;
        }

        public void setSitterId(UUID sitterId) {
            this.sitterId = sitterId;
        }

        public List<UUID> getPetIds() {
            return petIds;
        }

        public void setPetIds(List<UUID> petIds) {
            this.petIds = petIds;
        }

        public ServiceType getServiceType() {
            return serviceType;
        }

        public void setServiceType(ServiceType serviceType) {
            this.serviceType = serviceType;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public java.time.LocalTime getStartTime() {
            return startTime;
        }

        public void setStartTime(java.time.LocalTime startTime) {
            this.startTime = startTime;
        }

        public java.time.LocalTime getEndTime() {
            return endTime;
        }

        public void setEndTime(java.time.LocalTime endTime) {
            this.endTime = endTime;
        }

        public String getSpecialInstructions() {
            return specialInstructions;
        }

        public void setSpecialInstructions(String specialInstructions) {
            this.specialInstructions = specialInstructions;
        }
    }

    public static class UpdateSitterBookingStatusRequest {
        @NotNull
        private BookingStatus status;

        public BookingStatus getStatus() {
            return status;
        }

        public void setStatus(BookingStatus status) {
            this.status = status;
        }
    }

    public static class UpdateOwnerBookingStatusRequest {
        @NotNull
        private BookingStatus status;

        public BookingStatus getStatus() {
            return status;
        }

        public void setStatus(BookingStatus status) {
            this.status = status;
        }
    }

    public static class BookingResponse {
        private UUID bookingId;
        private UUID ownerId;
        private String ownerName;
        private UUID sitterId;
        private String sitterName;
        private ServiceType serviceType;
        private LocalDate date;
        private java.time.LocalTime startTime;
        private java.time.LocalTime endTime;
        private BookingStatus status;
        private List<String> petNames;
        private List<UUID> petIds;
        private java.math.BigDecimal totalAmount;
        private String currency;

        public static BookingResponse from(Booking booking) {
            BookingResponse r = new BookingResponse();
            r.bookingId = booking.getBookingId();
            r.ownerId = booking.getOwner().getUserId();
                r.ownerName = booking.getOwner().getFirstName() + " " + booking.getOwner().getLastName();
            r.sitterId = booking.getSitter() != null ? booking.getSitter().getUserId() : null;
            r.sitterName = booking.getSitter() != null
                    ? booking.getSitter().getFirstName() + " " + booking.getSitter().getLastName()
                    : null;
            r.serviceType = booking.getServiceType();
            r.date = booking.getDate();
            r.startTime = booking.getStartTime();
            r.endTime = booking.getEndTime();
            r.status = booking.getStatus();
                r.petNames = booking.getPets().stream().map(Pet::getName).toList();
            r.petIds = booking.getPets().stream().map(Pet::getPetId).toList();
                r.totalAmount = booking.getTotalAmount();
                r.currency = booking.getCurrency();
            return r;
        }

        public UUID getBookingId() {
            return bookingId;
        }

        public UUID getOwnerId() {
            return ownerId;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public UUID getSitterId() {
            return sitterId;
        }

        public String getSitterName() {
            return sitterName;
        }

        public ServiceType getServiceType() {
            return serviceType;
        }

        public LocalDate getDate() {
            return date;
        }

        public java.time.LocalTime getStartTime() {
            return startTime;
        }

        public java.time.LocalTime getEndTime() {
            return endTime;
        }

        public BookingStatus getStatus() {
            return status;
        }

        public List<String> getPetNames() {
            return petNames;
        }

        public List<UUID> getPetIds() {
            return petIds;
        }

        public java.math.BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public String getCurrency() {
            return currency;
        }
    }
}
