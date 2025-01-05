package pt.ipportalegre.estgd.studentmonitoringsystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class ParticipationScoresRequestDto {
    private Long attendanceId;
    private List<CategoryScoreDto> categoryScores;

    @Data
    public static class CategoryScoreDto {
        private Long categoryId;
        private Integer score;
    }
}