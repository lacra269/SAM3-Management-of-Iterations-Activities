package com.dam.pms.service.validation;

import com.dam.pms.domain.entity.Activity;
import com.dam.pms.domain.entity.TeamMember;
import com.dam.pms.domain.service.validation.ValidationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ValidationServiceTest {

    @Autowired
    private ValidationService validationService;

    @Test
    void testIsMemberActive() {
        TeamMember member = new TeamMember();
        member.setIsActive(true);
        assertTrue(validationService.isMemberActive(member));
    }

    @Test
    void testValidTaskDates() {
        Activity task = new Activity();
        task.setCreatedDate(LocalDate.of(2025, 10, 1));
        task.setDueDate(LocalDate.of(2025, 10, 5));
        assertTrue(validationService.isValidTaskDates(task));
    }
}
