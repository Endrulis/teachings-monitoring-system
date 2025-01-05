package pt.ipportalegre.estgd.studentmonitoringsystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.ParticipationCategory;

public interface ParticipationCategoryRepository extends JpaRepository<ParticipationCategory, Long> {
}
