package com.dam.pms.domain.entity;

import com.dam.pms.domain.enums.ActivityStatus;
import com.dam.pms.domain.enums.Priority;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private ActivityStatus status;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private Double estimatedHours;
    private Double actualHours;

    private LocalDate createdDate;
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "iteration_id")
    private Iteration iteration;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private TeamMember assignedTo;
}
