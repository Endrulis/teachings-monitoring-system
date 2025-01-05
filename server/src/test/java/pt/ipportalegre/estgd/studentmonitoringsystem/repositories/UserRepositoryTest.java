package pt.ipportalegre.estgd.studentmonitoringsystem.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import pt.ipportalegre.estgd.studentmonitoringsystem.ApplicationTests;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.CurricularUnit;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.MyUser;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.Role;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CurricularUnitRepository curricularUnitRepository;

    private MyUser myUser;

    @BeforeEach
    public void setUp() {
        Role role = new Role();
        role.setName("TEACHER");

        role = roleRepository.save(role);

        CurricularUnit unit1 = new CurricularUnit();
        unit1.setName("Mathematics");

        CurricularUnit unit2 = new CurricularUnit();
        unit2.setName("Physics");

        curricularUnitRepository.saveAll(List.of(unit1, unit2));

        myUser = new MyUser();
        myUser.setUsername("teacher");
        myUser.setPassword("teacher123");
        myUser.setEmail("teacher@gmail.com");
        myUser.setRole(role);
        myUser.setCurricularUnits(Set.of(unit1, unit2));

        userRepository.save(myUser);
    }

    @Test
    @Transactional
    public void testFindByEmail() {
        MyUser foundUser = userRepository.findByEmail("teacher@gmail.com");
        assertNotNull(foundUser);
        assertEquals(myUser.getEmail(), foundUser.getEmail());
    }

    @Test
    @Transactional
    public void testExistsByEmail() {
        boolean exists = userRepository.existsByEmail("teacher@gmail.com");
        assertTrue(exists);

        exists = userRepository.existsByEmail("this_shouldnt_exist@gmail.com");
        assertFalse(exists);
    }

    @Test
    @Transactional
    public void testFindByCurricularUnitsId() {
        // Retrieve the ID of UC
        CurricularUnit unit1 = curricularUnitRepository.findCurricularUnitByName("Mathematics");
        Long curricularUnitId = unit1.getId();

        Set<MyUser> users = userRepository.findByCurricularUnitsId(curricularUnitId); // find all users by curriculum unit

        assertFalse(users.isEmpty());  // checks if set of users is not empty
        boolean userFound = users.stream()
                .anyMatch(user -> user.getEmail().equals(myUser.getEmail()));  // Check if testUser exists in the set
        assertTrue(userFound);  // asserts that the user is found
    }

    @Test
    @Transactional
    public void testFindByCurricularUnitsId_NoUsers() {
        Set<MyUser> users = userRepository.findByCurricularUnitsId(999999L);
        System.out.println("Users found: " + users);
        assertTrue(users.isEmpty());
    }
}
