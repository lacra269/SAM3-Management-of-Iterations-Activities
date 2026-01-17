//package com.dam.pms.domain.entity;
//
//import com.dam.pms.domain.enums.Priority;
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import jakarta.persistence.*;
//import lombok.Data;
//
//@Entity
//@Data
//public class Risk {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String description;
//
//    @Enumerated(EnumType.STRING)
//    private Priority severity;
//
//    private boolean resolved;
//
////    @ManyToOne
////    private Project project;
//
//
//    ////
//    @ManyToOne
//    @JoinColumn(name = "project_id")
//    @JsonBackReference("project-risks")
//    private Project project;
//}


package com.dam.pms.domain.entity;

import com.dam.pms.domain.enums.Priority;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Risk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Enumerated(EnumType.STRING)
    private Priority severity;

    private boolean resolved;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonIgnore
    private Project project;
}