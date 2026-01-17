package com.dam.pms.infrastructure.repository;

import com.dam.pms.domain.entity.Iteration;
import com.dam.pms.domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IterationRepository extends JpaRepository<Iteration, Long> {

    List<Iteration> findByProject(Project project);
}
