package pt.ipportalegre.estgd.studentmonitoringsystem.services;

import org.springframework.stereotype.Service;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.ParticipationCategory;

import java.util.List;

@Service
public interface ParticipationCategoryService {
    List<ParticipationCategory> getAllParticipationCategories();

    ParticipationCategory getParticipationCategoryById(Long id);
}
