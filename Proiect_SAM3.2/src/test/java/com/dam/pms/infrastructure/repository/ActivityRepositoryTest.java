package com.dam.pms.infrastructure.repository;


import com.dam.pms.domain.entity.Activity;
import com.dam.pms.domain.enums.ActivityStatus;
import com.dam.pms.infrastructure.repository.ActivityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ActivityRepositoryTest {

    @Autowired
    private ActivityRepository activityRepository;

    @Test
    void testCRUDOperations() {

        // CREATE
        Activity a = new Activity();
        a.setTitle("Test Activity");
        a.setDescription("Description ABC");
        a.setStatus(ActivityStatus.TODO);

        Activity saved = activityRepository.save(a);

        assertNotNull(saved.getId());
        assertEquals("Test Activity", saved.getTitle());

        // READ
        Optional<Activity> found = activityRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Test Activity", found.get().getTitle());

        // UPDATE
        Activity toUpdate = found.get();
        toUpdate.setTitle("Updated Title");
        activityRepository.save(toUpdate);

        Optional<Activity> updated = activityRepository.findById(saved.getId());
        assertTrue(updated.isPresent());
        assertEquals("Updated Title", updated.get().getTitle());

        // DELETE
        activityRepository.delete(updated.get());
        boolean stillExists = activityRepository.existsById(saved.getId());

        assertFalse(stillExists);
    }
}
