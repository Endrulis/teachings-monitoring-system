package pt.ipportalegre.estgd.studentmonitoringsystem.dto;

import lombok.Data;

@Data
public class AttendenceRequestForGettingStatus {
    private String studentEmail;
    private Long classSessionId;
}
