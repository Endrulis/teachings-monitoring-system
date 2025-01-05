package pt.ipportalegre.estgd.studentmonitoringsystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.Attendance;

import java.util.Set;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Set<Attendance> findAttendanceByStudentIdAndClassSession_CurricularUnit_Id(Long userId, Long curricularUnitId);
    Attendance findAttendanceByStudentIdAndClassSessionId(Long userId, Long classSessionId);
    Set<Attendance> findAttendanceByClassSession_Id(Long classSessionId);
    Set<Attendance> findAttendanceByStudentIdAndClassSession_Id(Long studentId, Long classSessionId);
}
