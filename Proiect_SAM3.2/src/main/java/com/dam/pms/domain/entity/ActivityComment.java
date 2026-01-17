package com.dam.pms.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ActivityComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;
    private LocalDateTime timestamp;

    @ManyToOne
    private Activity activity;

    @ManyToOne
    private TeamMember member;
}
