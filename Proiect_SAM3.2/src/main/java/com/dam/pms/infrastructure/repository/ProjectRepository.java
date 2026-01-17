package com.dam.pms.infrastructure.repository;

import com.dam.pms.domain.entity.Project;
import com.dam.pms.domain.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByStatus(ProjectStatus status);
}
