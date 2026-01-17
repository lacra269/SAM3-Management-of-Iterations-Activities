//package com.dam.pms.domain.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import com.dam.pms.domain.enums.MemberRole;
//
//import java.util.List;
//
//@Entity
//@Data
//public class TeamMember {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String name;
//    private String email;
//    private String passwordHash;
//
//    @Enumerated(EnumType.STRING)
//    private MemberRole role;
//
//    private String skills;
//    private Boolean isActive;
//
//    @OneToMany(mappedBy = "teamMember")
//    private List<ProjectTeam> projectTeams;
//
//    @OneToMany(mappedBy = "assignedTo")
//    private List<Activity> activities;
//}



package com.dam.pms.domain.entity;

import com.dam.pms.domain.enums.MemberRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
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

    @OneToMany(mappedBy = "teamMember")
    @JsonIgnore
    private List<ProjectTeam> projectTeams = new ArrayList<>();

    @OneToMany(mappedBy = "assignedTo")
    @JsonIgnore
    private List<Activity> activities = new ArrayList<>();
}