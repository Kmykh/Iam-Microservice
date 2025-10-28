package upc.edu.muusmart.iamservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import upc.edu.muusmart.iamservice.model.AppUser;
import upc.edu.muusmart.iamservice.payload.AuthResponse;
import upc.edu.muusmart.iamservice.payload.LoginRequest;
import upc.edu.muusmart.iamservice.payload.RegisterRequest;
import upc.edu.muusmart.iamservice.security.JwtUtil;
import upc.edu.muusmart.iamservice.service.CustomUserDetailsService;
import upc.edu.muusmart.iamservice.service.UserService;

/**
 * REST controller that exposes authentication endpoints.
 *
 * <p>The endpoints under {@code /auth} allow clients to register new users and
 * authenticate existing ones. Upon successful registration or login, a JWT
 * token is returned to the client, which can be used to authorize subsequent
 * requests.</p>
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Registers a new user with the provided credentials.
     *
     * @param request the registration request payload
     * @return a response containing a JWT or an error message
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }
        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already registered");
        }
        AppUser user = userService.registerUser(request.getUsername(), request.getEmail(), request.getPassword());
        // After registration, load the user to build a UserDetails instance for token generation
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    /**
     * Authenticates a user with the provided credentials.
     *
     * @param request the login request payload
     * @return a response containing a JWT or an error message
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}