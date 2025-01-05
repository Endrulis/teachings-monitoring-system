package pt.ipportalegre.estgd.studentmonitoringsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class AttendanceDto {
    private Long id;
    private boolean attended;
    private Long studentId;
    private Long classSessionId;

    public AttendanceDto() {}

    public AttendanceDto(Long id, boolean attended, Long studentId, Long classSessionId) {
        this.id = id;
        this.attended = attended;
        this.studentId = studentId;
        this.classSessionId = classSessionId;
    }
}