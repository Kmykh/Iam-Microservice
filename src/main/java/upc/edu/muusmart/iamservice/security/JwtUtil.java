package upc.edu.muusmart.iamservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

/**
 * Utility component for generating and validating JSON Web Tokens (JWTs).
 *
 * <p>This class uses the JJWT library to create and parse tokens. A secret key and an expiration
 * time are defined as constants for simplicity; in a production system you should externalize
 * these values into configuration (e.g. environment variables or application properties).</p>
 */
@Component
public class JwtUtil {

    // In a real application these values should come from configuration and not be hard-coded.
    private final String jwtSecret = "ReplaceThisSecretWithAStrongKeyForProduction";
    private final long jwtExpirationMs = 60 * 60 * 1000; // 1 hour in milliseconds

    /**
     * Extracts the username (subject) from the JWT.
     *
     * @param token the JWT token
     * @return the username contained in the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the JWT using a resolver function.
     *
     * @param token          the JWT token
     * @param claimsResolver function to extract the desired claim
     * @param <T>            the type of the claim being returned
     * @return the extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Validates the JWT against the given user details.
     *
     * @param token       the JWT token
     * @param userDetails the authenticated user details
     * @return true if the token is valid and matches the user
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Checks whether the token has expired.
     *
     * @param token the JWT token
     * @return true if the token's expiration date is before the current time
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the JWT.
     *
     * @param token the JWT token
     * @return the expiration {@link Date}
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generates a new JWT for the specified user details.
     *
     * @param userDetails the authenticated user details
     * @return a signed JWT as a {@code String}
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes())
                .compact();
    }
}