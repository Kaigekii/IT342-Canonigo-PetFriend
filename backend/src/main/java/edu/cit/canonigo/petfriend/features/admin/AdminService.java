package edu.cit.canonigo.petfriend.features.admin;

import edu.cit.canonigo.petfriend.model.Booking;
import edu.cit.canonigo.petfriend.model.BookingStatus;
import edu.cit.canonigo.petfriend.model.SitterProfile;
import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.BookingRepository;
import edu.cit.canonigo.petfriend.repository.SitterProfileRepository;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import org.springframework.stereotype.Service;

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

/**
 * Service for admin business logic
 */
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final SitterProfileRepository sitterProfileRepository;

    public AdminService(UserRepository userRepository, BookingRepository bookingRepository,
                       SitterProfileRepository sitterProfileRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.sitterProfileRepository = sitterProfileRepository;
    }

    /**
     * Get admin dashboard with statistics
     */
    public AdminDtos.DashboardResponse getDashboard() {
        long totalUsers = userRepository.count();
        long petOwners = userRepository.countByRole(UserRole.PET_OWNER);
        long petSitters = userRepository.countByRole(UserRole.PET_SITTER);

        List<User> allUsers = userRepository.findAll();
        long pendingApprovals = allUsers.stream()
                .filter(u -> u.getRole() == UserRole.PET_SITTER)
                .filter(u -> !Boolean.TRUE.equals(u.getIsVerified()))
                .count();

        long totalBookings = bookingRepository.count();

        // Calculate monthly revenue
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

        // Build activity timeline
        List<AdminDtos.ActivityItem> recentActivity = buildActivityTimeline();

        return new AdminDtos.DashboardResponse(
                totalUsers, petOwners, petSitters, pendingApprovals, totalBookings,
                monthRevenue, platformFees, sitterPayouts, recentActivity
        );
    }

    /**
     * Get pending sitters awaiting verification
     */
    public List<AdminDtos.PendingSitterItem> getPendingSitters() {
        return userRepository.findByRoleOrderByCreatedAtAsc(UserRole.PET_SITTER)
                .stream()
                .filter(sitter -> !Boolean.TRUE.equals(sitter.getIsVerified()))
                .map(sitter -> {
                    SitterProfile profile = sitterProfileRepository.findByUser_UserId(sitter.getUserId()).orElse(null);
                    return new AdminDtos.PendingSitterItem(
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
    }

    /**
     * Approve a sitter
     */
    public AdminDtos.ActionResponse approveSitter(UUID sitterId) {
        User sitter = userRepository.findById(sitterId).orElse(null);
        if (sitter == null || sitter.getRole() != UserRole.PET_SITTER) {
            throw new IllegalArgumentException("Sitter not found");
        }

        sitter.setIsVerified(true);
        userRepository.save(sitter);
        return new AdminDtos.ActionResponse("Sitter approved successfully.");
    }

    /**
     * Reject a sitter
     */
    public AdminDtos.ActionResponse rejectSitter(UUID sitterId, String reason) {
        User sitter = userRepository.findById(sitterId).orElse(null);
        if (sitter == null || sitter.getRole() != UserRole.PET_SITTER) {
            throw new IllegalArgumentException("Sitter not found");
        }

        sitter.setIsVerified(false);
        userRepository.save(sitter);

        String message = "Sitter application rejected.";
        if (reason != null && !reason.isBlank()) {
            message = message + " Reason: " + reason.trim();
        }
        return new AdminDtos.ActionResponse(message);
    }

    /**
     * List all users
     */
    public List<AdminDtos.AdminUserItem> listAllUsers() {
        return userRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(User::getCreatedAt).reversed())
                .map(user -> new AdminDtos.AdminUserItem(
                        user.getUserId(),
                        safeName(user),
                        user.getEmail(),
                        user.getRole(),
                        Boolean.TRUE.equals(user.getIsVerified()),
                        user.getCreatedAt()
                ))
                .toList();
    }

    /**
     * List all bookings
     */
    public List<AdminDtos.AdminBookingItem> listAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .sorted(Comparator
                        .comparing(Booking::getDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed()
                        .thenComparing(Booking::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(booking -> new AdminDtos.AdminBookingItem(
                        booking.getBookingId(),
                        booking.getOwner() != null ? safeName(booking.getOwner()) : "Unknown",
                        booking.getSitter() != null ? safeName(booking.getSitter()) : "Unassigned",
                        booking.getServiceType(),
                        booking.getDate(),
                        booking.getStatus(),
                        booking.getTotalAmount(),
                        booking.getCurrency()
                ))
                .toList();
    }

    // ========== Helper Methods ==========

    private List<AdminDtos.ActivityItem> buildActivityTimeline() {
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

        return timeline.stream()
                .filter(item -> item.createdAt != null)
                .sorted(Comparator.comparing((TimedActivity item) -> item.createdAt).reversed())
                .limit(5)
                .map(item -> new AdminDtos.ActivityItem(item.type, item.message, toRelativeTime(item.createdAt)))
                .toList();
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
}
