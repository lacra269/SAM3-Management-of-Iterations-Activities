package com.dam.pms.domain.service.workflow;

import com.dam.pms.domain.entity.Activity;
import com.dam.pms.domain.entity.Subtask;
import com.dam.pms.domain.enums.ActivityStatus;
import org.springframework.stereotype.Service;

@Service
public class WorkflowRulesEngine {

    // Primary guard: can we transition activity -> newStatus ?
    public boolean canTransition(Activity a, ActivityStatus newStatus) {
        if (a == null || newStatus == null) return false;
        ActivityStatus cur = a.getStatus();
        if (cur == newStatus) return true;

        // Example rules:
        switch (newStatus) {
            case IN_PROGRESS:
                // cannot start if already blocked
                return cur != ActivityStatus.BLOCKED;
            case IN_REVIEW:
                return cur == ActivityStatus.IN_PROGRESS || cur == ActivityStatus.TODO;
            case DONE:
                // require subtasks done
                if (a.getSubtasks() != null && !a.getSubtasks().isEmpty()) {
                    return a.getSubtasks().stream().allMatch(s -> s.getStatus() == ActivityStatus.DONE);
                } else {
                    return cur == ActivityStatus.IN_REVIEW || cur == ActivityStatus.TESTING  || cur == ActivityStatus.IN_PROGRESS;
                }
            case BLOCKED:
                return true;
            default:
                return true;
        }
    }

    // Policy: when a subtask status changes, maybe update parent activity
    public ActivityStatus deriveActivityStatusFromSubtasks(Activity activity) {
        if (activity.getSubtasks() == null || activity.getSubtasks().isEmpty()) return activity.getStatus();
        boolean allDone = activity.getSubtasks().stream().allMatch(s -> s.getStatus() == ActivityStatus.DONE);
        boolean anyInProgress = activity.getSubtasks().stream().anyMatch(s -> s.getStatus() == ActivityStatus.IN_PROGRESS);
        if (allDone) return ActivityStatus.DONE;
        if (anyInProgress) return ActivityStatus.IN_PROGRESS;
        return activity.getStatus();
    }
}
