package com.dam.pms.domain.service.validation;

import com.dam.pms.domain.entity.Activity;
import com.dam.pms.domain.entity.TeamMember;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ValidationService {

    // Verifică dacă un membru este activ
    public boolean isMemberActive(TeamMember member) {
        return member.getIsActive() != null && member.getIsActive();
    }

    // Verifică dacă datele unui task sunt valide (dueDate >= createdDate)
    public boolean isValidTaskDates(Activity activity) {
        LocalDate created = activity.getCreatedDate();
        LocalDate due = activity.getDueDate();

        if (created == null || due == null) {
            return false; // date invalide
        }

        // dueDate trebuie să fie >= createdDate
        return due.isAfter(created) || due.isEqual(created);
    }

    // Verifică dacă task-ul poate fi atribuit unui membru
    public boolean canAssignTask(Activity activity, TeamMember member) {
        return isMemberActive(member) && isValidTaskDates(activity);
    }
}
