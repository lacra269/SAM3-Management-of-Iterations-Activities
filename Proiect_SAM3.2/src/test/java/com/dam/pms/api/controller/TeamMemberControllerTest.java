package com.dam.pms.api.controller;

import com.dam.pms.domain.entity.TeamMember;
import com.dam.pms.domain.enums.MemberRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TeamMemberControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/team-members";
    }

    @Test
    void testCreateAndGetTeamMember() {
        // CREATE
        TeamMember member = new TeamMember();
        member.setName("John Doe");
        member.setEmail("john.doe@example.com");
        member.setRole(MemberRole.DEVELOPER);
        member.setSkills("Java, Spring Boot");
        member.setIsActive(true);

        ResponseEntity<TeamMember> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                member,
                TeamMember.class
        );

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getId());
        assertEquals("John Doe", createResponse.getBody().getName());

        // GET BY ID
        Long memberId = createResponse.getBody().getId();
        ResponseEntity<TeamMember> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + memberId,
                TeamMember.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("John Doe", getResponse.getBody().getName());
    }

    @Test
    void testGetAllMembers() {
        ResponseEntity<TeamMember[]> response = restTemplate.getForEntity(
                getBaseUrl(),
                TeamMember[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testUpdateMember() {
        // Create
        TeamMember member = new TeamMember();
        member.setName("Jane Smith");
        member.setEmail("jane@example.com");
        member.setRole(MemberRole.TESTER);
        member.setIsActive(true);

        ResponseEntity<TeamMember> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                member,
                TeamMember.class
        );

        Long memberId = createResponse.getBody().getId();

        // Update
        TeamMember updated = createResponse.getBody();
        updated.setRole(MemberRole.PROJECT_MANAGER);
        updated.setSkills("Leadership, Planning");

        HttpEntity<TeamMember> requestEntity = new HttpEntity<>(updated);
        ResponseEntity<TeamMember> updateResponse = restTemplate.exchange(
                getBaseUrl() + "/" + memberId,
                HttpMethod.PUT,
                requestEntity,
                TeamMember.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals(MemberRole.PROJECT_MANAGER, updateResponse.getBody().getRole());
    }

    @Test
    void testGetMembersByRole() {
        ResponseEntity<TeamMember[]> response = restTemplate.getForEntity(
                getBaseUrl() + "/role/DEVELOPER",
                TeamMember[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetActiveMembers() {
        ResponseEntity<TeamMember[]> response = restTemplate.getForEntity(
                getBaseUrl() + "/active",
                TeamMember[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testDeleteMember() {
        // Create
        TeamMember member = new TeamMember();
        member.setName("Delete Test");
        member.setEmail("delete@example.com");
        member.setRole(MemberRole.ANALYST);
        member.setIsActive(true);

        ResponseEntity<TeamMember> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                member,
                TeamMember.class
        );

        Long memberId = createResponse.getBody().getId();

        // Delete
        restTemplate.delete(getBaseUrl() + "/" + memberId);





        // Verify
        ResponseEntity<TeamMember> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + memberId,
                TeamMember.class
        );

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }
}