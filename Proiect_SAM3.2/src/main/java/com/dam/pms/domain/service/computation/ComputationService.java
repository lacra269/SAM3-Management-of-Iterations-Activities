//package com.dam.pms.domain.service.computation;
//
//import com.dam.pms.domain.entity.Activity;
//import com.dam.pms.domain.entity.Iteration;
//import com.dam.pms.domain.entity.Project;
//import com.dam.pms.domain.entity.Subtask;
//import com.dam.pms.domain.enums.ActivityStatus;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.OptionalDouble;
//
//@Service
//@RequiredArgsConstructor
//public class ComputationService {
//
//    // Activity progress: weighted by subtasks if exist
//    public double calculateActivityProgress(Activity a) {
//        if (a == null) return 0.0;
//        List<Subtask> subs = a.getSubtasks();
//        if (subs != null && !subs.isEmpty()) {
//            double total = subs.size();
//            double done = subs.stream().filter(s -> s.getStatus() == ActivityStatus.DONE).count();
//            return (done / total) * 100.0;
//        } else {
//            // fallback by status mapping
//            return mapStatusToProgress(a.getStatus());
//        }
//    }
//
//    public double calculateIterationProgress(Iteration it) {
//        if (it == null || it.getActivities() == null || it.getActivities().isEmpty()) return 0.0;
//        OptionalDouble avg = it.getActivities().stream()
//                .mapToDouble(this::calculateActivityProgress)
//                .average();
//        return avg.orElse(0.0);
//    }
//
//    public double calculateProjectProgress(Project p) {
//        if (p == null || p.getIterations() == null || p.getIterations().isEmpty()) return 0.0;
//        OptionalDouble avg = p.getIterations().stream()
//                .mapToDouble(this::calculateIterationProgress)
//                .average();
//        return avg.orElse(0.0);
//    }
//
//    public double remainingHoursForIteration(Iteration it) {
//        if (it == null) return 0.0;
//        double estimated = it.getActivities().stream()
//                .mapToDouble(a -> a.getEstimatedHours() == null ? 0.0 : a.getEstimatedHours())
//                .sum();
//        double actual = it.getActivities().stream()
//                .mapToDouble(a -> a.getActualHours() == null ? 0.0 : a.getActualHours())
//                .sum();
//        return Math.max(0.0, estimated - actual);
//    }
//
//    private double mapStatusToProgress(ActivityStatus status) {
//        if (status == null) return 0.0;
//        switch (status) {
//            case TODO: return 0.0;
//            case IN_PROGRESS: return 40.0;
//            case IN_REVIEW: return 75.0;
//            case DONE: return 100.0;
//            case BLOCKED: return 10.0;
//            default: return 0.0;
//        }
//    }
//}



package com.dam.pms.domain.service.computation;

import com.dam.pms.domain.entity.Activity;
import com.dam.pms.domain.entity.Iteration;
import com.dam.pms.domain.entity.Project;
import com.dam.pms.domain.entity.Subtask;
import com.dam.pms.domain.enums.ActivityStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.OptionalDouble;

@Service
@RequiredArgsConstructor
public class ComputationService {

    // --------------------------------------------
    // ACTIVITY PROGRESS
    // --------------------------------------------
    public double calculateActivityProgress(Activity a) {
        if (a == null) return 0.0;

        List<Subtask> subs = a.getSubtasks();
        if (subs != null && !subs.isEmpty()) {

            // verificăm dacă subtasks au status
            boolean hasStatuses = subs.stream()
                    .anyMatch(s -> s.getStatus() != null);

            if (hasStatuses) {
                double total = subs.size();
                double done = subs.stream()
                        .filter(s -> ActivityStatus.DONE.equals(s.getStatus()))
                        .count();
                return (done / total) * 100.0;
            }

            // fallback: subtasks există, dar nu au status → folosim status-ul activității
            return mapStatusToProgress(a.getStatus());
        }

        // fără subtasks → progres în funcție de status
        return mapStatusToProgress(a.getStatus());
    }

    // --------------------------------------------
    // ITERATION PROGRESS
    // --------------------------------------------
    public double calculateIterationProgress(Iteration it) {
        if (it == null || it.getActivities() == null || it.getActivities().isEmpty())
            return 0.0;

        OptionalDouble avg = it.getActivities().stream()
                .mapToDouble(this::calculateActivityProgress)
                .average();

        return avg.orElse(0.0);
    }

    // --------------------------------------------
    // PROJECT PROGRESS
    // --------------------------------------------
    public double calculateProjectProgress(Project p) {
        if (p == null || p.getIterations() == null || p.getIterations().isEmpty())
            return 0.0;

        OptionalDouble avg = p.getIterations().stream()
                .mapToDouble(this::calculateIterationProgress)
                .average();

        return avg.orElse(0.0);
    }

    // --------------------------------------------
    // REMAINING HOURS FOR ITERATION
    // --------------------------------------------
    public double remainingHoursForIteration(Iteration it) {
        if (it == null || it.getActivities() == null) return 0.0;

        double estimated = it.getActivities().stream()
                .mapToDouble(a -> a.getEstimatedHours() == null ? 0.0 : a.getEstimatedHours())
                .sum();

        double actual = it.getActivities().stream()
                .mapToDouble(a -> a.getActualHours() == null ? 0.0 : a.getActualHours())
                .sum();

        return Math.max(0.0, estimated - actual);
    }

    // --------------------------------------------
    // MAP ACTIVITY STATUS TO PERCENTAGE
    // --------------------------------------------
    private double mapStatusToProgress(ActivityStatus status) {
        if (status == null) return 0.0;

        switch (status) {
            case TODO: return 0.0;
            case IN_PROGRESS: return 40.0;
            case IN_REVIEW: return 75.0;
            case DONE: return 100.0;
            case BLOCKED: return 10.0;
            default: return 0.0;
        }
    }
}




