//package com.dam.pms.domain.entity;
//
//import com.dam.pms.domain.enums.ActivityStatus;
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import jakarta.persistence.*;
//import lombok.Data;
//import com.dam.pms.domain.enums.Priority;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Data
//public class Activity {
//
//    public static Object Status;
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String title;
//    private String description;
//
//    @Enumerated(EnumType.STRING)
//    private ActivityStatus status;
//
//    @Enumerated(EnumType.STRING)
//    private Priority priority;
//
//    private Double estimatedHours;
//    private Double actualHours;
//
//    private LocalDate createdDate;
//    private LocalDate dueDate;
//
//    @ManyToOne
//    @JoinColumn(name = "iteration_id")
//    private Iteration iteration;
////
//    @ManyToOne
//    @JoinColumn(name = "team_member_id")
//    @JsonBackReference("member-activities")
//    private TeamMember assignedTo;
////
//    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
//    private List<Subtask> subtasks;
//
//    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
//    private List<ActivityComment> comments;
//
//    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
//    private List<ActivityHistory> historyEntries;
//
//    public void setStatus(ActivityStatus activityStatus)
//    {
//        this.status=activityStatus;
//    }
//
//    public boolean isDone() {
//        return this.status == ActivityStatus.DONE;
//    }
//
//    public void addSubtask(Subtask s) {
//        if (subtasks == null) subtasks = new ArrayList<>();
//        subtasks.add(s);
//        s.setActivity(this);
//    }
//
//    @PostLoad
//    @PostPersist
//    @PostUpdate
//    private void initLists() {
//        if (subtasks == null) subtasks = new ArrayList<>();
//        if (comments == null) comments = new ArrayList<>();
//        if (historyEntries == null) historyEntries = new ArrayList<>();
//    }
//
//}




package com.dam.pms.domain.entity;

import com.dam.pms.domain.enums.ActivityStatus;
import com.dam.pms.domain.enums.Priority;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    @JsonIgnore
    private Iteration iteration;

    @ManyToOne
    @JoinColumn(name = "team_member_id")
    @JsonIgnore
    private TeamMember assignedTo;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Subtask> subtasks = new ArrayList<>();

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ActivityComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ActivityHistory> historyEntries = new ArrayList<>();

    public void setStatus(ActivityStatus activityStatus) {
        this.status = activityStatus;
    }

    public boolean isDone() {
        return this.status == ActivityStatus.DONE;
    }

    public void addSubtask(Subtask s) {
        if (subtasks == null) subtasks = new ArrayList<>();
        subtasks.add(s);
        s.setActivity(this);
    }
}