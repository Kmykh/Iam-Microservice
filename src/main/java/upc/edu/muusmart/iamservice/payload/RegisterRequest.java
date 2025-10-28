package upc.edu.muusmart.iamservice.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for user registration operations.
 *
 * <p>Includes a username, email address and password. Validation annotations ensure the
 * fields are provided and valid. The email field is annotated with {@link Email} to
 * enforce proper formatting.</p>
 */
@Data
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
}