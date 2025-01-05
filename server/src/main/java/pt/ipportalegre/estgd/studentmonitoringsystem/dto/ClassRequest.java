package pt.ipportalegre.estgd.studentmonitoringsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClassRequest {
    private String className;
    private LocalDateTime date;
    private Long curricularUnitId;
    private String teacherEmail;
}
