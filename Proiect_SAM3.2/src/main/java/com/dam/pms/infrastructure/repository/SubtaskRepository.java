package com.dam.pms.infrastructure.repository;

import com.dam.pms.domain.entity.Subtask;
import com.dam.pms.domain.enums.ActivityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubtaskRepository extends JpaRepository<Subtask, Long> {

    List<Subtask> findByStatus(ActivityStatus status);

    List<Subtask> findByActivityId(Long activityId);
}
