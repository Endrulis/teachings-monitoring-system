package pt.ipportalegre.estgd.studentmonitoringsystem.services;

import org.springframework.stereotype.Service;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.Attendance;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.AttendanceDto;

import java.util.Set;

@Service
public interface AttendanceService {
    Attendance getAttendanceStatus(String studentEmail, Long classSessionId);

    AttendanceDto createAttendance(Long classSessionId, String userEmail, boolean attended);

    Set<Attendance> getAttendanceByClass(Long classId);

    AttendanceDto getAttendanceByUserAndClass(String userEmail, Long classSessionId);

    Set<AttendanceDto> getAttendanceByUserAndCurricularUnit(Long userId, Long curricularUnitId);

    Attendance saveAttendance(Attendance attendance);
}
