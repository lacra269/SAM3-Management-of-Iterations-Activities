package com.dam.pms.infrastructure.repository;

import com.dam.pms.domain.entity.ProjectTeam;
import com.dam.pms.domain.entity.Project;
import com.dam.pms.domain.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectTeamRepository extends JpaRepository<ProjectTeam, Long> {

    List<ProjectTeam> findByProject(Project project);

    List<ProjectTeam> findByTeamMember(TeamMember member);
}
