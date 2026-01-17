package com.dam.pms.domain.service.activity;

import com.dam.pms.domain.entity.Activity;
import com.dam.pms.domain.entity.ActivityHistory;
import com.dam.pms.domain.entity.Iteration;
import com.dam.pms.domain.entity.Project;
import com.dam.pms.domain.entity.Subtask;
import com.dam.pms.domain.entity.TeamMember;
import com.dam.pms.domain.enums.ActivityStatus;
import com.dam.pms.domain.service.computation.ComputationService;
import com.dam.pms.domain.service.validation.ValidationService;
import com.dam.pms.domain.service.workflow.WorkflowRulesEngine;
import com.dam.pms.infrastructure.repository.ActivityHistoryRepository;
import com.dam.pms.infrastructure.repository.ActivityRepository;
import com.dam.pms.infrastructure.repository.IterationRepository;
import com.dam.pms.infrastructure.repository.ProjectRepository;
import com.dam.pms.infrastructure.repository.SubtaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final SubtaskRepository subtaskRepository;
    private final IterationRepository iterationRepository;
    private final ProjectRepository projectRepository;
    private final ValidationService validationService;
    private final ComputationService computationService;
    private final WorkflowRulesEngine rulesEngine;
    private final ActivityHistoryRepository historyRepository;

    @Transactional
    public Activity createActivity(Activity a) {
        validationService.validateActivityAggregate(a);
        if (a.getCreatedDate() == null) a.setCreatedDate(java.time.LocalDate.now());
        Activity saved = activityRepository.save(a);
        cascadeProgress(saved);
        return saved;
    }

    @Transactional
    public Activity updateActivity(Activity a) {
        Activity persisted = activityRepository.findById(a.getId())
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        if (persisted.getStatus() != a.getStatus()) {
            ActivityHistory h = new ActivityHistory();
            h.setActivity(persisted);
            h.setFieldChanged("status");
            h.setOldValue(persisted.getStatus() == null ? null : persisted.getStatus().name());
            h.setNewValue(a.getStatus() == null ? null : a.getStatus().name());
            h.setChangedAt(LocalDateTime.now());
            historyRepository.save(h);
        }

        Activity saved = activityRepository.save(a);
        cascadeProgress(saved);
        return saved;
    }

    @Transactional
    public Activity changeStatus(Long activityId, ActivityStatus newStatus, TeamMember by) {
        Activity a = activityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        if (!rulesEngine.canTransition(a, newStatus)) {
            throw new IllegalStateException("Transition not allowed by rules engine");
        }

        ActivityHistory h = new ActivityHistory();
        h.setActivity(a);
        h.setFieldChanged("status");
        h.setOldValue(a.getStatus() == null ? null : a.getStatus().name());
        h.setNewValue(newStatus.name());
        h.setChangedAt(LocalDateTime.now());
        historyRepository.save(h);

        a.setStatus(newStatus);
        Activity saved = activityRepository.save(a);

        cascadeProgress(saved);
        return saved;
    }
//-----am inlocuit placeholderul ; care nu e valid in java...

    @Transactional
    public Subtask addSubtask(Long activityId, Subtask s) {
        Activity a = activityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        // 1. atașăm subtask-ul
        s.setActivity(a);
        Subtask savedSubtask = subtaskRepository.save(s);

        // 2. derivăm un nou status pe baza subtasks
        ActivityStatus derived = rulesEngine.deriveActivityStatusFromSubtasks(a);

        // 3. dacă statusul se schimbă → salvăm istoric
        if (derived != null && a.getStatus() != derived) {
            ActivityHistory h = new ActivityHistory();
            h.setActivity(a);
            h.setFieldChanged("status");
            h.setOldValue(a.getStatus() == null ? null : a.getStatus().name());
            h.setNewValue(derived.name());
            h.setChangedAt(LocalDateTime.now());
            historyRepository.save(h);

            a.setStatus(derived);
        }

        // 4. salvăm activity
        Activity savedActivity = activityRepository.save(a);

        // 5. actualizăm progresul iterației / proiectului
        cascadeProgress(savedActivity);

        return savedSubtask;
    }

    private void cascadeProgress(Activity a) {
        Iteration it = a.getIteration();
        if (it != null) {
            double itProg = computationService.calculateIterationProgress(it);
            it.setProgress(itProg);
            iterationRepository.save(it);

            Project p = it.getProject();
            if (p != null) {
                double pProg = computationService.calculateProjectProgress(p);
                p.setProgress(pProg);
                projectRepository.save(p);
            }
        }
    }
}
