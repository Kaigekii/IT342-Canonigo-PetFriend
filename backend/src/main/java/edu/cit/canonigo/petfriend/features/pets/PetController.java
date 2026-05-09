package edu.cit.canonigo.petfriend.features.pets;

import edu.cit.canonigo.petfriend.model.Pet;
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
 * REST Controller for pet management.
 * Handles CRUD operations for pets belonging to the authenticated owner.
 */
@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class PetController {

    private final PetService petService;
    private final UserRepository userRepository;

    public PetController(PetService petService, UserRepository userRepository) {
        this.petService = petService;
        this.userRepository = userRepository;
    }

    /**
     * GET /api/pets - List all pets for authenticated owner.
     */
    @GetMapping
    public ResponseEntity<?> listMyPets(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        if (user.getRole() != UserRole.PET_OWNER) {
            return ResponseEntity.status(403).body("Only pet owners can view their pets");
        }

        List<Pet> pets = petService.getOwnerPets(user.getUserId());
        List<PetDtos.PetResponse> responses = pets.stream()
                .map(PetDtos.PetResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    /**
     * POST /api/pets - Create a new pet.
     */
    @PostMapping
    public ResponseEntity<?> createPet(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PetDtos.CreatePetRequest request
    ) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            Pet pet = petService.createPet(user.getUserId(), request);
            return ResponseEntity.ok(PetDtos.PetResponse.from(pet));
        } catch (PetService.PetException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * PUT /api/pets/{petId} - Update an existing pet.
     */
    @PutMapping("/{petId}")
    public ResponseEntity<?> updatePet(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID petId,
            @Valid @RequestBody PetDtos.CreatePetRequest request
    ) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            Pet pet = petService.updatePet(user.getUserId(), petId, request);
            return ResponseEntity.ok(PetDtos.PetResponse.from(pet));
        } catch (PetService.PetException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * DELETE /api/pets/{petId} - Delete a pet.
     */
    @DeleteMapping("/{petId}")
    public ResponseEntity<?> deletePet(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID petId
    ) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            petService.deletePet(user.getUserId(), petId);
            return ResponseEntity.ok("Pet deleted successfully");
        } catch (PetService.PetException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private User getAuthenticatedUser(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }
}
