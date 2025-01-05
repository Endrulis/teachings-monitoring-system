package pt.ipportalegre.estgd.studentmonitoringsystem.services;

import org.springframework.stereotype.Service;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.CurricularUnit;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.CurricularUnitDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.CurricularUnitRequest;

import java.util.List;
import java.util.Set;

@Service
public interface CurricularUnitService {

    CurricularUnitDto createCurricularUnit(CurricularUnitRequest request);

    Set<CurricularUnitDto> getAllCurricularUnitsByStudentEmail(String studentEmail);

    Set<CurricularUnitDto> getAllCurricularUnits();

    void assignStudentsToCurricularUnit(Long curricularUnitId, List<Long> studentIds);

    Set<CurricularUnitDto> getAllCurricularUnitsByStudentId(Long studentId);

    Set<CurricularUnitDto> getAllCurricularUnitsByTeacherEmail(String teacherEmail);

    Set<CurricularUnitDto> getAllCurricularUnitsByTeacherId(Long teacherId);

    CurricularUnit saveCurricularUnit(CurricularUnit curricularUnit);

    boolean isTeacherOfCurricularUnit(Long curricularUnitId, String username);
}
