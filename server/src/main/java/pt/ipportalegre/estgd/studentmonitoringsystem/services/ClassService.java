package pt.ipportalegre.estgd.studentmonitoringsystem.services;

import org.springframework.stereotype.Service;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.ClassSession;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.ClassDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.ClassRequest;

import java.util.List;
import java.util.Set;

@Service
public interface ClassService {

    List<ClassDto> getAllClasses();

    Set<ClassDto> getClassesByCurricularUnit(Long unitId);

    ClassDto createClass(ClassRequest request);

    ClassSession saveClass(ClassSession newClass);

}
