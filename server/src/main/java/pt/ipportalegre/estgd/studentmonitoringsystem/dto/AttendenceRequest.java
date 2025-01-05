package pt.ipportalegre.estgd.studentmonitoringsystem.dto;

import lombok.Data;

@Data
public class AttendenceRequest {
    private Long classSessionId;
    private String userEmail;
    private boolean attended;
}
