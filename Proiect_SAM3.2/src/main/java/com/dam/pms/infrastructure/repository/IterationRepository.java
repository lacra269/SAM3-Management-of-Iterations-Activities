package com.dam.pms.infrastructure.repository;

import com.dam.pms.domain.entity.Iteration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IterationRepository extends JpaRepository<Iteration, Long> {
}
