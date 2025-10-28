package upc.edu.muusmart.iamservice.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for login operations.
 *
 * <p>Contains a username and password. Validation annotations ensure that both
 * fields are provided and not empty when a request is made.</p>
 */
@Data
@NoArgsConstructor
public class LoginRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}