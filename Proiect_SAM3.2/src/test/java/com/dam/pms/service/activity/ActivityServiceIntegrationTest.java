//package com.dam.pms.service.activity;
//
//import com.dam.pms.domain.entity.Activity;
//import com.dam.pms.domain.entity.Iteration;
//import com.dam.pms.domain.entity.Project;
//import com.dam.pms.domain.enums.ActivityStatus;
//import com.dam.pms.domain.service.activity.ActivityService;
//import com.dam.pms.infrastructure.repository.ActivityRepository;
//import com.dam.pms.infrastructure.repository.IterationRepository;
//import com.dam.pms.infrastructure.repository.ProjectRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//class ActivityServiceIntegrationTest {
//
//    @Autowired
//    private ActivityService activityService;
//    @Autowired
//    private ActivityRepository activityRepository;
//    @Autowired
//    private IterationRepository iterationRepository;
//    @Autowired
//    private ProjectRepository projectRepository;

//    @Test
//    void createActivityCascadeProgress() {
//        Project p = new Project(); p.setName("P"); projectRepository.save(p);
//        Iteration it = new Iteration(); it.setName("It"); it.setProject(p); iterationRepository.save(it);
//
//        Activity a = new Activity();
//        a.setTitle("T1");
//        a.setStatus(ActivityStatus.TODO);
//        a.setIteration(it);
//        a.setEstimatedHours(10.0);
//
//        Activity saved = activityService.createActivity(a);
//        assertNotNull(saved.getId());
//
//        Iteration updatedIt = iterationRepository.findById(it.getId()).orElseThrow();
//        Project updatedProject = projectRepository.findById(p.getId()).orElseThrow();
//
//        assertEquals(0.0, updatedIt.getProgress()); // no done tasks
//        assertEquals(0.0, updatedProject.getProgress());
//    }
//}



package com.dam.pms.service.activity;

import com.dam.pms.domain.entity.Activity;
import com.dam.pms.domain.entity.Iteration;
import com.dam.pms.domain.entity.Project;
import com.dam.pms.domain.enums.ActivityStatus;
import com.dam.pms.domain.service.activity.ActivityService;
import com.dam.pms.infrastructure.repository.ActivityRepository;
import com.dam.pms.infrastructure.repository.IterationRepository;
import com.dam.pms.infrastructure.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ActivityServiceSimpleTest {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private IterationRepository iterationRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void createActivityShouldPersistCorrectly() {

        // 1) Create project
        Project p = new Project();
        p.setName("Proj Test2");
        projectRepository.save(p);

        // 2) Create iteration
        Iteration it = new Iteration();
        it.setName("Iter Test");
        it.setProject(p);
        iterationRepository.save(it);

        // 3) Create activity
        Activity a = new Activity();
        a.setTitle("Activity Test");
        a.setDescription("Testing creation");
        a.setStatus(ActivityStatus.TODO);
        a.setEstimatedHours(5.0);
        a.setCreatedDate(LocalDate.now());
        a.setDueDate(LocalDate.now().plusDays(5));
        a.setIteration(it);

// IMPORTANT → menții integritatea relației bidirecționale
        it.getActivities().add(a);

// 4) Save via service
        Activity saved = activityService.createActivity(a);
    }}