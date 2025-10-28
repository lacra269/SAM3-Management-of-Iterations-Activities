package com.dam.pms.infrastructure.repository;

import com.dam.pms.domain.entity.ActivityComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityCommentRepository extends JpaRepository<ActivityComment, Long> {
}
