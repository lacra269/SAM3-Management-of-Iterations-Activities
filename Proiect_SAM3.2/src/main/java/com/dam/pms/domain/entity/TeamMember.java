package com.dam.pms.domain.entity;



import com.dam.pms.domain.entity.ActivityComment;
import com.dam.pms.domain.enums.MemberRole;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    private String skills;
    private Boolean isActive;

    @OneToMany(mappedBy = "assignedTo")
    private List<Activity> activities;

    @OneToMany(mappedBy = "member")
    private List<ActivityComment> comments;

    @OneToMany(mappedBy = "teamMember")
    private List<ProjectTeam> projectTeams;
}
