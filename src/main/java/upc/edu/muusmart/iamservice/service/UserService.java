package upc.edu.muusmart.iamservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import upc.edu.muusmart.iamservice.model.AppUser;
import upc.edu.muusmart.iamservice.model.Role;
import upc.edu.muusmart.iamservice.repository.UserRepository;

import java.util.Collections;
import java.util.HashSet;

/**
 * Service that encapsulates user-related business logic.
 *
 * <p>This service provides methods for registering new users and
 * checking for existing users by username or email. Passwords are
 * encoded using the configured {@link PasswordEncoder} before being
 * persisted to the database. The default role assigned to new users
 * is {@code ROLE_USER}. Additional user-related business rules can be
 * centralized in this class as the application evolves.</p>
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user with the provided username, email and password.
     *
     * @param username the unique username of the new user
     * @param email    the unique email address of the new user
     * @param password the raw password; it will be encoded before storage
     * @return the persisted {@link AppUser} entity
     */
    public AppUser registerUser(String username, String email, String password) {
        AppUser user = AppUser.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .roles(new HashSet<>(Collections.singleton(Role.ROLE_USER)))
                .build();
        return userRepository.save(user);
    }

    /**
     * Checks if a user exists by username.
     *
     * @param username the username to check
     * @return true if a user with the given username exists
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Checks if a user exists by email.
     *
     * @param email the email to check
     * @return true if a user with the given email exists
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}