package com.dam.pms.ui.service;

import com.dam.pms.domain.entity.Activity;
import com.dam.pms.domain.entity.Project;
import com.dam.pms.domain.entity.Subtask;
import com.dam.pms.domain.entity.TeamMember;
import com.dam.pms.infrastructure.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UIService {

    private final ProjectRepository projectRepository;
    private final ActivityRepository activityRepository;
    private final SubtaskRepository subtaskRepository;
    private final TeamMemberRepository teamMemberRepository;

    // ========== PROJECT OPERATIONS ==========

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    // ========== ACTIVITY OPERATIONS ==========

    public List<Activity> getActivitiesByProject(Long projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null || project.getIterations() == null) {
            return List.of();
        }

        return project.getIterations().stream()
                .flatMap(iteration -> {
                    if (iteration.getActivities() == null) {
                        return java.util.stream.Stream.empty();
                    }
                    return iteration.getActivities().stream();
                })
                .toList();
    }

    public Activity getActivityById(Long id) {
        return activityRepository.findById(id).orElse(null);
    }

    @Transactional
    public Activity updateActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    @Transactional
    public void deleteActivity(Long activityId) {
        activityRepository.deleteById(activityId);
    }

    // ========== SUBTASK OPERATIONS ==========

    public List<Subtask> getSubtasksByActivity(Long activityId) {
        Activity activity = activityRepository.findById(activityId).orElse(null);
        if (activity == null || activity.getSubtasks() == null) {
            return List.of();
        }
        return activity.getSubtasks();
    }

    @Transactional
    public Subtask updateSubtask(Subtask subtask) {
        return subtaskRepository.save(subtask);
    }

    @Transactional
    public void deleteSubtask(Long subtaskId) {
        subtaskRepository.deleteById(subtaskId);
    }

    @Transactional
    public Subtask createSubtask(Long activityId, Subtask subtask) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        subtask.setActivity(activity);
        return subtaskRepository.save(subtask);
    }

    // ========== TEAM MEMBER OPERATIONS ==========

    public List<TeamMember> getAllTeamMembers() {
        return teamMemberRepository.findAll();
    }

    public List<TeamMember> getActiveTeamMembers() {
        return teamMemberRepository.findByIsActive(true);
    }
}