package com.dam.pms.config;

import com.dam.pms.ui.service.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ServiceLocator implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ServiceLocator.context = applicationContext;
    }

    public static ProjectUIService getProjectUIService() {
        return context.getBean(ProjectUIService.class);
    }

    public static TeamMemberUIService getTeamMemberUIService() {
        return context.getBean(TeamMemberUIService.class);
    }

    public static RiskUIService getRiskUIService() {
        return context.getBean(RiskUIService.class);
    }

    public static SubtaskUIService getTaskUIService() {
        return context.getBean(SubtaskUIService.class);
    }
    public static SubtaskUIService getSubtaskUIService() {
        return context.getBean(SubtaskUIService.class);
    }
    public static ActivityUIService getActivityUIService() {
        return context.getBean(ActivityUIService.class);
    }

}