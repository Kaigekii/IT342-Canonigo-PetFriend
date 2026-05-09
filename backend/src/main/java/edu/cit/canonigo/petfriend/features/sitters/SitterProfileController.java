package edu.cit.canonigo.petfriend.features.sitters;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for sitter profile management
 * Allows sitters to manage their profile, services, and availability
 */
@RestController
@RequestMapping({"/api/sitter-profile", "/api/sitters/profile"})
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class SitterProfileController {

    private final UserRepository userRepository;
    private final SitterService sitterService;

    public SitterProfileController(UserRepository userRepository, SitterService sitterService) {
        this.userRepository = userRepository;
        this.sitterService = sitterService;
    }

    /**
     * Get the authenticated sitter's profile
     * 
     * @param userDetails Authenticated user (must be PET_SITTER)
     * @return Current sitter profile
     */
    @GetMapping
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User sitter = getAuthenticatedSitter(userDetails);
        if (sitter == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        SitterDtos.SitterProfileResponse profile = sitterService.getMyProfile(sitter);
        return ResponseEntity.ok(profile);
    }

    /**
     * Update the authenticated sitter's profile
     * 
     * @param userDetails Authenticated user (must be PET_SITTER)
     * @param request Updated profile information
     * @return Updated sitter profile
     */
    @PutMapping
    public ResponseEntity<?> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SitterDtos.UpsertSitterProfileRequest request
    ) {
        User sitter = getAuthenticatedSitter(userDetails);
        if (sitter == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            SitterDtos.SitterProfileResponse profile = sitterService.updateMyProfile(sitter, request);
            return ResponseEntity.ok(profile);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid profile payload");
        }
    }

    /**
     * Submit sitter profile for verification
     * 
     * @param userDetails Authenticated user (must be PET_SITTER)
     * @return Confirmation message
     */
    @PostMapping("/submit-verification")
    public ResponseEntity<?> submitVerification(@AuthenticationPrincipal UserDetails userDetails) {
        User sitter = getAuthenticatedSitter(userDetails);
        if (sitter == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        sitterService.submitVerification(sitter);
        return ResponseEntity.ok("Verification submitted");
    }

    // ========== Helper Methods ==========

    private User getAuthenticatedSitter(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }

        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null || user.getRole() != UserRole.PET_SITTER) {
            return null;
        }

        return user;
    }
}
