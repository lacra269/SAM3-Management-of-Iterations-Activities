package com.dam.pms.infrastructure.repository.jpa;

import com.dam.pms.domain.entity.Activity;
import com.dam.pms.infrastructure.repository.ActivityRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ActivityRepositoryJPA implements ActivityRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Activity> findByTitleLike(String titleFragment) {
        TypedQuery<Activity> q = em.createQuery("SELECT a FROM Activity a WHERE LOWER(a.title) LIKE LOWER(:t)", Activity.class);
        q.setParameter("t", "%" + titleFragment + "%");
        return q.getResultList();
    }
}
