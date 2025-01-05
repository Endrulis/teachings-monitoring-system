package pt.ipportalegre.estgd.studentmonitoringsystem.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.ParticipationCategory;
import pt.ipportalegre.estgd.studentmonitoringsystem.repositories.ParticipationCategoryRepository;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.ParticipationCategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipationCategoryServiceImpl implements ParticipationCategoryService {

    private final ParticipationCategoryRepository participationCategoryRepository;

    @Override
    public List<ParticipationCategory> getAllParticipationCategories() {
        return participationCategoryRepository.findAll();
    }

    @Override
    public ParticipationCategory getParticipationCategoryById(Long id) {
        return participationCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }
}
