package pt.ipportalegre.estgd.studentmonitoringsystem.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.ClassSession;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.CurricularUnit;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.MyUser;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.ClassDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.ClassRequest;
import pt.ipportalegre.estgd.studentmonitoringsystem.repositories.ClassRepository;
import pt.ipportalegre.estgd.studentmonitoringsystem.repositories.CurricularUnitRepository;
import pt.ipportalegre.estgd.studentmonitoringsystem.repositories.UserRepository;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.ClassService;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final CurricularUnitRepository curricularUnitRepository;
    private final ClassRepository classRepository;
    private final UserService userService;

    @Override
    public List<ClassDto> getAllClasses() {
        List<ClassSession> classSessions = classRepository.findAll();
        return classSessions.stream()
                .map(this::mapToClassSessionDto)
                .collect(Collectors.toList());
    }

    @Override
    public Set<ClassDto> getClassesByCurricularUnit(Long unitId){
        // Check if the logged-in user is a teacher or a student for the given curricular unit
        boolean isTeacher = userService.isTeacherOfCurricularUnit(unitId);
        boolean isStudent = userService.isStudentOfCurricularUnit(unitId);

        // If the user is neither a teacher nor a student for the given curricular unit, return an empty set
        if (!isTeacher && !isStudent) {
            return Collections.emptySet();
        }

        Set<ClassSession> classSessions = classRepository.findByCurricularUnitId(unitId);
        return classSessions.stream()
                .map(this::mapToClassSessionDto)
                .collect(Collectors.toSet());
    }

    @Override
    public ClassDto createClass(ClassRequest request) {
        CurricularUnit curricularUnit = curricularUnitRepository.findById(request.getCurricularUnitId())
                .orElseThrow(() -> new RuntimeException("Curricular Unit not found"));

        ClassSession classSession = new ClassSession();
        classSession.setClassName(request.getClassName());
        classSession.setDate(request.getDate());
        classSession.setCurricularUnit(curricularUnit);

        ClassSession savedClassSession = classRepository.save(classSession);

        return mapToClassSessionDto(savedClassSession);
    }

    @Override
    public ClassSession saveClass(ClassSession newClass){
        return classRepository.save(newClass);
    }

    private ClassDto mapToClassSessionDto(ClassSession classSession) {
        ClassDto dto = new ClassDto();
        dto.setId(classSession.getId());
        dto.setClassName(classSession.getClassName());
        dto.setDate(classSession.getDate());
        dto.setCurricularUnitId(classSession.getCurricularUnit().getId());
        return dto;
    }

}
