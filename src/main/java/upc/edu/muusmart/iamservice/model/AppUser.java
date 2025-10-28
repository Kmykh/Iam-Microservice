package upc.edu.muusmart.iamservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Entity representing a user in the IAM system.
 *
 * <p>This class maps to a relational database table named {@code users} via
 * JPA annotations. Each user has a unique username and email address, an
 * encrypted password, and a set of roles. The roles are stored in a
 * separate collection table to support a many-to-many style relationship
 * without introducing a dedicated role entity. Fetch type is set to
 * {@code EAGER} to ensure that roles are loaded alongside the user when
 * needed for authentication.</p>
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles;
}