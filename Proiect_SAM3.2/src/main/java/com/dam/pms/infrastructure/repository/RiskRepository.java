package com.dam.pms.infrastructure.repository;

import com.dam.pms.domain.entity.Risk;
import com.dam.pms.domain.enums.Priority;
import com.dam.pms.domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiskRepository extends JpaRepository<Risk, Long> {

    List<Risk> findByProject(Project project);

    List<Risk> findBySeverity(Priority severity);

    List<Risk> findByResolved(boolean resolved);
}
