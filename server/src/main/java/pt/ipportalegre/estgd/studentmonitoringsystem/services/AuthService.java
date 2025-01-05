package pt.ipportalegre.estgd.studentmonitoringsystem.services;

import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    String authenticateAndGetToken(String username, String password);
}
