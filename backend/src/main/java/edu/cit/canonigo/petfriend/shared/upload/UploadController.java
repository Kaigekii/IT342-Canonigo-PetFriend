package edu.cit.canonigo.petfriend.shared.upload;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import edu.cit.canonigo.petfriend.features.pets.PetDtos;
import edu.cit.canonigo.petfriend.features.pets.PetService;
import edu.cit.canonigo.petfriend.model.Pet;
import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.UserRepository;

@RestController
@RequestMapping("/api/uploads")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UploadController {

    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final PetService petService;

    public UploadController(FileStorageService fileStorageService, UserRepository userRepository, PetService petService) {
        this.fileStorageService = fileStorageService;
        this.userRepository = userRepository;
        this.petService = petService;
    }

    @PostMapping("/profile-photo")
    public ResponseEntity<?> uploadProfilePhoto(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file
    ) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            FileStorageService.StoredFile stored = fileStorageService.storeImage(file, "profiles");
            user.setProfilePhotoUrl(stored.getPublicUrl());
            userRepository.save(user);

            return ResponseEntity.ok(Map.of("url", stored.getPublicUrl()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping("/pets/{petId}/photo")
    public ResponseEntity<?> uploadPetPhoto(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID petId,
            @RequestParam("file") MultipartFile file
    ) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        if (user.getRole() != UserRole.PET_OWNER) {
            return ResponseEntity.status(403).body("Only pet owners can upload pet photos");
        }

        try {
            FileStorageService.StoredFile stored = fileStorageService.storeImage(file, "pets");
            Pet updated = petService.updatePetPhoto(user.getUserId(), petId, stored.getPublicUrl());
            return ResponseEntity.ok(PetDtos.PetResponse.from(updated));
        } catch (PetService.PetException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    private User getAuthenticatedUser(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }
}
