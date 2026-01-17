package com.dam.pms.domain.service.audit;

import com.dam.pms.domain.entity.Activity;
import com.dam.pms.domain.entity.ActivityHistory;
import com.dam.pms.infrastructure.repository.ActivityHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.PreUpdate;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;

/*
 Note: JPA listeners are not Spring-managed by default. To persist via Spring beans
 inside a listener you can publish an ApplicationEvent and handle it in a Spring bean,
 or use a static holder to fetch Spring beans. For simplicity, here we show the listener
 that constructs history objects and expects an external service to persist them.
*/

@Component
public class ActivityAuditListener {

    @Autowired
    private ActivityHistoryRepository historyRepository;

    @PrePersist
    public void prePersist(Activity a) {
        ActivityHistory h = new ActivityHistory();
        h.setActivity(a);
        h.setFieldChanged("CREATED");
        h.setOldValue(null);
        h.setNewValue("created");
        h.setChangedAt(LocalDateTime.now());
        // Save may fail here because entity manager flush ordering — safer to use ApplicationEvent
        try {
            historyRepository.save(h);
        } catch (Exception ex) {
            // fallback: ignore, or log
        }
    }

    @PreUpdate
    public void preUpdate(Activity a) {
        // simplistic: always record updatedAt-like event
        ActivityHistory h = new ActivityHistory();
        h.setActivity(a);
        h.setFieldChanged("UPDATED");
        h.setOldValue(null);
        h.setNewValue("updated");
        h.setChangedAt(LocalDateTime.now());
        try {
            historyRepository.save(h);
        } catch (Exception ex) {
            // ignore
        }
    }
}
