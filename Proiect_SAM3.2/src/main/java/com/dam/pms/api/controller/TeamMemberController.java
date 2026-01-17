package com.dam.pms.api.controller;

import com.dam.pms.domain.entity.TeamMember;
import com.dam.pms.domain.enums.MemberRole;
import com.dam.pms.infrastructure.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team-members")
@RequiredArgsConstructor
public class TeamMemberController {

    private final TeamMemberRepository teamMemberRepository;

    @GetMapping
    public ResponseEntity<List<TeamMember>> getAllMembers() {
        List<TeamMember> members = teamMemberRepository.findAll();
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamMember> getMemberById(@PathVariable Long id) {
        return teamMemberRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<TeamMember>> getMembersByRole(@PathVariable MemberRole role) {
        List<TeamMember> members = teamMemberRepository.findByRole(role);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/active")
    public ResponseEntity<List<TeamMember>> getActiveMembers() {
        List<TeamMember> members = teamMemberRepository.findByIsActive(true);
        return ResponseEntity.ok(members);
    }

    @PostMapping
    public ResponseEntity<TeamMember> createTeamMember(@RequestBody TeamMember member) {
        TeamMember created = teamMemberRepository.save(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamMember> updateTeamMember(@PathVariable Long id, @RequestBody TeamMember member) {
        if (!teamMemberRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        member.setId(id);
        TeamMember updated = teamMemberRepository.save(member);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeamMember(@PathVariable Long id) {
        if (!teamMemberRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        teamMemberRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}