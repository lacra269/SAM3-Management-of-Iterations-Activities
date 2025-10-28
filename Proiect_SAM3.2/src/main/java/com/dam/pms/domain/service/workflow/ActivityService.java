package com.dam.pms.domain.service.workflow;

import com.dam.pms.domain.entity.Activity;
import com.dam.pms.domain.entity.TeamMember;
import com.dam.pms.infrastructure.repository.ActivityRepository;
import com.dam.pms.domain.service.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ValidationService validationService;

    public Activity createActivity(Activity activity) {
        if (activity.getAssignedTo() != null && !validationService.canAssignTask(activity, activity.getAssignedTo())) {
            throw new IllegalArgumentException("Task cannot be assigned to this member");
        }
        return activityRepository.save(activity);
    }

    public List<Activity> getActivitiesByMember(TeamMember member) {
        return activityRepository.findByAssignedTo(member);
    }
}
