package upc.edu.muusmart.iamservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upc.edu.muusmart.iamservice.model.AppUser;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on {@link AppUser} entities.
 *
 * <p>Spring Data JPA will automatically provide implementations for this
 * interface at runtime, allowing you to interact with the database without
 * writing boilerplate code. Additional query methods follow Spring Data
 * naming conventions to automatically generate the appropriate queries.</p>
 */
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}