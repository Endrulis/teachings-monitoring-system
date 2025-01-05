package pt.ipportalegre.estgd.studentmonitoringsystem.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.CurricularUnit;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.MyUser;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.Role;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.RoleDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.UserDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.repositories.CurricularUnitRepository;
import pt.ipportalegre.estgd.studentmonitoringsystem.repositories.RoleRepository;
import pt.ipportalegre.estgd.studentmonitoringsystem.repositories.UserRepository;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CurricularUnitRepository curricularUnitRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("User with email " + userDto.getEmail() + " already exists.");
        }

        MyUser newUser = new MyUser();

        newUser.setUsername(userDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        newUser.setEmail(userDto.getEmail());

        Role userRole = roleRepository.findByName(RoleConstants.STUDENT.name())
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(RoleConstants.STUDENT.name());
                    return roleRepository.save(newRole);
                });

        newUser.setRole(userRole);

        userRepository.save(newUser);

        BeanUtils.copyProperties(newUser, userDto);

        RoleDto roleDto = new RoleDto(userRole.getId(), userRole.getName());
        userDto.setRole(roleDto);

        userDto.setPassword(null);

        return userDto;
    }

    @Override
    public List<UserDto> getUsersByCurricularUnitId(Long curricularUnitId){
        if (curricularUnitId == null) {
            IllegalArgumentException exception = new IllegalArgumentException("Curricular Unit ID cannot be null.");
            log.error("Exception occurred: {}", exception.getMessage(), exception);
            throw exception;
        }

        Set<MyUser> users = userRepository.findByCurricularUnitsId(curricularUnitId);

        return users.stream()
                .map(user -> new UserDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        new RoleDto(user.getRole().getId(), user.getRole().getName()) // Mapping Role to RoleDto
                ))
                .collect(Collectors.toList());

    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> {
                    if (user == null) {
                        throw new IllegalArgumentException("Encountered a null user in the database");
                    }
                    UserDto userDto = new UserDto();
                    BeanUtils.copyProperties(user, userDto);
                    if (user.getRole() != null) {
                        RoleDto roleDto = new RoleDto(user.getRole().getId(), user.getRole().getName());
                        userDto.setRole(roleDto);
                    }
                    userDto.setPassword(null);
                    return userDto;
                }).collect(Collectors.toList());
    }

    @Override
    public MyUser getUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User with id " + id + " does not exist."));
    }

    @Override
    public boolean isTeacherOfCurricularUnit(Long curricularUnitId) {
        MyUser loggedInUser = getLoggedInUser();

        if (loggedInUser == null) return false;

        Set<CurricularUnit> curricularUnits = curricularUnitRepository.findCurricularUnitByTeacherEmail(loggedInUser.getEmail());

        return loggedInUser.getRole().getName().equals(RoleConstants.TEACHER.name())
                && curricularUnits.stream()
                .anyMatch(unit -> unit.getId().equals(curricularUnitId));
    }

    @Override
    public boolean isStudentOfCurricularUnit(Long curricularUnitId) {
        MyUser loggedInUser = getLoggedInUser();

        if (loggedInUser == null) return false;

        Set<CurricularUnit> curricularUnits = curricularUnitRepository.findCurricularUnitByStudentsEmail(loggedInUser.getEmail());

        return loggedInUser.getRole().getName().equals(RoleConstants.STUDENT.name())
                && curricularUnits.stream()
                .anyMatch(unit -> unit.getId().equals(curricularUnitId));
    }

    // Helper method to retrieve the logged-in user
    private MyUser getLoggedInUser() {
        // Retrieve the authenticated user's email
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Fetch the user from the database
        MyUser loggedInUser = userRepository.findByEmail(username);

        if (loggedInUser == null) {
            // Log the error and throw a RuntimeException
            log.error("User with email {} not found", username);
            return null;
        }

        return loggedInUser;
    }
}
