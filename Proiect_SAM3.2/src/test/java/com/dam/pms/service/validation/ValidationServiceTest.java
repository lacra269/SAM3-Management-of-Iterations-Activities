package com.dam.pms.service.validation;

import com.dam.pms.domain.entity.Activity;
import com.dam.pms.domain.entity.TeamMember;
import com.dam.pms.domain.service.validation.ValidationService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {

    private final ValidationService vs = new ValidationService();

    @Test
    void testValidDatesAndMemberActive() {
        Activity a = new Activity();
        a.setCreatedDate(LocalDate.of(2025,10,1));
        a.setDueDate(LocalDate.of(2025,10,5));

        TeamMember m = new TeamMember();
        m.setIsActive(true);

        assertTrue(vs.validActivityDates(a));
        assertTrue(vs.canAssign(a, m));
    }

    @Test
    void invalidDates() {
        Activity a = new Activity();
        a.setCreatedDate(LocalDate.of(2025,10,5));
        a.setDueDate(LocalDate.of(2025,10,1));
        assertFalse(vs.validActivityDates(a));
    }
}
