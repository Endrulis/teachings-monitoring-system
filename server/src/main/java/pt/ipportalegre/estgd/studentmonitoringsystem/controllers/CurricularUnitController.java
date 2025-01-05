package pt.ipportalegre.estgd.studentmonitoringsystem.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.CurricularUnitDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.CurricularUnitRequest;
import pt.ipportalegre.estgd.studentmonitoringsystem.security.CustomUserDetails;
import pt.ipportalegre.estgd.studentmonitoringsystem.security.aspect.TeacherAuthorization;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.CurricularUnitService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/curricular-units")
@RequiredArgsConstructor
public class CurricularUnitController {

    private final CurricularUnitService curricularUnitService;

    @PreAuthorize("hasAuthority(T(pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants).TEACHER.name())")
    @TeacherAuthorization
    @PostMapping("/create")
    public ResponseEntity<CurricularUnitDto> createCurricularUnit(@RequestBody CurricularUnitRequest request) {
        CurricularUnitDto curricularUnitDto = curricularUnitService.createCurricularUnit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(curricularUnitDto);
    }

    @PreAuthorize("hasAuthority(T(pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants).TEACHER.name()) " +
            "or hasAuthority(T(pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants).STUDENT.name())")
    @GetMapping("/user/email/{email}")
    public Set<CurricularUnitDto> getCurricularUnitsByUserEmail(@PathVariable String email, @AuthenticationPrincipal CustomUserDetails currentUser) {
        System.out.println("Authorization header received: " + currentUser.getEmail());
        System.out.println("Decoded email from path: " + email);
        if (!email.trim().equalsIgnoreCase(currentUser.getEmail().trim())) {
            throw new AccessDeniedException("You are not authorized to view these curricular units.");
        }

        // Check if the user has the "TEACHER" authority
        boolean isTeacher = currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleConstants.TEACHER.name()));
        // Check if the user has the "STUDENT" authority
        boolean isStudent = currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleConstants.STUDENT.name()));

        if (isTeacher) {
            return curricularUnitService.getAllCurricularUnitsByTeacherEmail(email);
        } else if(isStudent) {
            return curricularUnitService.getAllCurricularUnitsByStudentEmail(email);
        }

        throw new AccessDeniedException("You are not authorized to view these curricular units.");
    }

    @PreAuthorize("hasAuthority(T(pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants).TEACHER.name())")
    @GetMapping("/teacher/{teacherId}")
    public Set<CurricularUnitDto> getCurricularUnitsByTeacherId(@PathVariable Long teacherId, @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (!teacherId.equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to view these curricular units.");
        }
        return curricularUnitService.getAllCurricularUnitsByTeacherId(teacherId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Set<CurricularUnitDto>> getAllCurricularUnits() {
        Set<CurricularUnitDto> curricularUnits = curricularUnitService.getAllCurricularUnits();
        return ResponseEntity.ok(curricularUnits);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{curricularUnitId}/assign-students")
    public ResponseEntity<String> assignStudentsToCurricularUnit(@PathVariable Long curricularUnitId,
                                                                 @RequestBody List<Long> studentIds) {
        curricularUnitService.assignStudentsToCurricularUnit(curricularUnitId, studentIds);
        return ResponseEntity.ok("Students successfully assigned to the curricular unit.");
    }

    @PreAuthorize("hasAuthority(T(pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants).STUDENT.name())")
    @GetMapping("/student/{studentId}")
    public Set<CurricularUnitDto> getCurricularUnitsByStudentId(@PathVariable Long studentId, @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (!studentId.equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to view these curricular units.");
        }
        return curricularUnitService.getAllCurricularUnitsByStudentId(studentId);
    }
}
