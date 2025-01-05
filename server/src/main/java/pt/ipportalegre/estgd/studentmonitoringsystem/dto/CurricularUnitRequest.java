package pt.ipportalegre.estgd.studentmonitoringsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurricularUnitRequest {
    private String name;
    private Long teacherId;
}
