package com.dam.pms.domain.service.workflow;

import com.dam.pms.domain.entity.Activity;
import com.dam.pms.domain.entity.TeamMember;
import com.dam.pms.domain.enums.ActivityStatus;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {

    public void assignTask(Activity activity, TeamMember member) {
        activity.setAssignedTo(member);
        activity.setStatus(ActivityStatus.TODO);
        // aici poți adăuga notificări sau log
    }

    public void updateStatus(Activity activity, ActivityStatus status) {
        activity.setStatus(status);
        // dacă status = DONE, se poate actualiza progres Iteration / Project
    }
}
