package pt.ipportalegre.estgd.studentmonitoringsystem.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.ParticipationCategory;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.ParticipationScore;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.ParticipationCategoryResponseDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.ParticipationScoreResponseDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.ParticipationScoresRequestDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.ParticipationScoreService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/participation-scores")
@RequiredArgsConstructor
public class ParticipationScoreController {
    private final ParticipationScoreService participationScoreService;

    @PreAuthorize("hasAuthority(T(pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants).TEACHER.name()) " +
            "or hasAuthority(T(pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants).STUDENT.name())")
    @GetMapping("/attendance/{attendanceId}")
    public ResponseEntity<List<ParticipationScoreResponseDto>> getScoresByAttendanceId(@PathVariable Long attendanceId) {
        List<ParticipationScoreResponseDto> scores = participationScoreService.getScoresByAttendanceId(attendanceId);
        if (scores.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if no scores are found
        }
        return ResponseEntity.ok(scores); // Return 200 OK with the scores
    }

    @PreAuthorize("hasAuthority(T(pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants).TEACHER.name())")
    @PutMapping
    public ResponseEntity<List<ParticipationScoreResponseDto>> createOrUpdateParticipationScore(@RequestBody ParticipationScoresRequestDto requestDto) {

        List<ParticipationScore> updatedScores =
                participationScoreService.createOrUpdateScores(requestDto);

        List<ParticipationScoreResponseDto> responseDtos = updatedScores.stream().map(score -> {
            ParticipationScoreResponseDto responseDto = new ParticipationScoreResponseDto();
            responseDto.setId(score.getId());
            responseDto.setScore(score.getScore());

            ParticipationCategoryResponseDto categoryDto = new ParticipationCategoryResponseDto();
            categoryDto.setId(score.getCategory().getId());
            categoryDto.setName(score.getCategory().getName());
            categoryDto.setDescription(score.getCategory().getDescription());
            responseDto.setParticipationCategory(categoryDto);

            return responseDto;
        }).toList();

        return ResponseEntity.ok(responseDtos);
    }
}
