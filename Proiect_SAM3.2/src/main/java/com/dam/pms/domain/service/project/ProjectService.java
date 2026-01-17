package com.dam.pms.domain.service.project;

import com.dam.pms.domain.entity.Project;
import com.dam.pms.infrastructure.repository.ProjectRepository;
import com.dam.pms.domain.service.computation.ComputationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ComputationService computationService;

    @Transactional
    public Project create(Project p) {
        if (p.getIterations() == null) p.setIterations(java.util.Collections.emptyList());
        Project saved = projectRepository.save(p);
        updateProgress(saved);
        return saved;
    }

    @Transactional
    public Project update(Project p) {
        Project saved = projectRepository.save(p);
        updateProgress(saved);
        return saved;
    }

    public double getProgress(Project p) {
        return computationService.calculateProjectProgress(p);
    }

    private void updateProgress(Project p) {
        double val = computationService.calculateProjectProgress(p);
        p.setProgress(val);
        projectRepository.save(p);
    }
}
