package upc.edu.muusmart.iamservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List; // ✅ IMPORTANTE: para List.of(role)
import java.util.function.Function;

@Component
public class JwtUtil {

    // En producción, usa una clave >= 32 bytes y muévela a configuración.
    private final String jwtSecret = "ReplaceThisSecretWithAStrongKeyForProduction";
    private final long jwtExpirationMs = 60 * 60 * 1000; // 1 hora
    private final SecretKey signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey) // evita Invalid Signature al usar misma clave/algoritmo
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Genera el token JWT con los roles incluidos en el claim "roles".
     * Ejemplo del payload generado:
     * {
     *   "sub": "Maycol",
     *   "roles": ["ROLE_USER"],
     *   "iat": 1730306000,
     *   "exp": 1730309600
     * }
     */
    public String generateToken(UserDetails userDetails) {
        // Extraemos el rol principal del usuario
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // Ej: ROLE_USER, ROLE_ADMIN
                .findFirst()
                .orElse("ROLE_USER");

        // Creamos el token con el claim plural "roles"
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", List.of(role)) // 👈 ahora plural y estándar
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
