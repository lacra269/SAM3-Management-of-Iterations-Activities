package com.dam.pms.domain.service.validation;

import com.dam.pms.domain.entity.Activity;
import com.dam.pms.domain.entity.Subtask;
import com.dam.pms.domain.entity.TeamMember;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

@Service
public class ValidationService {

    public boolean isMemberActive(TeamMember member) {
        return member != null && Boolean.TRUE.equals(member.getIsActive());
    }

    public boolean validActivityDates(Activity a) {
        LocalDate c = a.getCreatedDate();
        LocalDate d = a.getDueDate();
        if (c == null || d == null) return false;
        return !d.isBefore(c);
    }

    public boolean canAssign(Activity activity, TeamMember member) {
        return isMemberActive(member) && validActivityDates(activity);
    }

    public boolean areSubtasksComplete(Activity activity) {
        if (activity.getSubtasks() == null || activity.getSubtasks().isEmpty()) return true;
        return activity.getSubtasks().stream()
                .allMatch(s -> s.getStatus() != null && s.getStatus().name().equals("DONE"));
    }

    public boolean taskHasNoBlockingDependencies(Subtask subtask) {
        // For this simplified model: if subtask belongs to activity, check siblings? placeholder
        return true;
    }

    // Validate aggregate invariants for Activity before persisting
    public void validateActivityAggregate(Activity a) {
        if (!validActivityDates(a)) throw new IllegalArgumentException("Invalid dates for activity");
        if (a.getEstimatedHours() != null && a.getEstimatedHours() < 0) throw new IllegalArgumentException("Estimated hours negative");
    }
}
