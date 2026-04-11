package edu.cit.canonigo.petfriend.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
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
import edu.cit.canonigo.petfriend.model.SitterProfile;
import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.BookingRepository;
import edu.cit.canonigo.petfriend.repository.SitterProfileRepository;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AdminController {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final SitterProfileRepository sitterProfileRepository;

    public AdminController(UserRepository userRepository,
                           BookingRepository bookingRepository,
                           SitterProfileRepository sitterProfileRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.sitterProfileRepository = sitterProfileRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@AuthenticationPrincipal UserDetails userDetails) {
        User admin = getAuthenticatedAdmin(userDetails);
        if (admin == null) {
            return forbiddenOrUnauthorized(userDetails);
        }

        long totalUsers = userRepository.count();
        long petOwners = userRepository.countByRole(UserRole.PET_OWNER);
        long petSitters = userRepository.countByRole(UserRole.PET_SITTER);

        List<User> allUsers = userRepository.findAll();
        long pendingApprovals = allUsers.stream()
                .filter(u -> u.getRole() == UserRole.PET_SITTER)
                .filter(u -> !Boolean.TRUE.equals(u.getIsVerified()))
                .count();

        long totalBookings = bookingRepository.count();

        LocalDate todayUtc = LocalDate.now(ZoneOffset.UTC);
        LocalDate startOfMonth = todayUtc.withDayOfMonth(1);
        LocalDate endOfMonth = todayUtc.with(TemporalAdjusters.lastDayOfMonth());

        List<Booking> monthlyBookings = bookingRepository.findAllByDateBetween(startOfMonth, endOfMonth);
        BigDecimal monthRevenue = monthlyBookings.stream()
                .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
                .map(Booking::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal platformFees = monthRevenue.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal sitterPayouts = monthRevenue.subtract(platformFees).setScale(2, RoundingMode.HALF_UP);

        List<TimedActivity> timeline = new ArrayList<>();

        for (User recentUser : userRepository.findTop5ByOrderByCreatedAtDesc()) {
            timeline.add(new TimedActivity(
                    recentUser.getCreatedAt(),
                    "user",
                    "New user registration: " + safeName(recentUser) + " (" + formatRole(recentUser.getRole()) + ")"
            ));
        }

        for (Booking booking : bookingRepository.findTop5ByOrderByCreatedAtDesc()) {
            String action = booking.getStatus() == BookingStatus.COMPLETED ? "Booking completed" : "New booking";
            timeline.add(new TimedActivity(
                    booking.getCreatedAt(),
                    "booking",
                    action + ": " + shortBookingId(booking)
            ));
        }

        List<ActivityItem> recentActivity = timeline.stream()
                .filter(item -> item.createdAt != null)
                .sorted(Comparator.comparing((TimedActivity item) -> item.createdAt).reversed())
                .limit(5)
                .map(item -> new ActivityItem(item.type, item.message, toRelativeTime(item.createdAt)))
                .toList();

        return ResponseEntity.ok(new DashboardResponse(
                totalUsers,
                petOwners,
                petSitters,
                pendingApprovals,
                totalBookings,
                monthRevenue,
                platformFees,
                sitterPayouts,
                recentActivity
        ));
    }

    @GetMapping("/sitters/pending")
    public ResponseEntity<?> getPendingSitters(@AuthenticationPrincipal UserDetails userDetails) {
        User admin = getAuthenticatedAdmin(userDetails);
        if (admin == null) {
            return forbiddenOrUnauthorized(userDetails);
        }

        List<PendingSitterItem> pendingSitters = userRepository.findByRoleOrderByCreatedAtAsc(UserRole.PET_SITTER)
                .stream()
                .filter(sitter -> !Boolean.TRUE.equals(sitter.getIsVerified()))
                .map(sitter -> {
                    SitterProfile profile = sitterProfileRepository.findByUser_UserId(sitter.getUserId()).orElse(null);
                    return new PendingSitterItem(
                            sitter.getUserId(),
                            safeName(sitter),
                            sitter.getEmail(),
                            profile != null ? profile.getStudentId() : null,
                            profile != null ? profile.getBio() : null,
                            profile != null ? profile.getExperience() : null,
                            profile != null ? profile.getHourlyRate() : null,
                            profile != null ? profile.getServicesJson() : "[]",
                            profile != null ? profile.getVerificationDocumentUrl() : null,
                            sitter.getCreatedAt()
                    );
                })
                .toList();

        return ResponseEntity.ok(pendingSitters);
    }

    @PostMapping("/sitters/{sitterId}/approve")
    public ResponseEntity<?> approveSitter(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID sitterId
    ) {
        User admin = getAuthenticatedAdmin(userDetails);
        if (admin == null) {
            return forbiddenOrUnauthorized(userDetails);
        }

        User sitter = userRepository.findById(sitterId).orElse(null);
        if (sitter == null || sitter.getRole() != UserRole.PET_SITTER) {
            return ResponseEntity.status(404).body("Sitter not found");
        }

        sitter.setIsVerified(true);
        userRepository.save(sitter);
        return ResponseEntity.ok(new ActionResponse("Sitter approved successfully."));
    }

    @PostMapping("/sitters/{sitterId}/reject")
    public ResponseEntity<?> rejectSitter(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID sitterId,
            @Valid @RequestBody(required = false) RejectRequest request
    ) {
        User admin = getAuthenticatedAdmin(userDetails);
        if (admin == null) {
            return forbiddenOrUnauthorized(userDetails);
        }

        User sitter = userRepository.findById(sitterId).orElse(null);
        if (sitter == null || sitter.getRole() != UserRole.PET_SITTER) {
            return ResponseEntity.status(404).body("Sitter not found");
        }

        sitter.setIsVerified(false);
        userRepository.save(sitter);

        String message = "Sitter application rejected.";
        if (request != null && request.getReason() != null && !request.getReason().isBlank()) {
            message = message + " Reason: " + request.getReason().trim();
        }
        return ResponseEntity.ok(new ActionResponse(message));
    }

    private ResponseEntity<String> forbiddenOrUnauthorized(UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return ResponseEntity.status(403).body("Forbidden");
    }

    private User getAuthenticatedAdmin(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null || user.getRole() != UserRole.ADMIN) {
            return null;
        }
        return user;
    }

    private String safeName(User user) {
        String first = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String last = user.getLastName() == null ? "" : user.getLastName().trim();
        String full = (first + " " + last).trim();
        return full.isEmpty() ? user.getEmail() : full;
    }

    private String formatRole(UserRole role) {
        if (role == null) {
            return "User";
        }
        return switch (role) {
            case ADMIN -> "Admin";
            case PET_OWNER -> "Pet Owner";
            case PET_SITTER -> "Pet Sitter";
        };
    }

    private String shortBookingId(Booking booking) {
        if (booking.getBookingId() == null) {
            return "N/A";
        }
        String id = booking.getBookingId().toString().replace("-", "");
        String shortId = id.length() > 8 ? id.substring(0, 8) : id;
        return "bk_" + shortId;
    }

    private String toRelativeTime(Instant timestamp) {
        Instant now = Instant.now();
        Duration duration = Duration.between(timestamp, now);

        long minutes = Math.max(0, duration.toMinutes());
        if (minutes < 1) {
            return "just now";
        }
        if (minutes < 60) {
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        }

        long hours = duration.toHours();
        if (hours < 24) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        }

        long days = duration.toDays();
        return days + (days == 1 ? " day ago" : " days ago");
    }

    private static class TimedActivity {
        private final Instant createdAt;
        private final String type;
        private final String message;

        private TimedActivity(Instant createdAt, String type, String message) {
            this.createdAt = createdAt;
            this.type = type;
            this.message = message;
        }
    }

    public static class DashboardResponse {
        private final long totalUsers;
        private final long petOwners;
        private final long petSitters;
        private final long pendingApprovals;
        private final long totalBookings;
        private final BigDecimal monthRevenue;
        private final BigDecimal platformFees;
        private final BigDecimal sitterPayouts;
        private final List<ActivityItem> recentActivity;

        public DashboardResponse(long totalUsers,
                                 long petOwners,
                                 long petSitters,
                                 long pendingApprovals,
                                 long totalBookings,
                                 BigDecimal monthRevenue,
                                 BigDecimal platformFees,
                                 BigDecimal sitterPayouts,
                                 List<ActivityItem> recentActivity) {
            this.totalUsers = totalUsers;
            this.petOwners = petOwners;
            this.petSitters = petSitters;
            this.pendingApprovals = pendingApprovals;
            this.totalBookings = totalBookings;
            this.monthRevenue = monthRevenue;
            this.platformFees = platformFees;
            this.sitterPayouts = sitterPayouts;
            this.recentActivity = recentActivity;
        }

        public long getTotalUsers() {
            return totalUsers;
        }

        public long getPetOwners() {
            return petOwners;
        }

        public long getPetSitters() {
            return petSitters;
        }

        public long getPendingApprovals() {
            return pendingApprovals;
        }

        public long getTotalBookings() {
            return totalBookings;
        }

        public BigDecimal getMonthRevenue() {
            return monthRevenue;
        }

        public BigDecimal getPlatformFees() {
            return platformFees;
        }

        public BigDecimal getSitterPayouts() {
            return sitterPayouts;
        }

        public List<ActivityItem> getRecentActivity() {
            return recentActivity;
        }
    }

    public static class ActivityItem {
        private final String type;
        private final String message;
        private final String ago;

        public ActivityItem(String type, String message, String ago) {
            this.type = type;
            this.message = message;
            this.ago = ago;
        }

        public String getType() {
            return type;
        }

        public String getMessage() {
            return message;
        }

        public String getAgo() {
            return ago;
        }
    }

    public static class PendingSitterItem {
        private final UUID sitterId;
        private final String fullName;
        private final String email;
        private final String studentId;
        private final String bio;
        private final String experience;
        private final BigDecimal hourlyRate;
        private final String servicesJson;
        private final String verificationDocumentUrl;
        private final Instant submittedAt;

        public PendingSitterItem(UUID sitterId,
                                 String fullName,
                                 String email,
                                 String studentId,
                                 String bio,
                                 String experience,
                                 BigDecimal hourlyRate,
                                 String servicesJson,
                                 String verificationDocumentUrl,
                                 Instant submittedAt) {
            this.sitterId = sitterId;
            this.fullName = fullName;
            this.email = email;
            this.studentId = studentId;
            this.bio = bio;
            this.experience = experience;
            this.hourlyRate = hourlyRate;
            this.servicesJson = servicesJson;
            this.verificationDocumentUrl = verificationDocumentUrl;
            this.submittedAt = submittedAt;
        }

        public UUID getSitterId() {
            return sitterId;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmail() {
            return email;
        }

        public String getStudentId() {
            return studentId;
        }

        public String getBio() {
            return bio;
        }

        public String getExperience() {
            return experience;
        }

        public BigDecimal getHourlyRate() {
            return hourlyRate;
        }

        public String getServicesJson() {
            return servicesJson;
        }

        public String getVerificationDocumentUrl() {
            return verificationDocumentUrl;
        }

        public Instant getSubmittedAt() {
            return submittedAt;
        }
    }

    public static class RejectRequest {
        @NotBlank(message = "Reason is required")
        private String reason;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public static class ActionResponse {
        private final String message;

        public ActionResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}