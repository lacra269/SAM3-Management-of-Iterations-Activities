package com.dam.pms.ui.service;

import com.dam.pms.domain.entity.TeamMember;
import com.dam.pms.domain.enums.MemberRole;
import com.dam.pms.infrastructure.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamMemberUIService {

    private final TeamMemberRepository teamMemberRepository;

    public List<TeamMember> findAll() {
        return teamMemberRepository.findAll();
    }

    public Optional<TeamMember> findById(Long id) {
        return teamMemberRepository.findById(id);
    }

    public List<TeamMember> findByRole(MemberRole role) {
        return teamMemberRepository.findByRole(role);
    }

    public List<TeamMember> findActive() {
        return teamMemberRepository.findByIsActive(true);
    }

    public TeamMember save(TeamMember member) {
        return teamMemberRepository.save(member);
    }

    public void delete(Long id) {
        teamMemberRepository.deleteById(id);
    }
}