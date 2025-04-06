package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.CustomerStatus;
import com.project.SaasCRM.domain.DealStage;
import com.project.SaasCRM.domain.TaskStatus;
import com.project.SaasCRM.domain.dto.AuditLogDTO;
import com.project.SaasCRM.domain.dto.DashboardDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface DashboardService {
    DashboardDTO getDashboardSummary();
    
    Map<CustomerStatus, Long> getCustomerStatusDistribution();
    
    Map<DealStage, BigDecimal> getDealValuesByStage();
    
    Map<DealStage, Long> getDealCountsByStage();
    
    Map<TaskStatus, Long> getTaskStatusDistribution();
    
    List<AuditLogDTO> getRecentActivities(int limit);
    
    Map<String, BigDecimal> getSalesPerformance(LocalDateTime startDate, LocalDateTime endDate);
    
    Map<String, Long> getTasksDueDistribution(int nextDays);
    
    Map<String, Object> getUserPerformanceMetrics(Long userId);
    
    Map<String, Object> getConversionRates();
    
    Map<String, Object> getRevenueMetrics(LocalDateTime startDate, LocalDateTime endDate);

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
