package com.dam.pms.infrastructure.repository;

import com.dam.pms.domain.entity.Activity;
import com.dam.pms.domain.entity.TeamMember;
import com.dam.pms.domain.enums.ActivityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByAssignedTo(TeamMember assignedTo);
    List<Activity> findByStatus(ActivityStatus status);
    List<Activity> findByIterationId(Long iterationId);

    // 🔹 Nou: încarcă activitățile + subtasks direct
    @Query("SELECT a FROM Activity a LEFT JOIN FETCH a.subtasks WHERE a.iteration.id = :iterationId")
    List<Activity> findByIterationIdWithSubtasks(@Param("iterationId") Long iterationId);


}

