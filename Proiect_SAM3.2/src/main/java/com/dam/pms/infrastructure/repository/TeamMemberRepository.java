package com.dam.pms.infrastructure.repository;

import com.dam.pms.domain.entity.TeamMember;
import com.dam.pms.domain.enums.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findByRole(MemberRole role);

    List<TeamMember> findByIsActive(Boolean active);
}
