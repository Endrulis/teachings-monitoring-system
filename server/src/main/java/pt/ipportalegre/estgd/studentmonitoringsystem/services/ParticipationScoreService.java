package pt.ipportalegre.estgd.studentmonitoringsystem.services;

import org.springframework.stereotype.Service;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.ParticipationScore;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.ParticipationScoreResponseDto;
import pt.ipportalegre.estgd.studentmonitoringsystem.dto.ParticipationScoresRequestDto;

import java.util.List;

@Service
public interface ParticipationScoreService {
    List<ParticipationScoreResponseDto> getScoresByAttendanceId(Long attendanceId);

    List<ParticipationScore> createOrUpdateScores(ParticipationScoresRequestDto requestDto);
}
