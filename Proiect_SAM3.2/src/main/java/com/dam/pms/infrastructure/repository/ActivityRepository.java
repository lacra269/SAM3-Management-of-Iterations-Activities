package com.dam.pms.infrastructure.repository;

import com.dam.pms.domain.entity.Activity;
import com.dam.pms.domain.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByAssignedTo(TeamMember member);
}
