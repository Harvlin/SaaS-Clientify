package com.project.SaasCRM.service.impl;

import com.project.SaasCRM.domain.CustomerStatus;
import com.project.SaasCRM.domain.DealStage;
import com.project.SaasCRM.domain.TaskStatus;
import com.project.SaasCRM.domain.entity.Customer;
import com.project.SaasCRM.domain.entity.Deal;
import com.project.SaasCRM.domain.entity.Task;
import com.project.SaasCRM.domain.entity.User;
import com.project.SaasCRM.domain.dto.AuditLogDTO;
import com.project.SaasCRM.domain.dto.DashboardDTO;
import com.project.SaasCRM.repository.CustomerRepository;
import com.project.SaasCRM.repository.DealRepository;
import com.project.SaasCRM.repository.TaskRepository;
import com.project.SaasCRM.repository.UserRepository;
import com.project.SaasCRM.service.AuditLogService;
import com.project.SaasCRM.service.DashboardService;
import com.project.SaasCRM.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final CustomerRepository customerRepository;
    private final DealRepository dealRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final DashboardMapper dashboardMapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "dashboardSummary", unless = "#result == null")
    public DashboardDTO getDashboardSummary() {
        try {
            Long totalCustomers = customerRepository.count();
            Map<CustomerStatus, Long> customersByStatus = getCustomerStatusDistribution();
            Map<DealStage, Long> dealsByStage = getDealCountsByStage();
            Map<DealStage, BigDecimal> dealValuesByStage = getDealValuesByStage();
            Map<TaskStatus, Long> tasksByStatus = getTaskStatusDistribution();
            List<AuditLogDTO> recentActivities = getRecentActivities(5);

            return dashboardMapper.toDto(
                totalCustomers,
                customersByStatus,
                dealsByStage,
                dealValuesByStage,
                tasksByStatus,
                recentActivities
            );
        } catch (Exception e) {
            log.error("Error getting dashboard summary", e);
            throw new RuntimeException("Failed to get dashboard summary", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "customerStatusDistribution", unless = "#result == null")
    public Map<CustomerStatus, Long> getCustomerStatusDistribution() {
        try {
            Map<CustomerStatus, Long> distribution = new EnumMap<>(CustomerStatus.class);
            for (CustomerStatus status : CustomerStatus.values()) {
                distribution.put(status, customerRepository.countByStatus(status));
            }
            return distribution;
        } catch (Exception e) {
            log.error("Error getting customer status distribution", e);
            throw new RuntimeException("Failed to get customer status distribution", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "dealValuesByStage", unless = "#result == null")
    public Map<DealStage, BigDecimal> getDealValuesByStage() {
        try {
            Map<DealStage, BigDecimal> valuesByStage = new EnumMap<>(DealStage.class);
            for (DealStage stage : DealStage.values()) {
                BigDecimal value = dealRepository.calculateTotalValueByStage(stage);
                valuesByStage.put(stage, value != null ? value : BigDecimal.ZERO);
            }
            return valuesByStage;
        } catch (Exception e) {
            log.error("Error getting deal values by stage", e);
            throw new RuntimeException("Failed to get deal values by stage", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "dealCountsByStage", unless = "#result == null")
    public Map<DealStage, Long> getDealCountsByStage() {
        try {
            Map<DealStage, Long> countsByStage = new EnumMap<>(DealStage.class);
            for (DealStage stage : DealStage.values()) {
                countsByStage.put(stage, dealRepository.countByStage(stage));
            }
            return countsByStage;
        } catch (Exception e) {
            log.error("Error getting deal counts by stage", e);
            throw new RuntimeException("Failed to get deal counts by stage", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "taskStatusDistribution", unless = "#result == null")
    public Map<TaskStatus, Long> getTaskStatusDistribution() {
        try {
            Map<TaskStatus, Long> distribution = new EnumMap<>(TaskStatus.class);
            for (TaskStatus status : TaskStatus.values()) {
                distribution.put(status, taskRepository.countByStatus(status));
            }
            return distribution;
        } catch (Exception e) {
            log.error("Error getting task status distribution", e);
            throw new RuntimeException("Failed to get task status distribution", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "recentActivities", key = "#limit", unless = "#result == null")
    public List<AuditLogDTO> getRecentActivities(int limit) {
        try {
            return auditLogService.getRecentActivities(limit);
        } catch (Exception e) {
            log.error("Error getting recent activities", e);
            throw new RuntimeException("Failed to get recent activities", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "salesPerformance", key = "{#startDate, #endDate}", unless = "#result == null")
    public Map<String, BigDecimal> getSalesPerformance(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            Map<String, BigDecimal> performance = new HashMap<>();
            List<Deal> deals = dealRepository.findByCreatedAtBetween(startDate, endDate);
            
            BigDecimal totalValue = deals.stream()
                .map(Deal::getValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
            performance.put("totalRevenue", totalValue);
            
            long totalDeals = deals.size();
            long wonDeals = deals.stream()
                .filter(deal -> deal.getStage() == DealStage.CLOSED_WON)
                .count();
                
            performance.put("winRate", totalDeals > 0 ? 
                BigDecimal.valueOf((double) wonDeals / totalDeals * 100) : 
                BigDecimal.ZERO);
                
            return performance;
        } catch (Exception e) {
            log.error("Error getting sales performance", e);
            throw new RuntimeException("Failed to get sales performance", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "tasksDueDistribution", key = "#nextDays", unless = "#result == null")
    public Map<String, Long> getTasksDueDistribution(int nextDays) {
        try {
            LocalDateTime endDate = LocalDateTime.now().plusDays(nextDays);
            Map<String, Long> distribution = new HashMap<>();
            
            for (TaskStatus status : TaskStatus.values()) {
                Long count = taskRepository.countByStatusAndDueDateBefore(status, endDate);
                distribution.put(status.name(), count);
            }
            
            return distribution;
        } catch (Exception e) {
            log.error("Error getting tasks due distribution", e);
            throw new RuntimeException("Failed to get tasks due distribution", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userPerformanceMetrics", key = "#userId", unless = "#result == null")
    public Map<String, Object> getUserPerformanceMetrics(Long userId) {
        try {
            Map<String, Object> metrics = new HashMap<>();
            
            // Deal metrics
            List<Deal> userDeals = dealRepository.findByAssignedUser_Id(userId);
            long totalDeals = userDeals.size();
            long wonDeals = userDeals.stream()
                .filter(deal -> deal.getStage() == DealStage.CLOSED_WON)
                .count();
                
            metrics.put("totalDeals", totalDeals);
            metrics.put("wonDeals", wonDeals);
            metrics.put("winRate", totalDeals > 0 ? (double) wonDeals / totalDeals * 100 : 0.0);
            
            // Task metrics
            long totalTasks = taskRepository.countByAssignedUser_Id(userId);
            long completedTasks = taskRepository.countByAssignedUser_IdAndStatus(userId, TaskStatus.COMPLETED);
            
            metrics.put("totalTasks", totalTasks);
            metrics.put("completedTasks", completedTasks);
            metrics.put("completionRate", totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0.0);
            
            return metrics;
        } catch (Exception e) {
            log.error("Error getting user performance metrics", e);
            throw new RuntimeException("Failed to get user performance metrics", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "conversionRates", unless = "#result == null")
    public Map<String, Object> getConversionRates() {
        try {
            Map<String, Object> rates = new HashMap<>();
            
            List<Deal> allDeals = dealRepository.findAll();
            long totalDeals = allDeals.size();
            
            if (totalDeals > 0) {
                rates.put("proposalToNegotiation", calculateStageConversion(allDeals, DealStage.PROPOSAL, DealStage.NEGOTIATION));
                rates.put("negotiationToClosing", calculateStageConversion(allDeals, DealStage.NEGOTIATION, DealStage.CLOSED_WON));
                rates.put("closingToWon", calculateStageConversion(allDeals, DealStage.CLOSED_WON, DealStage.CLOSED_WON));
                rates.put("overallWinRate", calculateWinRate(allDeals));
            }
            
            return rates;
        } catch (Exception e) {
            log.error("Error getting conversion rates", e);
            throw new RuntimeException("Failed to get conversion rates", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "revenueMetrics", key = "{#startDate, #endDate}", unless = "#result == null")
    public Map<String, Object> getRevenueMetrics(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            Map<String, Object> metrics = new HashMap<>();
            
            List<Deal> deals = dealRepository.findByCreatedAtBetween(startDate, endDate);
            
            BigDecimal totalRevenue = deals.stream()
                .filter(deal -> deal.getStage() == DealStage.CLOSED_WON)
                .map(Deal::getValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
            long wonDealsCount = deals.stream()
                .filter(deal -> deal.getStage() == DealStage.CLOSED_WON)
                .count();
                
            BigDecimal averageDealValue = wonDealsCount > 0 ? 
                totalRevenue.divide(BigDecimal.valueOf(wonDealsCount), 2, BigDecimal.ROUND_HALF_UP) : 
                BigDecimal.ZERO;
                
            metrics.put("totalRevenue", totalRevenue);
            metrics.put("averageDealValue", averageDealValue);
            metrics.put("dealsClosed", wonDealsCount);
            
            return metrics;
        } catch (Exception e) {
            log.error("Error getting revenue metrics", e);
            throw new RuntimeException("Failed to get revenue metrics", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "salesForecast", key = "#months", unless = "#result == null")
    public Map<String, Object> getSalesForecast(int months) {
        try {
            Map<String, Object> forecast = new HashMap<>();
            
            LocalDateTime endDate = LocalDateTime.now().plusMonths(months);
            List<Deal> activeDeals = dealRepository.findByStageNotIn(
                Arrays.asList(DealStage.CLOSED_WON, DealStage.CLOSED_LOST)
            );
            
            BigDecimal pipelineValue = activeDeals.stream()
                .map(Deal::getValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
            double historicalWinRate = calculateWinRate(dealRepository.findAll());
            BigDecimal forecastedRevenue = pipelineValue.multiply(BigDecimal.valueOf(historicalWinRate / 100));
            
            forecast.put("pipelineValue", pipelineValue);
            forecast.put("forecastedRevenue", forecastedRevenue);
            forecast.put("historicalWinRate", historicalWinRate);
            
            return forecast;
        } catch (Exception e) {
            log.error("Error getting sales forecast", e);
            throw new RuntimeException("Failed to get sales forecast", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "customerGrowth", key = "{#startDate, #endDate}", unless = "#result == null")
    public Map<String, Object> getCustomerGrowth(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            Map<String, Object> growth = new HashMap<>();
            
            long newCustomers = customerRepository.countByCreatedAtBetween(startDate, endDate);
            long lostCustomers = customerRepository.countByStatusAndUpdatedAtBetween(
                CustomerStatus.INACTIVE, startDate, endDate);
                
            growth.put("newCustomers", newCustomers);
            growth.put("lostCustomers", lostCustomers);
            growth.put("netGrowth", newCustomers - lostCustomers);
            
            return growth;
        } catch (Exception e) {
            log.error("Error getting customer growth", e);
            throw new RuntimeException("Failed to get customer growth", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "dealPerformance", key = "{#startDate, #endDate}", unless = "#result == null")
    public Map<String, Object> getDealPerformance(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            Map<String, Object> performance = new HashMap<>();
            
            List<Deal> deals = dealRepository.findByCreatedAtBetween(startDate, endDate);
            
            performance.put("totalDeals", deals.size());
            performance.put("wonDeals", deals.stream()
                .filter(deal -> deal.getStage() == DealStage.CLOSED_WON)
                .count());
            performance.put("lostDeals", deals.stream()
                .filter(deal -> deal.getStage() == DealStage.CLOSED_LOST)
                .count());
            performance.put("averageDealCycle", calculateAverageDealCycle(deals));
            
            return performance;
        } catch (Exception e) {
            log.error("Error getting deal performance", e);
            throw new RuntimeException("Failed to get deal performance", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "customerOverview", unless = "#result == null")
    public Map<String, Object> getCustomerOverview() {
        try {
            Map<String, Object> overview = new HashMap<>();
            
            long totalCustomers = customerRepository.count();
            long activeCustomers = customerRepository.countByStatus(CustomerStatus.ACTIVE);
            
            overview.put("totalCustomers", totalCustomers);
            overview.put("activeCustomers", activeCustomers);
            overview.put("averageDealsPerCustomer", calculateAverageDealsPerCustomer());
            
            return overview;
        } catch (Exception e) {
            log.error("Error getting customer overview", e);
            throw new RuntimeException("Failed to get customer overview", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "taskOverview", key = "#userId", unless = "#result == null")
    public Map<String, Object> getTaskOverview(Long userId) {
        try {
            Map<String, Object> overview = new HashMap<>();
            
            long totalTasks = taskRepository.countByAssignedUser_Id(userId);
            long completedTasks = taskRepository.countByAssignedUser_IdAndStatus(userId, TaskStatus.COMPLETED);
            long overdueTasks = taskRepository.countByAssignedUser_IdAndDueDateBefore(userId, LocalDateTime.now());
            
            overview.put("totalTasks", totalTasks);
            overview.put("completedTasks", completedTasks);
            overview.put("overdueTasks", overdueTasks);
            
            return overview;
        } catch (Exception e) {
            log.error("Error getting task overview", e);
            throw new RuntimeException("Failed to get task overview", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "dealValueByStage", unless = "#result == null")
    public Map<String, Object> getDealValueByStage() {
        try {
            Map<String, Object> values = new HashMap<>();
            
            for (DealStage stage : DealStage.values()) {
                BigDecimal value = dealRepository.calculateTotalValueByStage(stage);
                values.put(stage.name(), value != null ? value : BigDecimal.ZERO);
            }
            
            return values;
        } catch (Exception e) {
            log.error("Error getting deal value by stage", e);
            throw new RuntimeException("Failed to get deal value by stage", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "dealsWonLostRatio", key = "{#startDate, #endDate}", unless = "#result == null")
    public Map<String, Object> getDealsWonLostRatio(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            Map<String, Object> ratio = new HashMap<>();
            
            List<Deal> deals = dealRepository.findByCreatedAtBetween(startDate, endDate);
            long wonDeals = deals.stream()
                .filter(deal -> deal.getStage() == DealStage.CLOSED_WON)
                .count();
            long lostDeals = deals.stream()
                .filter(deal -> deal.getStage() == DealStage.CLOSED_LOST)
                .count();
            
            ratio.put("won", wonDeals);
            ratio.put("lost", lostDeals);
            ratio.put("ratio", wonDeals > 0 ? (double) wonDeals / (wonDeals + lostDeals) * 100 : 0.0);
            
            return ratio;
        } catch (Exception e) {
            log.error("Error getting deals won/lost ratio", e);
            throw new RuntimeException("Failed to get deals won/lost ratio", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "topPerformingUsers", key = "#limit", unless = "#result == null")
    public Map<String, Object> getTopPerformingUsers(int limit) {
        try {
            Map<String, Object> performance = new HashMap<>();
            
            List<Object[]> results = dealRepository.findTopPerformingUsers(
                DealStage.CLOSED_WON,
                PageRequest.of(0, limit)
            );
            
            for (Object[] result : results) {
                User user = (User) result[0];
                Long wonDeals = (Long) result[1];
                BigDecimal totalValue = (BigDecimal) result[2];
                
                Map<String, Object> userStats = new HashMap<>();
                userStats.put("wonDeals", wonDeals);
                userStats.put("totalValue", totalValue);
                
                performance.put(user.getUsername(), userStats);
            }
            
            return performance;
        } catch (Exception e) {
            log.error("Error getting top performing users", e);
            throw new RuntimeException("Failed to get top performing users", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userActivitySummary", key = "#userId", unless = "#result == null")
    public Map<String, Object> getUserActivitySummary(Long userId) {
        try {
            Map<String, Object> summary = new HashMap<>();
            
            // Deal activity
            List<Deal> userDeals = dealRepository.findByAssignedUser_Id(userId);
            summary.put("totalDeals", userDeals.size());
            summary.put("activeDeals", userDeals.stream()
                .filter(deal -> deal.getStage() != DealStage.CLOSED_WON && deal.getStage() != DealStage.CLOSED_LOST)
                .count());
                
            // Task activity
            long totalTasks = taskRepository.countByAssignedUser_Id(userId);
            long completedTasks = taskRepository.countByAssignedUser_IdAndStatus(userId, TaskStatus.COMPLETED);
            
            summary.put("totalTasks", totalTasks);
            summary.put("completedTasks", completedTasks);
            summary.put("completionRate", totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0.0);
            
            return summary;
        } catch (Exception e) {
            log.error("Error getting user activity summary", e);
            throw new RuntimeException("Failed to get user activity summary", e);
        }
    }

    private double calculateAverageDealsPerCustomer() {
        long totalCustomers = customerRepository.count();
        long totalDeals = dealRepository.count();
        return totalCustomers > 0 ? (double) totalDeals / totalCustomers : 0.0;
    }

    private double calculateStageConversion(List<Deal> deals, DealStage fromStage, DealStage toStage) {
        long fromCount = deals.stream()
            .filter(deal -> deal.getStage() == fromStage)
            .count();
            
        long toCount = deals.stream()
            .filter(deal -> deal.getStage() == toStage)
            .count();
            
        return fromCount > 0 ? (double) toCount / fromCount * 100 : 0.0;
    }

    private double calculateWinRate(List<Deal> deals) {
        long totalDeals = deals.size();
        long wonDeals = deals.stream()
            .filter(deal -> deal.getStage() == DealStage.CLOSED_WON)
            .count();
            
        return totalDeals > 0 ? (double) wonDeals / totalDeals * 100 : 0.0;
    }

    private double calculateAverageDealCycle(List<Deal> deals) {
        return deals.stream()
            .filter(deal -> deal.getStage() == DealStage.CLOSED_WON)
            .mapToLong(deal -> deal.getUpdatedAt().toEpochSecond(java.time.ZoneOffset.UTC) - 
                              deal.getCreatedAt().toEpochSecond(java.time.ZoneOffset.UTC))
            .average()
            .orElse(0.0) / (24 * 60 * 60); // Convert to days
    }
} 