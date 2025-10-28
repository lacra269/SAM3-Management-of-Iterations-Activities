package com.dam.pms.domain.service.workflow;

import com.dam.pms.domain.entity.Project;
import com.dam.pms.infrastructure.repository.ProjectRepository;
import com.dam.pms.domain.service.computation.ComputationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ComputationService computationService;

    public Project save(Project project) {
        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public double getProjectProgress(Project project) {
        return computationService.calculateProjectProgress(project);
    }
}
