package pt.ipportalegre.estgd.studentmonitoringsystem.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.Role;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.*;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.AuthService;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.RoleService;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.UserService;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private final AuthService authService;

    @PostMapping("/authenticate")
    public AuthResponse login(@Valid @RequestBody LoginRequest loginRequest){
        String token = authService.authenticateAndGetToken(loginRequest.getEmail(), loginRequest.getPassword());
        return new AuthResponse(token);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest){
        UserDto newUser = new UserDto(signUpRequest.getUsername(), signUpRequest.getPassword(), signUpRequest.getEmail());

        userService.createUser(newUser);

        String token = authService.authenticateAndGetToken(signUpRequest.getEmail(), signUpRequest.getPassword());

        return new ResponseEntity<>(new AuthResponse(token), HttpStatus.CREATED);
    }
}
