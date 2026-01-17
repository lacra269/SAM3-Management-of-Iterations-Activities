////package com.dam.pms.domain.entity;
////
////import com.dam.pms.domain.enums.ActivityStatus;
////import jakarta.persistence.*;
////import lombok.Data;
////
////@Entity
////@Data
////public class Subtask {
////
////    @Id
////    @GeneratedValue(strategy = GenerationType.IDENTITY)
////    private Long id;
////
////    private String name;
////
////    @Enumerated(EnumType.STRING)
////    private ActivityStatus status;
////
////    @ManyToOne
////    @JoinColumn(name = "activity_id")
////    private Activity activity;
////
////    public void setStatus(ActivityStatus activityStatus) {
////    }
////}
//
//
//
//package com.dam.pms.domain.entity;
//
//import com.dam.pms.domain.enums.ActivityStatus;
//import jakarta.persistence.*;
//import lombok.Data;
//
//@Entity
//@Data
//public class Subtask {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String name;
//
//    @Enumerated(EnumType.STRING)
//    private ActivityStatus status;
//
//    @ManyToOne
//    @JoinColumn(name = "activity_id")
//    private Activity activity;
//
//    // IMPORTANT — setter-ul TREBUIE să seteze valoarea!!!
//    public void setStatus(ActivityStatus status) {
//        this.status = status;
//    }
//}
package com.dam.pms.domain.entity;

import com.dam.pms.domain.enums.ActivityStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Subtask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ActivityStatus status;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    @JsonIgnore
    private Activity activity;

    public void setStatus(ActivityStatus status) {
        this.status = status;
    }
}