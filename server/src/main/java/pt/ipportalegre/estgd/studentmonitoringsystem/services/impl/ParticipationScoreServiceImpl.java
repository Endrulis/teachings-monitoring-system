package pt.ipportalegre.estgd.studentmonitoringsystem.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.Attendance;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.ParticipationCategory;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.ParticipationScore;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.ParticipationCategoryResponseDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.ParticipationScoreResponseDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.ParticipationScoresRequestDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.repositories.AttendanceRepository;
import pt.ipportalegre.estgd.studentmonitoringsystem.repositories.ParticipationCategoryRepository;
import pt.ipportalegre.estgd.studentmonitoringsystem.repositories.ParticipationScoreRepository;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.ParticipationScoreService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationScoreServiceImpl implements ParticipationScoreService {
    private final ParticipationScoreRepository participationScoreRepository;
    private final AttendanceRepository attendanceRepository;
    private final ParticipationCategoryRepository categoryRepository;

    @Override
    public List<ParticipationScoreResponseDto> getScoresByAttendanceId(Long attendanceId) {
        return participationScoreRepository.findByAttendanceId(attendanceId)
                .stream()
                .map(score -> {
                    // Map ParticipationCategory to ParticipationCategoryResponseDto
                    ParticipationCategoryResponseDto categoryDto = new ParticipationCategoryResponseDto();
                    categoryDto.setId(score.getCategory().getId());
                    categoryDto.setName(score.getCategory().getName());
                    categoryDto.setDescription(score.getCategory().getDescription());

                    // Map ParticipationScore to ParticipationScoreResponseDto
                    ParticipationScoreResponseDto scoreDto = new ParticipationScoreResponseDto();
                    scoreDto.setId(score.getId());
                    scoreDto.setScore(score.getScore());
                    scoreDto.setParticipationCategory(categoryDto);

                    return scoreDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationScore> createOrUpdateScores(ParticipationScoresRequestDto requestDto) {
        Long attendanceId = requestDto.getAttendanceId();
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new EntityNotFoundException("Attendance not found"));

        List<ParticipationScore> updatedScores = new ArrayList<>();

        for (ParticipationScoresRequestDto.CategoryScoreDto scoreDto : requestDto.getCategoryScores()) {
            Long categoryId = scoreDto.getCategoryId();
            Integer score = scoreDto.getScore();

            if (score < 1 || score > 5) {
                throw new IllegalArgumentException("Score must be between 1 and 5");
            }

            ParticipationCategory category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));

            Optional<ParticipationScore> existingScore =
                    participationScoreRepository.findByAttendanceIdAndCategoryId(attendanceId, categoryId);

            ParticipationScore participationScore = existingScore.orElseGet(() -> {
                ParticipationScore newScore = new ParticipationScore();
                newScore.setAttendance(attendance);
                newScore.setCategory(category);
                return newScore;
            });

            participationScore.setScore(score);
            updatedScores.add(participationScoreRepository.save(participationScore));
        }

        return updatedScores;
    }

}
