package pt.ipportalegre.estgd.studentmonitoringsystem.services;

import org.springframework.stereotype.Service;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.Role;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.RoleDto;

import java.util.List;
import java.util.Optional;

@Service
public interface RoleService {
    Optional<Role> findByName(String name);

    List<RoleDto> getAllRoles();

    RoleDto createRole(RoleDto roleDto);
}
