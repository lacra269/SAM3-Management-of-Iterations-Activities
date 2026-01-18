
package com.dam.pms.ui.service;

import com.dam.pms.domain.entity.Activity;
import com.dam.pms.domain.entity.Iteration;
import com.dam.pms.domain.entity.Project;
import com.dam.pms.domain.entity.Subtask;
import com.dam.pms.domain.enums.ActivityStatus;
import com.dam.pms.domain.service.computation.ComputationService;
import com.dam.pms.infrastructure.repository.ActivityRepository;
import com.dam.pms.infrastructure.repository.IterationRepository;
import com.dam.pms.infrastructure.repository.ProjectRepository;
import com.dam.pms.infrastructure.repository.SubtaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubtaskUIService {

    private final SubtaskRepository subtaskRepository;
    private final ActivityRepository activityRepository;
    private final IterationRepository iterationRepository;
    private final ProjectRepository projectRepository;
    private final ComputationService computationService;

    public List<Subtask> findAll() {
        return subtaskRepository.findAll();
    }

    public Optional<Subtask> findById(Long id) {
        return subtaskRepository.findById(id);
    }

    public List<Subtask> findByActivityId(Long activityId) {
        return subtaskRepository.findByActivityId(activityId);
    }

    /**
     * ✅ Salvează subtask și actualizează automat progresul ierarhiei
     * FIX: Re-încarcă activitatea cu subtasks pentru a evita LazyInitializationException
     */
    public Subtask save(Subtask subtask) {
        if (subtask.getStatus() == null) {
            subtask.setStatus(ActivityStatus.TODO);
        }

        // Salvează subtask-ul (cu membrul asignat dacă există)
        Subtask saved = subtaskRepository.save(subtask);

        // ✅ FIX: Re-încarcă activitatea cu subtasks
        if (saved.getActivity() != null) {
            Long activityId = saved.getActivity().getId();
            Activity freshActivity = activityRepository.findById(activityId).orElse(null);

            if (freshActivity != null) {
                // Forțează încărcarea subtasks-urilor
                freshActivity.getSubtasks().size();
                updateProgressHierarchy(freshActivity);
            }
        }

        return saved;
    }

    /**
     * ✅ Șterge subtask și actualizează progresul
     */
    public void delete(Long id) {
        Optional<Subtask> subtaskOpt = subtaskRepository.findById(id);
        if (subtaskOpt.isPresent()) {
            Subtask subtask = subtaskOpt.get();
            Long activityId = subtask.getActivity() != null ? subtask.getActivity().getId() : null;

            subtaskRepository.deleteById(id);

            // ✅ FIX: Re-încarcă activitatea cu subtasks după ștergere
            if (activityId != null) {
                Activity freshActivity = activityRepository.findById(activityId).orElse(null);
                if (freshActivity != null) {
                    freshActivity.getSubtasks().size(); // Forțează încărcarea
                    updateProgressHierarchy(freshActivity);
                }
            }
        }
    }

    public void delete(Subtask subtask) {
        Long activityId = subtask.getActivity() != null ? subtask.getActivity().getId() : null;

        subtaskRepository.delete(subtask);

        // ✅ FIX: Re-încarcă activitatea cu subtasks după ștergere
        if (activityId != null) {
            Activity freshActivity = activityRepository.findById(activityId).orElse(null);
            if (freshActivity != null) {
                freshActivity.getSubtasks().size(); // Forțează încărcarea
                updateProgressHierarchy(freshActivity);
            }
        }
    }

    /**
     * ✅ Actualizează progresul Activity → Iteration → Project
     */
    private void updateProgressHierarchy(Activity activity) {
        if (activity == null) return;

        try {


            // 2. Actualizează Iteration
            Iteration iteration = activity.getIteration();
            if (iteration != null) {
                // ✅ Re-încarcă iterația pentru a avea toate activitățile
                iteration   = iterationRepository.findById(iteration.getId()).orElse(iteration);

                double iterationProgress = computationService.calculateIterationProgress(iteration);
                iteration.setProgress(iterationProgress);
                iterationRepository.save(iteration);

                // 3. Actualizează Project
                Project project = iteration.getProject();
                if (project != null) {
                    // ✅ Re-încarcă proiectul
                    project = projectRepository.findById(project.getId()).orElse(project);

                    double projectProgress = computationService.calculateProjectProgress(project);
                    project.setProgress(projectProgress);
                    projectRepository.save(project);
                }
            }
        } catch (Exception e) {
            // Log error dar nu întrerupe operația
            System.err.println("⚠️ Eroare la actualizarea progresului: " + e.getMessage());
            e.printStackTrace();
        }
    }
}