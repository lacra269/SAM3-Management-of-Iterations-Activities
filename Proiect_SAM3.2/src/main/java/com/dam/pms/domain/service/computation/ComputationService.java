package com.dam.pms.domain.service.computation;

import com.dam.pms.domain.entity.Project;
import com.dam.pms.domain.entity.Iteration;
import org.springframework.stereotype.Service;


@Service
public class ComputationService {

    public double calculateProjectProgress(Project project) {
        if (project.getIterations() == null || project.getIterations().isEmpty()) return 0.0;
        return project.getIterations().stream()
                .mapToDouble(Iteration::calculateProgress)
                .average()
                .orElse(0.0);
    }

    public double calculateIterationProgress(Iteration iteration) {
        if (iteration.getActivities() == null || iteration.getActivities().isEmpty()) return 0.0;
        long done = iteration.getActivities().stream()
                .filter(a -> a.getStatus() == com.dam.pms.domain.enums.ActivityStatus.DONE)
                .count();
        return ((double) done / iteration.getActivities().size()) * 100;
    }
}
