package com.dam.pms.infrastructure.repository;

import com.dam.pms.domain.entity.Activity;
import com.dam.pms.domain.entity.TeamMember;
import com.dam.pms.domain.enums.ActivityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository
        extends JpaRepository<Activity, Long>, ActivityRepositoryCustom {

    List<Activity> findByAssignedTo(TeamMember assignedTo);
    List<Activity> findByStatus(ActivityStatus status);
    List<Activity> findByIterationId(Long iterationId);
}
