package com.dam.pms.infrastructure.repository;

import com.dam.pms.domain.entity.ActivityHistory;
import com.dam.pms.domain.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityHistoryRepository extends JpaRepository<ActivityHistory, Long> {

    List<ActivityHistory> findByActivity(Activity activity);
}
