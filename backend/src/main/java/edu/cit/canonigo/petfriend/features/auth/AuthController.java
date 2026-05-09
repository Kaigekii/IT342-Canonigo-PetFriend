package edu.cit.canonigo.petfriend.features.auth;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.canonigo.petfriend.model.User;
import edu.cit.canonigo.petfriend.model.UserRole;
import edu.cit.canonigo.petfriend.repository.UserRepository;
import edu.cit.canonigo.petfriend.security.TokenProvider;
import jakarta.validation.Valid;

/**
 * REST Controller for authentication operations.
 * Handles user registration and login.
 */
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

    /**
     * POST /api/auth/register - Register a new user.
     * - PET_OWNER: Created as verified immediately (no approval needed)
     * - PET_SITTER: Created as unverified (requires admin approval)
     * - ADMIN: Created as verified
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already registered");
        }

        // Determine verification status based on role
        Boolean isVerified = null;
        if (request.getRole() == UserRole.PET_SITTER) {
            isVerified = false; // Sitters require admin verification
        } else if (request.getRole() == UserRole.ADMIN) {
            isVerified = true; // Admins are auto-verified
        }
        // PET_OWNER: isVerified remains null (not applicable)

        // Hash password and create user
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

        // Generate token and return response
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

    /**
     * POST /api/auth/login - Authenticate user and return JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        // Authenticate credentials
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Email or password is incorrect");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Authentication failed");
        }

        // Extract user details and fetch from database
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update last login timestamp
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        // Generate token and return response
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
