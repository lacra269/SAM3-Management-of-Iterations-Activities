package com.dam.pms.service.computation;

import com.dam.pms.domain.entity.Activity;
import com.dam.pms.domain.entity.Iteration;
import com.dam.pms.domain.entity.Subtask;
import com.dam.pms.domain.enums.ActivityStatus;
import com.dam.pms.domain.service.computation.ComputationService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComputationServiceTest {

    private final ComputationService cs = new ComputationService();

    @Test
    void activityProgressWithSubtasks() {
        Activity a = new Activity();
        Subtask s1 = new Subtask(); s1.setStatus(ActivityStatus.DONE);
        Subtask s2 = new Subtask(); s2.setStatus(ActivityStatus.IN_PROGRESS);
        a.setSubtasks(List.of(s1, s2));
        double p = cs.calculateActivityProgress(a);
        assertEquals(50.0, p, 0.001);
    }

    @Test
    void iterationProgress() {
        Activity a1 = new Activity(); a1.setSubtasks(List.of(new Subtask())); a1.setStatus(ActivityStatus.DONE);
        Activity a2 = new Activity(); a2.setStatus(ActivityStatus.IN_PROGRESS);
        Iteration it = new Iteration();
        it.setActivities(List.of(a1, a2));
        double p = cs.calculateIterationProgress(it);
        // a1 -> DONE => progress 100, a2 -> IN_PROGRESS -> 40 -> average (100+40)/2=70
        assertEquals(70.0, p, 0.001);
    }
}
