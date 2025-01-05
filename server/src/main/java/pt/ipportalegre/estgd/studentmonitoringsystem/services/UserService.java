package pt.ipportalegre.estgd.studentmonitoringsystem.services;

import org.springframework.stereotype.Service;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.MyUser;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.UserDto;

import java.util.List;

@Service
public interface UserService {
    List<UserDto> getUsersByCurricularUnitId(Long curricularUnitId);

    UserDto createUser(UserDto userDto);

    List<UserDto> getAllUsers();

    MyUser getUserById(Long id);

    boolean isTeacherOfCurricularUnit(Long curricularUnitId);

    boolean isStudentOfCurricularUnit(Long curricularUnitId);
}
