package pt.ipportalegre.estgd.studentmonitoringsystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.ClassSession;

import java.util.List;
import java.util.Set;

@Repository
public interface ClassRepository extends JpaRepository<ClassSession,Long> {
    Set<ClassSession> findByCurricularUnitId(Long id);
}
