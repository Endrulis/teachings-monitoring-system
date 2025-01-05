package pt.ipportalegre.estgd.studentmonitoringsystem.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.RoleDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.UserDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.UserService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController; // Controller under test

    @Mock
    private UserService userService; // Mocked service

    private List<UserDto> userDtoList;

    @BeforeEach
    public void setup() {
        // Prepare a mock list of users
        UserDto user1 = new UserDto(1L, "teacher", "teacher123", "teacher@gmail.com", new RoleDto(1L, "TEACHER"));
        UserDto user2 = new UserDto(2L, "student", "student123", "student@gmail.com", new RoleDto(2L, "STUDENT"));

        userDtoList = List.of(user1, user2);
    }

    @Test
    public void testGetUsersByCurricularUnitId() {
        // Given
        Long curricularUnitId = 1L;
        when(userService.getUsersByCurricularUnitId(curricularUnitId)).thenReturn(userDtoList);

        // When
        ResponseEntity<List<UserDto>> response = userController.getUsersByCurricularUnitId(curricularUnitId);

        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status should be 200 OK");
        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals(2, response.getBody().size(), "Response should contain 2 users");
        assertEquals("teacher", response.getBody().get(0).getUsername(), "First user's username should be 'teacher'");

        // Verify interactions
        verify(userService, times(1)).getUsersByCurricularUnitId(curricularUnitId);
    }

    @Test
    public void testGetUsersByCurricularUnitId_EmptyList() {
        // Given
        Long curricularUnitId = 2L;
        when(userService.getUsersByCurricularUnitId(curricularUnitId)).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<UserDto>> response = userController.getUsersByCurricularUnitId(curricularUnitId);

        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status should be 200 OK");
        assertTrue(response.getBody().isEmpty(), "Response body should be an empty list");

        // Verify interactions
        verify(userService, times(1)).getUsersByCurricularUnitId(curricularUnitId);
    }
}
