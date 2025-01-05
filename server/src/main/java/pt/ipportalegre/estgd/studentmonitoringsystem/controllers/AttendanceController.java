package pt.ipportalegre.estgd.studentmonitoringsystem.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.Attendance;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.AttendanceDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.AttendenceRequest;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.AttendenceRequestForGettingStatus;
import pt.ipportalegre.estgd.studentmonitoringsystem.security.CustomUserDetails;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.AttendanceService;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.CurricularUnitService;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final CurricularUnitService curricularUnitService;

    @PreAuthorize("hasAuthority(T(pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants).TEACHER.name())")
    @PutMapping
    public ResponseEntity<List<AttendanceDto>> createAttendance(
            @RequestBody List<AttendenceRequest> attendances) {
        List<AttendanceDto> attendanceDtos = new ArrayList<>();
        for (AttendenceRequest attendance : attendances) {
            attendanceDtos.add(attendanceService.createAttendance(
                    attendance.getClassSessionId(),
                    attendance.getUserEmail(),
                    attendance.isAttended()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceDtos);
    }

    @PreAuthorize("hasAuthority(T(pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants).TEACHER.name())")
    @GetMapping("/class/{classId}")
    public Set<AttendanceDto> getAttendancesByClassForTeacher(@PathVariable Long classId) {
        Set<Attendance> attendanceSet = attendanceService.getAttendanceByClass(classId);

        return attendanceSet.stream()
                .map(attendance -> {
                    AttendanceDto dto = new AttendanceDto();
                    dto.setId(attendance.getId());
                    dto.setStudentId(attendance.getStudent().getId());
                    dto.setClassSessionId(attendance.getClassSession().getId());
                    dto.setAttended(attendance.isAttended());
                return dto;
                }).collect(Collectors.toSet());
    }

    @PreAuthorize("hasAuthority(T(pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants).STUDENT.name())")
    @PostMapping("/status")
    public ResponseEntity<?> getAttendanceStatus(@RequestBody AttendenceRequestForGettingStatus attendanceRequest) {

        Attendance attendance = attendanceService.getAttendanceStatus(attendanceRequest.getStudentEmail(), attendanceRequest.getClassSessionId());

        if (attendance != null) {
            return ResponseEntity.ok(Map.of(
                    "attended", attendance.isAttended()
            ));
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("attended", null);
            return ResponseEntity.ok(response);
        }
    }

    @PreAuthorize("hasAuthority(T(pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants).TEACHER.name()) " +
            "or hasAuthority(T(pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants).STUDENT.name())")
    @GetMapping("/user/{userEmail}/class/{classSessionId}")
    public AttendanceDto getAttendanceByUserAndClass(@PathVariable String userEmail, @PathVariable Long classSessionId)
    {
        return attendanceService.getAttendanceByUserAndClass(userEmail, classSessionId);
    }

    @PreAuthorize("hasAuthority(T(pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants).TEACHER.name())")
    @GetMapping("/user/{userId}/curricularUnit/{curricularUnitId}")
    public Set<AttendanceDto> getAttendanceByUserAndCurricularUnit(@PathVariable Long userId,
                                                                   @PathVariable Long curricularUnitId) {
        return attendanceService.getAttendanceByUserAndCurricularUnit(userId, curricularUnitId);
    }

}
