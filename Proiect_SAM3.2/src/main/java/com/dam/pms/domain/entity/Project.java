////package com.dam.pms.domain.entity;
////
////import com.dam.pms.domain.enums.ProjectStatus;
////import jakarta.persistence.*;
////import lombok.Data;
////
////import java.util.ArrayList;
////import java.util.List;
////
////@Entity
////@Data
////public class Project {
////
////    @Id
////    @GeneratedValue(strategy = GenerationType.IDENTITY)
////    private Long id;
////
////    private String name;
////    private String description;
////
////    @Enumerated(EnumType.STRING)
////    private ProjectStatus status;
////
////    private Double progress = 0.0;
////
////    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
////    private List<Iteration> iterations = new ArrayList<>();
////
////    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
////    private List<Risk> risks;
////
////    @OneToMany(mappedBy = "project")
////    private List<ProjectTeam> projectTeams;
////}
//package com.dam.pms.domain.entity;
//
//import com.dam.pms.domain.enums.ProjectStatus;
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Data
//public class Project {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String name;
//    private String description;
//
//    @Enumerated(EnumType.STRING)
//    private ProjectStatus status;
//
//    private Double progress = 0.0;
//
//    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference("project-iterations")
//    private List<Iteration> iterations = new ArrayList<>();
//
//    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
//    @JsonManagedReference("project-risks")
//    private List<Risk> risks;
//
//    @OneToMany(mappedBy = "project")
//    @JsonManagedReference("project-teams")
//    private List<ProjectTeam> projectTeams;
//}



package com.dam.pms.domain.entity;

import com.dam.pms.domain.enums.ProjectStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    private Double progress = 0.0;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Iteration> iterations = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Risk> risks = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    @JsonIgnore
    private List<ProjectTeam> projectTeams = new ArrayList<>();
}