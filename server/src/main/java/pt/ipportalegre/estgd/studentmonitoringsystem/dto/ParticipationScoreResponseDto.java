package pt.ipportalegre.estgd.studentmonitoringsystem.dto;

import lombok.Data;

@Data
public class ParticipationScoreResponseDto {
    private Long id;
    private Integer score;
    private ParticipationCategoryResponseDto participationCategory;
}
