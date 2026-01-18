


package com.dam.pms.ui.service;

import com.dam.pms.domain.entity.Activity;
import com.dam.pms.domain.entity.Iteration;
import com.dam.pms.domain.entity.Project;
import com.dam.pms.domain.service.computation.ComputationService;
import com.dam.pms.infrastructure.repository.ActivityRepository;
import com.dam.pms.infrastructure.repository.IterationRepository;
import com.dam.pms.infrastructure.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityUIService {

    private final ActivityRepository activityRepository;
    private final ProjectUIService projectUIService;
    private final IterationRepository iterationRepository;
    private final ProjectRepository projectRepository;
    private final ComputationService computationService;

    public List<Activity> findAll() {
        return activityRepository.findAll();
    }

    public Optional<Activity> findById(Long id) {
        return activityRepository.findById(id);
    }

    public List<Activity> findByProjectId(Long projectId) {
        List<Activity> result = new ArrayList<>();

        Project project = projectUIService.findById(projectId).orElse(null);
        if (project == null) return result;

        if (project.getIterations() != null) {
            for (Iteration iteration : project.getIterations()) {
                List<Activity> activities = activityRepository.findByIterationIdWithSubtasks(iteration.getId());
                result.addAll(activities);
            }
        }

        return result;
    }

    public List<Activity> findByIterationId(Long iterationId) {
        return activityRepository.findByIterationId(iterationId);
    }

    public List<Activity> findByIterationIdWithSubtasks(Long iterationId) {
        return activityRepository.findByIterationIdWithSubtasks(iterationId);
    }

    /**
     * ✨ Salvează activitate și actualizează progresul ierarhiei
     */
    public Activity save(Activity activity) {
        Activity saved = activityRepository.save(activity);

        // ✅ Actualizează progresul în cascadă
        updateProgressHierarchy(saved);

        return saved;
    }

    /**
     * ✨ Șterge activitate și actualizează progresul
     */
    public void delete(Activity activity) {
        Iteration iteration = activity.getIteration();
        activityRepository.delete(activity);

        // Actualizează progresul după ștergere
        if (iteration != null) {
            double iterationProgress = computationService.calculateIterationProgress(iteration);
            iteration.setProgress(iterationProgress);
            iterationRepository.save(iteration);

            Project project = iteration.getProject();
            if (project != null) {
                double projectProgress = computationService.calculateProjectProgress(project);
                project.setProgress(projectProgress);
                projectRepository.save(project);
            }
        }
    }

    /**
     * ✨ Actualizează progresul Activity → Iteration → Project
     */
    private void updateProgressHierarchy(Activity activity) {
        if (activity == null) return;

        Iteration iteration = activity.getIteration();
        if (iteration != null) {
            double iterationProgress = computationService.calculateIterationProgress(iteration);
            iteration.setProgress(iterationProgress);
            iterationRepository.save(iteration);

            Project project = iteration.getProject();
            if (project != null) {
                double projectProgress = computationService.calculateProjectProgress(project);
                project.setProgress(projectProgress);
                projectRepository.save(project);
            }
        }
    }
}
