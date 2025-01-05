package pt.ipportalegre.estgd.studentmonitoringsystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.MyUser;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<MyUser, Long> {

    MyUser findByEmail(String email);

    boolean existsByEmail(String email);

    Set<MyUser> findByCurricularUnitsId(@Param("id") Long id);

}
