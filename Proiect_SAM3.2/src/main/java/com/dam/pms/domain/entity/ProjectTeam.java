////package com.dam.pms.domain.entity;
////
////import jakarta.persistence.*;
////import lombok.Data;
////
////@Entity
////@Data
////public class ProjectTeam {
////
////    @Id
////    @GeneratedValue(strategy = GenerationType.IDENTITY)
////    private Long id;
////
////    private String roleInProject;
////
////    @ManyToOne
////    private Project project;
////
////    @ManyToOne
////    private TeamMember teamMember;
////}
//package com.dam.pms.domain.entity;
//
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import jakarta.persistence.*;
//import lombok.Data;
//
//@Entity
//@Data
//public class ProjectTeam {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String roleInProject;
//
//    @ManyToOne
//    @JoinColumn(name = "project_id")
//    @JsonBackReference("project-teams")
//    private Project project;
//
//    @ManyToOne
//    @JoinColumn(name = "team_member_id")
//    @JsonBackReference("member-teams")
//    private TeamMember teamMember;
//}




package com.dam.pms.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ProjectTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roleInProject;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonIgnore
    private Project project;

    @ManyToOne
    @JoinColumn(name = "team_member_id")
    @JsonIgnore
    private TeamMember teamMember;
}