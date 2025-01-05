package pt.ipportalegre.estgd.studentmonitoringsystem.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import pt.ipportalegre.estgd.studentmonitoringsystem.ApplicationTests;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.MyUser;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.Role;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.RoleDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.UserDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.repositories.RoleRepository;
import pt.ipportalegre.estgd.studentmonitoringsystem.repositories.UserRepository;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDto userDto;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_Should_CreateUser_When_ValidUserDtoProvided() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setUsername("John Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setPassword("password");

        Role mockRole = new Role();
        mockRole.setId(1L);
        mockRole.setName("STUDENT");

        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(mockRole));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // Act
        UserDto result = userService.createUser(userDto);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getUsername());
        assertEquals("john.doe@example.com", result.getEmail());
        assertNull(result.getPassword()); // Password should not be returned
        assertNotNull(result.getRole());
        assertEquals("STUDENT", result.getRole().getName());

        verify(userRepository).save(any(MyUser.class));
    }

    @Test
    void createUser_Should_ThrowException_When_EmailExists() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setEmail("existing.email@example.com");

        when(userRepository.existsByEmail("existing.email@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDto));
        verify(userRepository, never()).save(any(MyUser.class));
    }

    @Test
    void getUsersByCurricularUnitId_Should_ReturnUsers_When_UsersExistForCurricularUnitId() {
        // Arrange
        Long curricularUnitId = 1L;

        Role role = new Role();
        role.setId(1L);
        role.setName("STUDENT");

        MyUser user1 = new MyUser();
        user1.setId(1L);
        user1.setUsername("John Doe");
        user1.setEmail("john.doe@example.com");
        user1.setRole(role);

        MyUser user2 = new MyUser();
        user2.setId(2L);
        user2.setUsername("Jane Doe");
        user2.setEmail("jane.doe@example.com");
        user2.setRole(role);

        when(userRepository.findByCurricularUnitsId(curricularUnitId)).thenReturn(Set.of(user1, user2));

        // Act
        List<UserDto> result = userService.getUsersByCurricularUnitId(curricularUnitId);
        result.sort((u1, u2) -> u1.getUsername().compareTo(u2.getUsername())); // Sort by username

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("Jane Doe", result.get(0).getUsername());
        assertEquals("jane.doe@example.com", result.get(0).getEmail());
        assertNotNull(result.get(0).getRole());
        assertEquals("STUDENT", result.get(0).getRole().getName());

        assertEquals("John Doe", result.get(1).getUsername());
        assertEquals("john.doe@example.com", result.get(1).getEmail());
        assertNotNull(result.get(1).getRole());
        assertEquals("STUDENT", result.get(1).getRole().getName());
    }


    @Test
    void getUsersByCurricularUnitId_Should_ReturnEmptyList_When_NoUsersExistForCurricularUnitId() {
        // Arrange
        Long curricularUnitId = 999L;

        when(userRepository.findByCurricularUnitsId(curricularUnitId)).thenReturn(Set.of());

        // Act
        List<UserDto> result = userService.getUsersByCurricularUnitId(curricularUnitId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getUsersByCurricularUnitId_Should_ThrowException_When_CurricularUnitIdIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.getUsersByCurricularUnitId(null));
        verify(userRepository, never()).findByCurricularUnitsId(any());
    }

    @Test
    void getAllUsers_Should_ReturnAllUsers_When_UsersExist() {
        // Arrange
        Role role = new Role();
        role.setId(1L);
        role.setName("STUDENT");

        MyUser user1 = new MyUser();
        user1.setId(1L);
        user1.setUsername("John Doe");
        user1.setEmail("john.doe@example.com");
        user1.setPassword("password1");
        user1.setRole(role);

        MyUser user2 = new MyUser();
        user2.setId(2L);
        user2.setUsername("Jane Doe");
        user2.setEmail("jane.doe@example.com");
        user2.setPassword("password2");
        user2.setRole(role);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<UserDto> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        UserDto firstUser = result.get(0);
        assertNotNull(firstUser, "First user should not be null");
        assertEquals("John Doe", firstUser.getUsername());
        assertEquals("john.doe@example.com", firstUser.getEmail());
        assertNull(firstUser.getPassword()); // Password should not be returned
        assertNotNull(firstUser.getRole());
        assertEquals("STUDENT", firstUser.getRole().getName());

        UserDto secondUser = result.get(1);
        assertNotNull(secondUser, "Second user should not be null");
        assertEquals("Jane Doe", secondUser.getUsername());
        assertEquals("jane.doe@example.com", secondUser.getEmail());
        assertNull(secondUser.getPassword()); // Password should not be returned
        assertNotNull(secondUser.getRole());
        assertEquals("STUDENT", secondUser.getRole().getName());
    }


    @Test
    void getAllUsers_Should_ReturnEmptyList_When_NoUsersExist() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of());

        // Act
        List<UserDto> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getUserById_Should_ReturnUser_When_UserExists() {
        // Arrange
        Long userId = 1L;
        Role role = new Role();
        role.setId(1L);
        role.setName("STUDENT");

        MyUser user = new MyUser();
        user.setId(userId);
        user.setUsername("John Doe");
        user.setEmail("john.doe@example.com");
        user.setRole(role);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        MyUser result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("John Doe", result.getUsername());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("STUDENT", result.getRole().getName());

        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_Should_ThrowException_When_UserDoesNotExist() {
        // Arrange
        Long userId = 999L; // ID that does not exist
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(userId));

        verify(userRepository).findById(userId);
    }

}
