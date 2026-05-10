package edu.cit.canonigo.petfriend.features.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.repository.UserRepository;

/**
 * REST Controller for user-related endpoints.
 * Handles fetching current user profile and other user info.
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * GET /api/user/me - Get current authenticated user's profile
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            // Extract user email from authentication principal
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();

            // Fetch user from database
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Return user info
            AuthDtos.AuthResponse response = new AuthDtos.AuthResponse(
                null, // Token not needed for this endpoint
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getRole(),
                user.getIsVerified()
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Failed to fetch user profile: " + ex.getMessage());
        }
    }

    /**
     * PUT /api/user/me - Update the current authenticated user's profile
     */
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateCurrentUser(Authentication authentication,
                                               @RequestBody AuthDtos.UpdateProfileRequest request) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (request.getFirstName() != null) {
                user.setFirstName(request.getFirstName().trim());
            }
            if (request.getLastName() != null) {
                user.setLastName(request.getLastName().trim());
            }
            if (request.getPhoneNumber() != null) {
                user.setPhoneNumber(request.getPhoneNumber().trim());
            }
            if (request.getAddress() != null) {
                user.setAddress(request.getAddress().trim());
            }
            if (request.getProfilePhotoUrl() != null) {
                user.setProfilePhotoUrl(request.getProfilePhotoUrl().trim());
            }

            boolean passwordFieldsProvided = request.getCurrentPassword() != null
                    || request.getNewPassword() != null
                    || request.getConfirmNewPassword() != null;
            if (passwordFieldsProvided) {
                if (request.getCurrentPassword() == null || request.getNewPassword() == null || request.getConfirmNewPassword() == null) {
                    return ResponseEntity.badRequest().body("Please complete all password fields");
                }
                if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
                    return ResponseEntity.badRequest().body("Current password is incorrect");
                }
                if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
                    return ResponseEntity.badRequest().body("New passwords do not match");
                }
                user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
            }

            userRepository.save(user);

            AuthDtos.AuthResponse response = new AuthDtos.AuthResponse(
                null,
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getRole(),
                user.getIsVerified()
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to update profile: " + ex.getMessage());
        }
    }
}
