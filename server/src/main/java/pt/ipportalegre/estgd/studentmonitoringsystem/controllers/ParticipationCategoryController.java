package pt.ipportalegre.estgd.studentmonitoringsystem.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ipportalegre.estgd.studentmonitoringsystem.domain.ParticipationCategory;
import pt.ipportalegre.estgd.studentmonitoringsystem.services.ParticipationCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class ParticipationCategoryController {

    private final ParticipationCategoryService participationCategoryService;

    @PreAuthorize("hasAuthority(T(pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants).TEACHER.name()) " +
            "or hasAuthority(T(pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants).STUDENT.name())")
    @GetMapping
    public ResponseEntity<List<ParticipationCategory>> getAllCategories() {
        List<ParticipationCategory> categories = participationCategoryService.getAllParticipationCategories();
        return ResponseEntity.ok(categories);
    }

    @PreAuthorize("hasAuthority(T(pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants).TEACHER.name()) " +
            "or hasAuthority(T(pt.ipportalegre.estgd.studentmonitoringsystem.domain.RoleConstants).STUDENT.name())")
    @GetMapping("/{id}")
    public ResponseEntity<ParticipationCategory> getCategoryById(@PathVariable Long id) {
        ParticipationCategory category = participationCategoryService.getParticipationCategoryById(id);
        return ResponseEntity.ok(category);
    }
}
