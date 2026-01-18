package com.dam.pms.ui.service;

import com.dam.pms.domain.entity.Project;
import com.dam.pms.domain.entity.Risk;
import com.dam.pms.domain.service.risk.RiskService;
import com.dam.pms.infrastructure.repository.RiskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RiskUIService {

    private final RiskRepository riskRepository;
    private final RiskService riskService;

    public List<Risk> findAll() {
        return riskRepository.findAll();
    }

    public Optional<Risk> findById(Long id) {
        return riskRepository.findById(id);
    }

    public List<Risk> findByProject(Project project) {
        return riskService.findByProject(project);
    }

    public List<Risk> findUnresolved() {
        return riskRepository.findByResolved(false);
    }

    public Risk save(Risk risk) {
        return riskService.create(risk);
    }

    public Risk resolve(Long id) {
        return riskService.resolve(id);
    }

    public void delete(Long id) {
        riskRepository.deleteById(id);
    }
}