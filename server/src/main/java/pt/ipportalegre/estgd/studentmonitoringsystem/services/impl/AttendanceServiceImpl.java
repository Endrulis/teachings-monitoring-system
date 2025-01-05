package pt.ipportalegre.estgd.studentmonitoringsystem.services.impl;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.Attendance;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.ClassSession;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.MyUser;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.AttendanceDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.repositories.AttendanceRepository;
import pt.ipportalegre.estgd.studentmonitoringsystem.repositories.ClassRepository;
import pt.ipportalegre.estgd.studentmonitoringsystem.repositories.UserRepository;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.AttendanceService;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.CurricularUnitService;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final ClassRepository classRepository;
    private final UserRepository userRepository;
    private final CurricularUnitService curricularUnitService;

    @Override
    public Attendance getAttendanceStatus(String studentEmail, Long classSessionId) {
        MyUser user = userRepository.findByEmail(studentEmail);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + studentEmail);
        }

        Set<Attendance> attendances = attendanceRepository.findAttendanceByStudentIdAndClassSession_Id(user.getId(), classSessionId);
        return attendances.stream().findFirst().orElse(null);
    }

    @Override
    public AttendanceDto createAttendance(Long classSessionId, String userEmail, boolean attended){
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        ClassSession classSession = classRepository.findById(classSessionId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        MyUser user = userRepository.findByEmail(userEmail);
        if (user == null) throw new RuntimeException("User not found with email: " + userEmail);

        boolean isTeacher = curricularUnitService.isTeacherOfCurricularUnit(classSession.getCurricularUnit().getId(), currentUsername);

        if (!isTeacher) {
            log.error("User with username {} is not the teacher of the curricular unit with ID {}", currentUsername, classSession.getCurricularUnit().getId());
            throw new RuntimeException("Current user is not the teacher of the curricular unit.");
        }

        // Check if the attendance already exists for this student and class session
        Set<Attendance> existingAttendance = attendanceRepository.findAttendanceByStudentIdAndClassSession_Id(user.getId(), classSessionId);

        if (!existingAttendance.isEmpty()) {
            // If attendance exists, update the 'attended' field
            Attendance existing = existingAttendance.iterator().next(); // Assuming only one attendance record per student and class session
            existing.setAttended(attended);
            Attendance updatedAttendance = attendanceRepository.save(existing);
            return convertToDto(updatedAttendance);
        } else {
            // If attendance does not exist, create a new attendance record
            Attendance attendance = new Attendance();
            attendance.setClassSession(classSession);
            attendance.setStudent(user);
            attendance.setAttended(attended);
            Attendance savedAttendance = attendanceRepository.save(attendance);
            return convertToDto(savedAttendance);
        }
    }

    @Override
    public Set<Attendance> getAttendanceByClass(Long classId){
        Set<Attendance> attendances = attendanceRepository.findAttendanceByClassSession_Id(classId);
        Attendance attendance = attendances.iterator().next();
        return attendanceRepository.findAttendanceByClassSession_Id(classId);
    }

    @Override
    public AttendanceDto getAttendanceByUserAndClass(String userEmail, Long classSessionId){

        MyUser user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + userEmail);
        }

        Attendance attendance = attendanceRepository.findAttendanceByStudentIdAndClassSessionId(user.getId(), classSessionId);
        return new AttendanceDto(
                attendance.getId(),
                attendance.isAttended(),
                attendance.getStudent().getId(),
                attendance.getClassSession().getId()
        );
    }

    @Override
    public Set<AttendanceDto> getAttendanceByUserAndCurricularUnit(Long userId, Long curricularUnitId){
        Set<Attendance> attendances = attendanceRepository.findAttendanceByStudentIdAndClassSession_CurricularUnit_Id(userId, curricularUnitId);
        return attendances.stream()
                .map(attendance -> new AttendanceDto(
                        attendance.getId(),
                        attendance.isAttended(),
                        attendance.getStudent().getId(),
                        attendance.getClassSession().getId()
                ))
                .collect(Collectors.toSet());
    }

    @Override
    public Attendance saveAttendance(Attendance attendance){
        return attendanceRepository.save(attendance);
    }

    private AttendanceDto convertToDto(Attendance attendance) {
        AttendanceDto attendanceDTO = new AttendanceDto();
        attendanceDTO.setId(attendance.getId());
        attendanceDTO.setAttended(attendance.isAttended());
        attendanceDTO.setStudentId(attendance.getStudent().getId());
        attendanceDTO.setClassSessionId(attendance.getClassSession().getId());

        return attendanceDTO;
    }
}
