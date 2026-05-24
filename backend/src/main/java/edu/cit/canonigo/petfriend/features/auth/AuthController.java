package edu.cit.canonigo.petfriend.features.auth;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.client.RestTemplate;

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

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-role-key}")
    private String supabaseServiceRoleKey;

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

    /**
     * POST /api/auth/google - Authenticate user via Google OAuth using Supabase access token.
     */
    @PostMapping("/google")
    public ResponseEntity<?> googleAuth(@RequestBody AuthDtos.GoogleAuthRequest request) {
        try {
            // Verify the Supabase token by calling Supabase's /auth/v1/user endpoint
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + request.getToken());
            headers.set("apikey", supabaseServiceRoleKey);

            HttpEntity<String> entity = new HttpEntity<>("", headers);
            
            ResponseEntity<Map> supabaseResponse = restTemplate.exchange(
                supabaseUrl + "/auth/v1/user",
                HttpMethod.GET,
                entity,
                Map.class
            );

            if (!supabaseResponse.getStatusCode().is2xxSuccessful() || supabaseResponse.getBody() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google token");
            }

            Map<String, Object> userData = supabaseResponse.getBody();
            String email = (String) userData.get("email");
            String supabaseId = (String) userData.get("id");

            Map<String, Object> userMetadata = (Map<String, Object>) userData.get("user_metadata");
            String fullName = userMetadata != null ? (String) userMetadata.get("full_name") : "";
            
            String firstName = "";
            String lastName = "";
            if (fullName != null && !fullName.trim().isEmpty()) {
                String[] parts = fullName.split(" ", 2);
                firstName = parts[0];
                lastName = parts.length > 1 ? parts[1] : "";
            } else {
                firstName = "Google";
                lastName = "User";
            }

            // Check if user exists in our database
            Optional<User> existingUserOpt = userRepository.findByEmail(email);

            User user;
            if (existingUserOpt.isPresent()) {
                user = existingUserOpt.get();
                
                // If a role was provided (meaning they came from a register page)
                // we should ensure it matches their existing role to prevent confusion.
                if (request.getRole() != null && !request.getRole().equals(user.getRole().name())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Account already exists with a different role. Please login instead.");
                }

                // Update their Supabase ID if not already set (e.g. they registered normally, then logged in with Google)
                if (user.getSupabaseId() == null) {
                    user.setSupabaseId(supabaseId);
                }
            } else {
                // New user - they must provide a role (meaning they must come from a register page)
                if (request.getRole() == null || request.getRole().trim().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("No account found. Please create an account first.");
                }

                UserRole role;
                try {
                    role = UserRole.valueOf(request.getRole());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body("Invalid role");
                }

                Boolean isVerified = null;
                if (role == UserRole.PET_SITTER) {
                    isVerified = false; // Sitters require admin verification
                } else if (role == UserRole.ADMIN) {
                    isVerified = true; // Admins are auto-verified
                }

                // Generate a random password for Google-linked accounts
                String randomPassword = passwordEncoder.encode(UUID.randomUUID().toString());
                
                user = new User(
                    email,
                    randomPassword,
                    firstName,
                    lastName,
                    null,
                    null,
                    role,
                    isVerified
                );
                user.setSupabaseId(supabaseId);
            }

            // Update last login
            user.setLastLoginAt(Instant.now());
            userRepository.save(user);

            // Generate token and return response
            String jwtToken = tokenProvider.createToken(user);
            AuthDtos.AuthResponse authResponse = new AuthDtos.AuthResponse(
                jwtToken,
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getRole(),
                user.getIsVerified()
            );

            return ResponseEntity.ok(authResponse);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to process Google authentication: " + ex.getMessage());
        }
    }
}
