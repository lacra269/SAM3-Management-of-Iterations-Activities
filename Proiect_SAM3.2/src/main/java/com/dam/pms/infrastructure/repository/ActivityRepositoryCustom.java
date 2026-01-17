package com.dam.pms.infrastructure.repository;

import com.dam.pms.domain.entity.Activity;
import java.util.List;

public interface ActivityRepositoryCustom {
    List<Activity> findByTitleLike(String titleFragment);
}
