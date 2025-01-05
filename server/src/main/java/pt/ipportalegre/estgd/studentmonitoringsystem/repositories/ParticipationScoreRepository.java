package pt.ipportalegre.estgd.studentmonitoringsystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.ParticipationScore;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationScoreRepository extends JpaRepository<ParticipationScore, Long> {
    Optional<ParticipationScore> findByAttendanceIdAndCategoryId(Long attendanceId, Long categoryId);
    List<ParticipationScore> findByAttendanceId(Long attendanceId);
}
