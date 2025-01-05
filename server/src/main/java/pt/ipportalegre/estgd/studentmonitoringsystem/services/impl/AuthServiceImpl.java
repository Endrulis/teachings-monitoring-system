package pt.ipportalegre.estgd.studentmonitoringsystem.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pt.ipportalegre.estgd.studentmonitoringsystem.security.JwtUtil;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.AuthService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public String authenticateAndGetToken(String username, String password) {
        System.out.println("Authenticating user: " + username);
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            System.out.println("Authentication successful for user: " + username);
            return jwtUtil.generateJwtToken(authentication);
        } catch (Exception e) {
            System.err.println("Authentication failed: " + e.getMessage());
            throw e; // Ensure the error propagates for debugging
        }
    }
}
