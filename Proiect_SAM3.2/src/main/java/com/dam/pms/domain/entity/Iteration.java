//package com.dam.pms.domain.entity;
//
//
//
//import jakarta.persistence.*;
//import lombok.Data;
//import java.util.List;
//
//@Entity
//@Data
//public class Iteration {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String name;
//    private Integer iterationNumber;
//
//    @ManyToOne
//    @JoinColumn(name = "project_id")
//    private Project project;
//
//    @OneToMany(mappedBy = "iteration")
//    private List<Activity> activities;
//}

package com.dam.pms.domain.entity;

import com.dam.pms.domain.enums.ActivityStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Iteration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer iterationNumber;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "iteration")
    private List<Activity> activities;

    // --------- metoda calculateProgress ----------
    public double calculateProgress() {
        if (activities == null || activities.isEmpty()) return 0.0;

        long done = activities.stream()
                .filter(a -> a.getStatus() == ActivityStatus.DONE)
                .count();

        return ((double) done / activities.size()) * 100;
    }
}
