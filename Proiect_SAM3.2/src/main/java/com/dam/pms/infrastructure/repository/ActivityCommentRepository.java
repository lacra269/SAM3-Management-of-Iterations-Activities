package com.dam.pms.infrastructure.repository;

import com.dam.pms.domain.entity.ActivityComment;
import com.dam.pms.domain.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityCommentRepository extends JpaRepository<ActivityComment, Long> {

    List<ActivityComment> findByActivity(Activity activity);
}
