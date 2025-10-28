package com.dam.pms.domain.entity;

import com.dam.pms.domain.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.Data;
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

    @OneToMany(mappedBy = "project")
    private List<Iteration> iterations;
}

