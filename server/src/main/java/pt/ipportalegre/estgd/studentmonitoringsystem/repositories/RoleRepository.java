package pt.ipportalegre.estgd.studentmonitoringsystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    boolean existsByName(String name);
}
