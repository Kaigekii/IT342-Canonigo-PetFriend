package edu.cit.canonigo.petfriend.features.sitters;

import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for sitter search and discovery
 * Allows pet owners to find and view sitter profiles
 */
@RestController
@RequestMapping("/api/sitters")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class SitterController {

    private final UserRepository userRepository;
    private final SitterService sitterService;

    public SitterController(UserRepository userRepository, SitterService sitterService) {
        this.userRepository = userRepository;
        this.sitterService = sitterService;
    }

    /**
     * Search for verified sitters with optional filtering
     * 
     * @param userDetails Authenticated user (must be PET_OWNER)
     * @param location Optional location filter
     * @param serviceType Optional service type filter
     * @return List of available sitters
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchSitters(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "location", required = false) String location,
            @RequestParam(name = "serviceType", required = false) String serviceType
    ) {
        User owner = getAuthenticatedOwner(userDetails);
        if (owner == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<SitterDtos.SitterSummaryResponse> results = sitterService.searchSitters(location, serviceType);
        return ResponseEntity.ok(results);
    }

    /**
     * Get detailed profile for a specific sitter
     * 
     * @param userDetails Authenticated user (must be PET_OWNER)
     * @param sitterId ID of the sitter to view
     * @return Detailed sitter profile with reviews and availability
     */
    @GetMapping("/{sitterId}")
    public ResponseEntity<?> getSitterDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID sitterId
    ) {
        User owner = getAuthenticatedOwner(userDetails);
        if (owner == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        SitterDtos.SitterDetailResponse details = sitterService.getSitterDetails(sitterId);
        if (details == null) {
            return ResponseEntity.status(404).body("Sitter not found");
        }

        return ResponseEntity.ok(details);
    }

    // ========== Helper Methods ==========

    private User getAuthenticatedOwner(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }

        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null || user.getRole() != UserRole.PET_OWNER) {
            return null;
        }

        return user;
    }
}
