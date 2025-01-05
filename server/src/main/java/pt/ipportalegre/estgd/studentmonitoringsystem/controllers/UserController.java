package pt.ipportalegre.estgd.studentmonitoringsystem.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.SignUpRequest;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.UserDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.security.aspect.TeacherAuthorization;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @TeacherAuthorization
    @GetMapping("/curricular-unit/{curricularUnitId}")
    public ResponseEntity<List<UserDto>> getUsersByCurricularUnitId(@PathVariable Long curricularUnitId) {
        List<UserDto> users = userService.getUsersByCurricularUnitId(curricularUnitId);
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody SignUpRequest signUpRequest) {
        UserDto userDto = new UserDto(signUpRequest.getUsername(), signUpRequest.getPassword(), signUpRequest.getEmail());
        try {
            UserDto newUser = userService.createUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
