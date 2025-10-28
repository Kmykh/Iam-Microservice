package upc.edu.muusmart.iamservice.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response body returned after successful authentication operations.
 *
 * <p>Wraps a JWT token. Additional fields (such as expiration time or user info) can be
 * added here as needed.</p>
 */
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
}