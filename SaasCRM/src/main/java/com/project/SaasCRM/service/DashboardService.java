package com.project.SaasCRM.service;

import java.time.LocalDateTime;
import java.util.Map;

public interface DashboardService {
    Map<String, Object> getDashboardSummary();

    Map<String, Object> getSalesForecast(int months);

    Map<String, Object> getCustomerGrowth(LocalDateTime startDate, LocalDateTime endDate);

    Map<String, Object> getDealPerformance(LocalDateTime startDate, LocalDateTime endDate);

    Map<String, Object> getCustomerOverview();

    Map<String, Object> getTaskOverview(Long userId);

    Map<String, Object> getDealValueByStage();

    Map<String, Object> getDealsWonLostRatio(LocalDateTime startDate, LocalDateTime endDate);

    Map<String, Object> getTopPerformingUsers(int limit);

    Map<String, Object> getUserActivitySummary(Long userId);
}
