package pt.ipportalegre.estgd.studentmonitoringsystem.services.impl;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.Role;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.RoleDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.repositories.RoleRepository;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.RoleService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Optional<Role> findByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    @Override
    public List<RoleDto> getAllRoles() {
        List<Role> roles = roleRepository.findAll();

        return roles.stream()
                .map(role -> new RoleDto(role.getId(), role.getName()))  // Convert each Role to RoleDto inline
                .collect(Collectors.toList());
    }

    @Override
    public RoleDto createRole(RoleDto roleDto) {
        if(roleRepository.existsByName(roleDto.getName())){
            throw new EntityExistsException("Role with name " + roleDto.getName() + " already exists");
        }

        Role newRole = new Role();
        newRole.setName(roleDto.getName());

        Role savedRole = roleRepository.save(newRole);

        return new RoleDto(savedRole.getId(), roleDto.getName());
    }
}
