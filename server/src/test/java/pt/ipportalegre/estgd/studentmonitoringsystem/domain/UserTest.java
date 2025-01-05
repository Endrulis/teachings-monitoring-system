package pt.ipportalegre.estgd.studentmonitoringsystem.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private MyUser myUser;
    private Role testRole;

    private CurricularUnit unit1;
    private CurricularUnit unit2;

    private Validator validator;

    @BeforeEach
    public void setUp() {
        // Set up the Validator to check constraints
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        // Initialize objects
        testRole = new Role();
        testRole.setName("TEACHER");

        unit1 = new CurricularUnit();
        unit1.setName("Mathematics");

        unit2 = new CurricularUnit();
        unit2.setName("Physics");

        // Create MyUser instance
        myUser = new MyUser();
        myUser.setUsername("teacher");
        myUser.setPassword("password123");
        myUser.setEmail("teacher@gmail.com");
        myUser.setRole(testRole);

        // Set CurricularUnits
        Set<CurricularUnit> units = Set.of(unit1, unit2);
        myUser.setCurricularUnits(units);
    }

    @Test
    public void testEmailConstraintValid() {
        myUser.setEmail("teacher@gmail.com");
        Set<ConstraintViolation<MyUser>> violations = validator.validate(myUser);
        assertTrue(violations.isEmpty(), "Should not have validation errors for valid email");
    }

    @Test
    public void testEmailConstraintInvalid() {
        myUser.setEmail("invalid-email");
        Set<ConstraintViolation<MyUser>> violations = validator.validate(myUser);
        assertFalse(violations.isEmpty(), "Should have validation errors for invalid email format");
    }

    @Test
    public void testNotNullUsername() {
        myUser.setUsername(null);
        Set<ConstraintViolation<MyUser>> violations = validator.validate(myUser);
        assertFalse(violations.isEmpty(), "Username should not be null");
    }

    @Test
    public void testUsernameLengthInvalid() {
        myUser.setUsername("us");
        Set<ConstraintViolation<MyUser>> violations = validator.validate(myUser);
        assertFalse(violations.isEmpty(), "Username should be invalid with length less than 3");
    }

    @Test
    public void testNotNullPassword() {
        myUser.setPassword(null);
        Set<ConstraintViolation<MyUser>> violations = validator.validate(myUser);
        assertFalse(violations.isEmpty(), "Password should not be null");
    }

    @Test
    public void testPasswordLengthInvalid() {
        myUser.setPassword("123");
        Set<ConstraintViolation<MyUser>> violations = validator.validate(myUser);
        assertFalse(violations.isEmpty(), "Password should meet length requirements");
    }


    @Test
    public void testNotNullRole() {
        myUser.setRole(null);
        Set<ConstraintViolation<MyUser>> violations = validator.validate(myUser);
        assertFalse(violations.isEmpty(), "Role should not be null");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("role")));
    }

    @Test
    public void testValidUser() {
        myUser.setEmail("teacher@gmail.com");
        myUser.setUsername("validUsername");
        myUser.setPassword("password123");

        Set<ConstraintViolation<MyUser>> violations = validator.validate(myUser);

        assertTrue(violations.isEmpty(), "User should be valid with correct constraints");
    }

    @Test
    public void testRoleRelationship() {
        Role newRole = new Role();
        newRole.setName("TEACHER");
        myUser.setRole(newRole);

        assertNotNull(myUser.getRole(), "Role should be properly set");
        assertEquals("TEACHER", myUser.getRole().getName(), "Role should be TEACHER");
    }

    @Test
    public void testCurricularUnitsRelationship() {
        Set<CurricularUnit> newUnits = Set.of(unit1);
        myUser.setCurricularUnits(newUnits);

        Set<CurricularUnit> units = myUser.getCurricularUnits();
        assertNotNull(units, "Curricular units should not be null");
        assertEquals(1, units.size(), "There should be one curricular unit");
        assertTrue(units.contains(unit1), "Curricular unit should be present");
    }

    @Test
    public void testCurricularUnitsEmpty() {
        myUser.setCurricularUnits(Set.of());

        Set<CurricularUnit> units = myUser.getCurricularUnits();
        assertTrue(units.isEmpty(), "Curricular units should be empty");
    }
}
