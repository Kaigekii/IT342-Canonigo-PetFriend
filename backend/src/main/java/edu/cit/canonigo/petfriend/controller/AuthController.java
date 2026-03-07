package edu.cit.canonigo.petfriend.controller;

import edu.cit.canonigo.petfriend.dto.AuthDtos;
import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import edu.cit.canonigo.petfriend.security.TokenProvider;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already registered");
        }

        // Only sitters need verification; owners don't need it
        Boolean isVerified = null;
        if (request.getRole() == UserRole.PET_SITTER) {
            isVerified = false; // Sitters start unverified, require admin approval
        } else if (request.getRole() == UserRole.ADMIN) {
            isVerified = true; // Admins are pre-verified
        }
        // PET_OWNER: isVerified remains null (not applicable)

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(
            request.getEmail(),
            hashedPassword,
            request.getFirstName(),
            request.getLastName(),
            request.getPhoneNumber(),
            request.getAddress(),
            request.getRole(),
            isVerified
        );
        userRepository.save(user);

        String token = tokenProvider.createToken(user);
        AuthDtos.AuthResponse response = new AuthDtos.AuthResponse(
            token,
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
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        
        // Update last login timestamp
        user.setLastLoginAt(java.time.Instant.now());
        userRepository.save(user);
        
        String token = tokenProvider.createToken(user);
        AuthDtos.AuthResponse response = new AuthDtos.AuthResponse(
            token,
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
    }
}
