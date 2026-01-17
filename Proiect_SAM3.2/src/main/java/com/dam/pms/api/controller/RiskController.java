package com.dam.pms.api.controller;

import com.dam.pms.domain.entity.Risk;
import com.dam.pms.domain.service.risk.RiskService;
import com.dam.pms.infrastructure.repository.ProjectRepository;
import com.dam.pms.infrastructure.repository.RiskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/risks")
@RequiredArgsConstructor
public class RiskController {

    private final RiskRepository riskRepository;
    private final RiskService riskService;
    private final ProjectRepository projectRepository;

    @GetMapping
    public ResponseEntity<List<Risk>> getAllRisks() {
        return ResponseEntity.ok(riskRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Risk> getRiskById(@PathVariable Long id) {
        return riskRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Risk>> getRisksByProject(@PathVariable Long projectId) {
        return projectRepository.findById(projectId)
                .map(project -> ResponseEntity.ok(riskService.findByProject(project)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/unresolved")
    public ResponseEntity<List<Risk>> getUnresolvedRisks() {
        return ResponseEntity.ok(riskRepository.findByResolved(false));
    }

    @PostMapping
    public ResponseEntity<Risk> createRisk(@RequestBody Risk risk) {
        Risk created = riskService.create(risk);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<Risk> resolveRisk(@PathVariable Long id) {
        Risk resolved = riskService.resolve(id);
        return ResponseEntity.ok(resolved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRisk(@PathVariable Long id) {
        if (!riskRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        riskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}