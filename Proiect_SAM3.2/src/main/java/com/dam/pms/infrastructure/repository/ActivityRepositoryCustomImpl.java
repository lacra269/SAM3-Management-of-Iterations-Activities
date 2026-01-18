package com.dam.pms.infrastructure.repository;

import com.dam.pms.domain.entity.Activity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ActivityRepositoryCustomImpl implements ActivityRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Activity> findByTitleLike(String titleFragment) {
        String jpql = "SELECT a FROM Activity a WHERE a.title LIKE :fragment";
        TypedQuery<Activity> query = entityManager.createQuery(jpql, Activity.class);
        query.setParameter("fragment", "%" + titleFragment + "%");
        return query.getResultList();
    }

    // Metodă publică pentru Vaadin UI
    public List<Activity> findByIterationIdWithSubtasks(Long iterationId) {
        String jpql = "SELECT DISTINCT a FROM Activity a " +
                "LEFT JOIN FETCH a.subtasks " +
                "WHERE a.iteration.id = :iterationId";
        TypedQuery<Activity> query = entityManager.createQuery(jpql, Activity.class);
        query.setParameter("iterationId", iterationId);
        return query.getResultList();
    }
}