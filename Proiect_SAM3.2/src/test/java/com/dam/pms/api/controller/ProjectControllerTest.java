package com.dam.pms.api.controller;

import com.dam.pms.domain.entity.Project;
import com.dam.pms.domain.enums.ProjectStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProjectControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/projects";
    }

    @Test
    void testCreateAndGetProject() {
        // CREATE
        Project project = new Project();
        project.setName("REST Test Project");
        project.setDescription("Testing REST API");
        project.setStatus(ProjectStatus.ACTIVE);

        ResponseEntity<Project> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                project,
                Project.class
        );

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getId());
        assertEquals("REST Test Project", createResponse.getBody().getName());

        // GET BY ID
        Long projectId = createResponse.getBody().getId();
        ResponseEntity<Project> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + projectId,
                Project.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals("REST Test Project", getResponse.getBody().getName());
    }

    @Test
    void testGetAllProjects() {
        ResponseEntity<Project[]> response = restTemplate.getForEntity(
                getBaseUrl(),
                Project[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testUpdateProject() {
        // Create first
        Project project = new Project();
        project.setName("Update Test");
        project.setStatus(ProjectStatus.ACTIVE);

        ResponseEntity<Project> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                project,
                Project.class
        );

        Long projectId = createResponse.getBody().getId();

        // Update
        Project updated = createResponse.getBody();
        updated.setStatus(ProjectStatus.ON_HOLD);

        HttpEntity<Project> requestEntity = new HttpEntity<>(updated);
        ResponseEntity<Project> updateResponse = restTemplate.exchange(
                getBaseUrl() + "/" + projectId,
                HttpMethod.PUT,
                requestEntity,
                Project.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals(ProjectStatus.ON_HOLD, updateResponse.getBody().getStatus());
    }

    @Test
    void testDeleteProject() {
        // Create first
        Project project = new Project();
        project.setName("Delete Test");
        project.setStatus(ProjectStatus.ACTIVE);

        ResponseEntity<Project> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                project,
                Project.class
        );

        Long projectId = createResponse.getBody().getId();

        // Delete
        restTemplate.delete(getBaseUrl() + "/" + projectId);

        // Verify deletion
        ResponseEntity<Project> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + projectId,
                Project.class
        );

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void testGetProjectsByStatus() {
        ResponseEntity<Project[]> response = restTemplate.getForEntity(
                getBaseUrl() + "/status/ACTIVE",
                Project[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetProjectProgress() {
        // Create project
        Project project = new Project();
        project.setName("Progress Test");
        project.setStatus(ProjectStatus.ACTIVE);

        ResponseEntity<Project> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                project,
                Project.class
        );

        Long projectId = createResponse.getBody().getId();

        // Get progress
        ResponseEntity<Double> progressResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + projectId + "/progress",
                Double.class
        );

        assertEquals(HttpStatus.OK, progressResponse.getStatusCode());
        assertNotNull(progressResponse.getBody());
    }
}