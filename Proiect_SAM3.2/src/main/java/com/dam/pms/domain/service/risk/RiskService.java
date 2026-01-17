package com.dam.pms.domain.service.risk;

import com.dam.pms.domain.entity.Project;
import com.dam.pms.domain.entity.Risk;
import com.dam.pms.infrastructure.repository.RiskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskService {

    private final RiskRepository riskRepository;

    @Transactional
    public Risk create(Risk r) {
        return riskRepository.save(r);
    }

    public List<Risk> findByProject(Project p) {
        return riskRepository.findByProject(p);
    }

    @Transactional
    public Risk resolve(Long riskId) {
        Risk r = riskRepository.findById(riskId).orElseThrow();
        r.setResolved(true);
        return riskRepository.save(r);
    }
}
