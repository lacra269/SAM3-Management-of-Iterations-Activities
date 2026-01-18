
package com.dam.pms.ui.service;

import com.dam.pms.domain.entity.Project;
import com.dam.pms.domain.enums.ProjectStatus;
import com.dam.pms.infrastructure.repository.ProjectRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectUIService {

    private final ProjectRepository projectRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Găsește toate proiectele cu EAGER fetch pentru iterations
     * (pentru Vaadin UI)
     */
    public List<Project> findAll() {
        return entityManager.createQuery(
                "SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.iterations",
                Project.class
        ).getResultList();
    }

    /**
     * Găsește proiecte după status
     */
    public List<Project> findByStatus(ProjectStatus status) {
        return projectRepository.findByStatus(status);
    }

    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    public Project save(Project project) {
        return projectRepository.save(project);
    }

    public void delete(Long id) {
        projectRepository.deleteById(id);
    }
}