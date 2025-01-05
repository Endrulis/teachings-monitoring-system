package pt.ipportalegre.estgd.studentmonitoringsystem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class ApplicationTests {

    @Autowired
    private Environment environment;

    @Test
    void contextLoads() {
        // Verify the active profile
        String[] activeProfiles = environment.getActiveProfiles();
        assertThat(activeProfiles).containsExactly("test");
    }

}
