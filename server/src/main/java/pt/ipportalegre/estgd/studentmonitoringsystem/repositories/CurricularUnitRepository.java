package pt.ipportalegre.estgd.studentmonitoringsystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.CurricularUnit;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.MyUser;

import java.util.Set;

@Repository
public interface CurricularUnitRepository extends JpaRepository<CurricularUnit, Long> {
    Set<CurricularUnit> findCurricularUnitByTeacherId(Long teacherId);

    Set<CurricularUnit> findCurricularUnitByTeacherEmail(String teacherEmail);

    Set<CurricularUnit> findCurricularUnitByStudentsId(Long studentId);

    Set<CurricularUnit> findCurricularUnitByStudentsEmail(String studentsEmail);

    CurricularUnit findCurricularUnitByName(String curricularUnitName);

    @Query("SELECT cu.teacher FROM CurricularUnit cu WHERE cu.id = :curricularUnitId")
    MyUser findTeacherByCurricularUnitId(@Param("curricularUnitId") Long curricularUnitId);
}
