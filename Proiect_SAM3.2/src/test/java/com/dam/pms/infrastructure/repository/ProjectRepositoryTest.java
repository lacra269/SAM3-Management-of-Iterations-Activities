package com.dam.pms.infrastructure.repository;
import com.dam.pms.domain.entity.Project;
import com.dam.pms.domain.enums.ProjectStatus;
import com.dam.pms.infrastructure.repository.ProjectRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void testCreateReadUpdateDeleteProject() {

        // CREATE
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Description test");
        project.setStatus(ProjectStatus.ACTIVE);

        Project saved = projectRepository.save(project);
        Assertions.assertNotNull(saved.getId());


        // READ
        Project found = projectRepository.findById(saved.getId()).orElse(null);
        Assertions.assertNotNull(found);
        Assertions.assertEquals("Test Project", found.getName());


        // UPDATE
        found.setStatus(ProjectStatus.ON_HOLD);
        projectRepository.save(found);

        Project updated = projectRepository.findById(saved.getId()).orElse(null);
        Assertions.assertEquals(ProjectStatus.ON_HOLD, updated.getStatus());


        // DELETE
        projectRepository.delete(updated);

        boolean exists = projectRepository.existsById(saved.getId());
        Assertions.assertFalse(exists);
    }
}