package pt.ipportalegre.estgd.studentmonitoringsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurricularUnitDto {
    private Long id;
    private String name;
    private UserDto teacher;
}

