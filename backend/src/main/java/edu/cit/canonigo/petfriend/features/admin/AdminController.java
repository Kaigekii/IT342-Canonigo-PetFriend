package edu.cit.canonigo.petfriend.features.admin;

import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for admin dashboard and management
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AdminController {

    private final UserRepository userRepository;
    private final AdminService adminService;

    public AdminController(UserRepository userRepository, AdminService adminService) {
        this.userRepository = userRepository;
        this.adminService = adminService;
    }

    /**
     * Get admin dashboard with statistics and recent activity
     * 
     * @param userDetails Authenticated user (must be ADMIN role)
     * @return Dashboard with stats, revenue, and activity timeline
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@AuthenticationPrincipal UserDetails userDetails) {
        User admin = getAuthenticatedAdmin(userDetails);
        if (admin == null) {
            return forbiddenOrUnauthorized(userDetails);
        }

        AdminDtos.DashboardResponse dashboard = adminService.getDashboard();
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Get list of pending sitters awaiting verification
     * 
     * @param userDetails Authenticated user (must be ADMIN role)
     * @return List of pending sitters
     */
    @GetMapping("/sitters/pending")
    public ResponseEntity<?> getPendingSitters(@AuthenticationPrincipal UserDetails userDetails) {
        User admin = getAuthenticatedAdmin(userDetails);
        if (admin == null) {
            return forbiddenOrUnauthorized(userDetails);
        }

        List<AdminDtos.PendingSitterItem> pendingSitters = adminService.getPendingSitters();
        return ResponseEntity.ok(pendingSitters);
    }

    /**
     * Approve a pending sitter
     * 
     * @param userDetails Authenticated user (must be ADMIN role)
     * @param sitterId ID of the sitter to approve
     * @return Success message
     */
    @PostMapping("/sitters/{sitterId}/approve")
    public ResponseEntity<?> approveSitter(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID sitterId
    ) {
        User admin = getAuthenticatedAdmin(userDetails);
        if (admin == null) {
            return forbiddenOrUnauthorized(userDetails);
        }

        try {
            AdminDtos.ActionResponse response = adminService.approveSitter(sitterId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Sitter not found");
        }
    }

    /**
     * Reject a pending sitter
     * 
     * @param userDetails Authenticated user (must be ADMIN role)
     * @param sitterId ID of the sitter to reject
     * @param request Optional rejection request with reason
     * @return Success message with reason if provided
     */
    @PostMapping("/sitters/{sitterId}/reject")
    public ResponseEntity<?> rejectSitter(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID sitterId,
            @Valid @RequestBody(required = false) AdminDtos.RejectRequest request
    ) {
        User admin = getAuthenticatedAdmin(userDetails);
        if (admin == null) {
            return forbiddenOrUnauthorized(userDetails);
        }

        try {
            String reason = request != null ? request.getReason() : null;
            AdminDtos.ActionResponse response = adminService.rejectSitter(sitterId, reason);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Sitter not found");
        }
    }

    /**
     * List all users in the system
     * 
     * @param userDetails Authenticated user (must be ADMIN role)
     * @return List of all users with roles and verification status
     */
    @GetMapping("/users")
    public ResponseEntity<?> listUsers(@AuthenticationPrincipal UserDetails userDetails) {
        User admin = getAuthenticatedAdmin(userDetails);
        if (admin == null) {
            return forbiddenOrUnauthorized(userDetails);
        }

        List<AdminDtos.AdminUserItem> users = adminService.listAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * List all bookings in the system
     * 
     * @param userDetails Authenticated user (must be ADMIN role)
     * @return List of all bookings sorted by date
     */
    @GetMapping("/bookings")
    public ResponseEntity<?> listBookings(@AuthenticationPrincipal UserDetails userDetails) {
        User admin = getAuthenticatedAdmin(userDetails);
        if (admin == null) {
            return forbiddenOrUnauthorized(userDetails);
        }

        List<AdminDtos.AdminBookingItem> bookings = adminService.listAllBookings();
        return ResponseEntity.ok(bookings);
    }

    // ========== Helper Methods ==========

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
}
