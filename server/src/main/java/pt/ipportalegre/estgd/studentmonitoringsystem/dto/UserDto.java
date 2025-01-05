package pt.ipportalegre.estgd.studentmonitoringsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.Role;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @NotNull
    private Long id;
    private String username;
    private String password;
    private String email;
    private RoleDto role;

    public UserDto(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;

    }

    public UserDto(String username, String password, String email, RoleDto role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public UserDto(Long id, String username, String email, RoleDto role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }
}
