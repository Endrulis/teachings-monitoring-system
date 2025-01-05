package pt.ipportalegre.estgd.studentmonitoringsystem.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.ClassSession;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.ClassDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.ClassRequest;
import pt.ipportalegre.estgd.studentmonitoringsystem.security.CustomUserDetails;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.ClassService;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.CurricularUnitService;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.UserService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    @PreAuthorize("hasAuthority(T(pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants).TEACHER.name())")
    @PostMapping
    public ResponseEntity<ClassDto> createClass(@RequestBody ClassRequest request, @AuthenticationPrincipal CustomUserDetails currentUser) {

        if (!request.getTeacherEmail().trim().equalsIgnoreCase(currentUser.getEmail().trim())) {
            throw new AccessDeniedException("You can only create classes which belongs to you.");
        }

        ClassDto classSession = classService.createClass(request);
        return new ResponseEntity<>(classSession, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority(T(pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants).TEACHER.name()) " +
            "or hasAuthority(T(pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants).STUDENT.name())")
    @GetMapping("/unit/{unitId}")
    public ResponseEntity<Set<ClassDto>> getAllClassesByCurricularUnit(@PathVariable Long unitId) {
        Set<ClassDto> classes = classService.getClassesByCurricularUnit(unitId);
        return ResponseEntity.ok(classes);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ClassDto>> getAllClasses() {
        List<ClassDto> classes = classService.getAllClasses();
        return ResponseEntity.ok(classes);
    }

}
