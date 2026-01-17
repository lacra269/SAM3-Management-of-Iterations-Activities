//package com.dam.pms.domain.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Data
//public class ActivityHistory {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String fieldChanged;
//    private String oldValue;
//    private String newValue;
//
//    private LocalDateTime changedAt;
//
//    @ManyToOne
//    private Activity activity;
//}


package com.dam.pms.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ActivityHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fieldChanged;
    private String oldValue;
    private String newValue;

    private LocalDateTime changedAt;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    @JsonIgnore
    private Activity activity;
}