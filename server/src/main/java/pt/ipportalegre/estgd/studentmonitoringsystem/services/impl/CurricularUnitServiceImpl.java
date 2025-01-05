package pt.ipportalegre.estgd.studentmonitoringsystem.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.CurricularUnit;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.MyUser;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.*;
import pt.ipportalegre.estgd.studentmonitoringsystem.repositories.CurricularUnitRepository;
import pt.ipportalegre.estgd.studentmonitoringsystem.repositories.UserRepository;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.CurricularUnitService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurricularUnitServiceImpl implements CurricularUnitService {

    private final CurricularUnitRepository curricularUnitRepository;
    private final UserRepository userRepository;

    @Override
    public CurricularUnitDto createCurricularUnit(CurricularUnitRequest request) {
        MyUser teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        CurricularUnit curricularUnit = new CurricularUnit();
        curricularUnit.setName(request.getName());
        curricularUnit.setTeacher(teacher);

        CurricularUnit savedCurricularUnit = saveCurricularUnit(curricularUnit);

        return mapToCurricularUnitDto(savedCurricularUnit);
    }

    @Override
    public Set<CurricularUnitDto> getAllCurricularUnitsByTeacherEmail(String teacherEmail){
        Set<CurricularUnit> curricularUnits = curricularUnitRepository.findCurricularUnitByTeacherEmail(teacherEmail);

        return curricularUnits.stream()
                .map(this::mapToCurricularUnitDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<CurricularUnitDto> getAllCurricularUnitsByStudentEmail(String studentEmail) {
        // Get curricular units associated with the student
        Set<CurricularUnit> units = curricularUnitRepository.findCurricularUnitByStudentsEmail(studentEmail);

        System.out.println("Found " + units.size() + " curricular units for student with ID: " + studentEmail);

        // Map to DTOs
        return units.stream()
                .map(this::mapToCurricularUnitDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<CurricularUnitDto> getAllCurricularUnits() {
        List<CurricularUnit> curricularUnits = curricularUnitRepository.findAll();

        return curricularUnits.stream()
                .map(this::mapToCurricularUnitDto)
                .collect(Collectors.toSet());
    }

    @Override
    public void assignStudentsToCurricularUnit(Long curricularUnitId, List<Long> studentIds) {
        // Find the CurricularUnit by its ID
        CurricularUnit curricularUnit = curricularUnitRepository.findById(curricularUnitId)
                .orElseThrow(() -> new RuntimeException("Curricular Unit not found"));

        // Find the students by their IDs
        Set<MyUser> students = new HashSet<>();
        for (Long studentId : studentIds) {
            MyUser student = userRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

            // Ensure bidirectional relationship is maintained
            student.getCurricularUnits().add(curricularUnit);
            students.add(student);
        }

        System.out.println("Adding " + students.size() + " students to CurricularUnit ID: " + curricularUnitId);

        curricularUnit.getStudents().addAll(students);

        // Save the updated CurricularUnit
        curricularUnitRepository.save(curricularUnit);
        userRepository.saveAll(students);

        System.out.println("CurricularUnit ID: " + curricularUnitId + " now has " + curricularUnit.getStudents().size() + " students.");
    }

    @Override
    public Set<CurricularUnitDto> getAllCurricularUnitsByStudentId(Long studentId) {
        // Get curricular units associated with the student
        Set<CurricularUnit> units = curricularUnitRepository.findCurricularUnitByStudentsId(studentId);

        System.out.println("Found " + units.size() + " curricular units for student with ID: " + studentId);

        // Map to DTOs
        return units.stream()
                .map(this::mapToCurricularUnitDto)
                .collect(Collectors.toSet());
    }



    @Override
    public Set<CurricularUnitDto> getAllCurricularUnitsByTeacherId(Long teacherId) {
        Set<CurricularUnit> curricularUnits = curricularUnitRepository.findCurricularUnitByTeacherId(teacherId);

        return curricularUnits.stream()
                .map(this::mapToCurricularUnitDto)
                .collect(Collectors.toSet());
    }

    @Override
    public CurricularUnit saveCurricularUnit(CurricularUnit curricularUnit) {
        return curricularUnitRepository.save(curricularUnit);
    }

    @Override
    public boolean isTeacherOfCurricularUnit(Long curricularUnitId, String username) {
        MyUser teacher = curricularUnitRepository.findTeacherByCurricularUnitId(curricularUnitId);
        // Check if the teacher's email matches the current user's username (email)
        return teacher.getEmail().equals(username);
    }

    // Helper method to map a CurricularUnit to CurricularUnitDto
    private CurricularUnitDto mapToCurricularUnitDto(CurricularUnit curricularUnit) {
        UserDto teacherDto = mapToUserDto(curricularUnit.getTeacher());

        CurricularUnitDto curricularUnitDto = new CurricularUnitDto();
        curricularUnitDto.setId(curricularUnit.getId());
        curricularUnitDto.setName(curricularUnit.getName());
        curricularUnitDto.setTeacher(teacherDto);

        return curricularUnitDto;
    }

    // Helper method to map a MyUser to UserDto
    private UserDto mapToUserDto(MyUser user) {
        RoleDto roleDto = new RoleDto(user.getRole().getId(), user.getRole().getName());

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setRole(roleDto);

        return userDto;
    }
}

