package pt.ipportalegre.estgd.studentmonitoringsystem.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "spring-api";
    public static final String TOKEN_AUDIENCE = "react-app";

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    public String generateJwtToken(Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        //String username = authentication.getName();
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        System.out.println("Roles assigned to JWT: " + roles);

        return Jwts.builder()
                .setHeaderParam("typ", TOKEN_TYPE)
                .signWith(SignatureAlgorithm.HS512, jwtSecret.getBytes())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .setIssuedAt(new Date())
                .setId(UUID.randomUUID().toString())
                .setIssuer(TOKEN_ISSUER)
                .setAudience(TOKEN_AUDIENCE)
                .setSubject(userDetails.getUsername())
                .claim("roles", roles)
                .compact();
    }

    public Optional<Jws<Claims>> validateTokenAndGetJws(String authToken) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .setSigningKey(jwtSecret.getBytes())
                    .build()
                    .parseClaimsJws(authToken);
            return Optional.of(jws);
        } catch (ExpiredJwtException exception) {
            // Handle expired token
            System.err.println("JWT expired: " + exception.getMessage());
        } catch (JwtException exception) {
            // Handle other JWT exceptions
            System.err.println("Invalid JWT: " + exception.getMessage());
        }
        return Optional.empty();
    }
}
