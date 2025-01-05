package pt.ipportalegre.estgd.studentmonitoringsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClassDto {
    private Long id;
    private String className;
    private LocalDateTime date;
    private Long curricularUnitId;
}
