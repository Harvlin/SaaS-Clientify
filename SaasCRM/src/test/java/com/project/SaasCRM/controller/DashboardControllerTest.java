package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.CustomerStatus;
import com.project.SaasCRM.domain.DealStage;
import com.project.SaasCRM.domain.TaskStatus;
import com.project.SaasCRM.domain.dto.AuditLogDTO;
import com.project.SaasCRM.domain.dto.DashboardDTO;
import com.project.SaasCRM.exception.UnauthorizedException;
import com.project.SaasCRM.security.SecurityService;
import com.project.SaasCRM.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private DashboardController dashboardController;

    private DashboardDTO testDashboard;
    private Map<String, Object> testData;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        testDashboard = new DashboardDTO();
        testDashboard.setTotalCustomers(100L);
        
        Map<CustomerStatus, Long> customersByStatus = new HashMap<>();
        customersByStatus.put(CustomerStatus.ACTIVE, 80L);
        customersByStatus.put(CustomerStatus.INACTIVE, 20L);
        testDashboard.setCustomersByStatus(customersByStatus);
        
        Map<DealStage, Long> dealsByStage = new HashMap<>();
        dealsByStage.put(DealStage.PROPOSAL, 5L);
        dealsByStage.put(DealStage.NEGOTIATION, 10L);
        testDashboard.setDealsByStage(dealsByStage);
        
        Map<DealStage, BigDecimal> dealValuesByStage = new HashMap<>();
        dealValuesByStage.put(DealStage.PROPOSAL, new BigDecimal("10000"));
        dealValuesByStage.put(DealStage.NEGOTIATION, new BigDecimal("50000"));
        testDashboard.setDealValuesByStage(dealValuesByStage);
        
        Map<TaskStatus, Long> tasksByStatus = new HashMap<>();
        tasksByStatus.put(TaskStatus.PENDING, 15L);
        tasksByStatus.put(TaskStatus.IN_PROGRESS, 25L);
        testDashboard.setTasksByStatus(tasksByStatus);
        
        testDashboard.setRecentActivities(new ArrayList<>());
        
        testData = new HashMap<>();
        testData.put("key1", "value1");
        testData.put("key2", 123);
        testData.put("key3", true);
        
        startDate = LocalDateTime.now().minusDays(30);
        endDate = LocalDateTime.now();
    }

    @Test
    void getDashboardSummary_ShouldReturnDashboardSummary() {
        when(dashboardService.getDashboardSummary()).thenReturn(testDashboard);

        ResponseEntity<DashboardDTO> response = dashboardController.getDashboardSummary();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testDashboard, response.getBody());
        verify(dashboardService).getDashboardSummary();
    }

    @Test
    void getSalesForecast_ShouldReturnSalesForecast() {
        when(dashboardService.getSalesForecast(6)).thenReturn(testData);

        ResponseEntity<Map<String, Object>> response = dashboardController.getSalesForecast(6);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testData, response.getBody());
        verify(dashboardService).getSalesForecast(6);
    }

    @Test
    void getCustomerGrowth_ShouldReturnCustomerGrowth() {
        when(dashboardService.getCustomerGrowth(startDate, endDate)).thenReturn(testData);

        ResponseEntity<Map<String, Object>> response = dashboardController.getCustomerGrowth(startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testData, response.getBody());
        verify(dashboardService).getCustomerGrowth(startDate, endDate);
    }

    @Test
    void getDealPerformance_ShouldReturnDealPerformance() {
        when(dashboardService.getDealPerformance(startDate, endDate)).thenReturn(testData);

        ResponseEntity<Map<String, Object>> response = dashboardController.getDealPerformance(startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testData, response.getBody());
        verify(dashboardService).getDealPerformance(startDate, endDate);
    }

    @Test
    void getCustomerOverview_ShouldReturnCustomerOverview() {
        when(dashboardService.getCustomerOverview()).thenReturn(testData);

        ResponseEntity<Map<String, Object>> response = dashboardController.getCustomerOverview();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testData, response.getBody());
        verify(dashboardService).getCustomerOverview();
    }

    @Test
    void getTaskOverview_WithUserIdAndAuthorized_ShouldReturnTaskOverview() {
        when(securityService.isAdmin()).thenReturn(true);
        when(dashboardService.getTaskOverview(1L)).thenReturn(testData);

        ResponseEntity<Map<String, Object>> response = dashboardController.getTaskOverview(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testData, response.getBody());
        verify(securityService).isAdmin();
        verify(dashboardService).getTaskOverview(1L);
    }

    @Test
    void getTaskOverview_WithUserIdAndUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isCurrentUser(2L)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> dashboardController.getTaskOverview(2L));

        verify(securityService).isAdmin();
        verify(securityService).isCurrentUser(2L);
        verify(dashboardService, never()).getTaskOverview(anyLong());
    }

    @Test
    void getTaskOverview_WithoutUserIdProvided_ShouldUseCurrentUserId() {
        when(securityService.getCurrentUserId()).thenReturn(1L);
        when(dashboardService.getTaskOverview(1L)).thenReturn(testData);

        ResponseEntity<Map<String, Object>> response = dashboardController.getTaskOverview(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testData, response.getBody());
        verify(securityService).getCurrentUserId();
        verify(dashboardService).getTaskOverview(1L);
    }

    @Test
    void getDealValueByStage_ShouldReturnDealValueByStage() {
        when(dashboardService.getDealValueByStage()).thenReturn(testData);

        ResponseEntity<Map<String, Object>> response = dashboardController.getDealValueByStage();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testData, response.getBody());
        verify(dashboardService).getDealValueByStage();
    }

    @Test
    void getDealsWonLostRatio_ShouldReturnDealsWonLostRatio() {
        when(dashboardService.getDealsWonLostRatio(startDate, endDate)).thenReturn(testData);

        ResponseEntity<Map<String, Object>> response = dashboardController.getDealsWonLostRatio(startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testData, response.getBody());
        verify(dashboardService).getDealsWonLostRatio(startDate, endDate);
    }

    @Test
    void getTopPerformers_ShouldReturnTopPerformers() {
        when(dashboardService.getTopPerformingUsers(5)).thenReturn(testData);

        ResponseEntity<Map<String, Object>> response = dashboardController.getTopPerformers(5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testData, response.getBody());
        verify(dashboardService).getTopPerformingUsers(5);
    }

    @Test
    void getUserActivitySummary_WithUserIdAndAuthorized_ShouldReturnUserActivitySummary() {
        when(securityService.isAdmin()).thenReturn(true);
        when(dashboardService.getUserActivitySummary(1L)).thenReturn(testData);

        ResponseEntity<Map<String, Object>> response = dashboardController.getUserActivitySummary(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testData, response.getBody());
        verify(securityService).isAdmin();
        verify(dashboardService).getUserActivitySummary(1L);
    }

    @Test
    void getUserActivitySummary_WithUserIdAndUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isCurrentUser(2L)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> dashboardController.getUserActivitySummary(2L));

        verify(securityService).isAdmin();
        verify(securityService).isCurrentUser(2L);
        verify(dashboardService, never()).getUserActivitySummary(anyLong());
    }

    @Test
    void getUserActivitySummary_WithoutUserIdProvided_ShouldUseCurrentUserId() {
        when(securityService.getCurrentUserId()).thenReturn(1L);
        when(dashboardService.getUserActivitySummary(1L)).thenReturn(testData);

        ResponseEntity<Map<String, Object>> response = dashboardController.getUserActivitySummary(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testData, response.getBody());
        verify(securityService).getCurrentUserId();
        verify(dashboardService).getUserActivitySummary(1L);
    }

    @Test
    void getCustomerStatusDistribution_ShouldReturnCustomerStatusDistribution() {
        Map<CustomerStatus, Long> distribution = new HashMap<>();
        distribution.put(CustomerStatus.ACTIVE, 80L);
        distribution.put(CustomerStatus.INACTIVE, 20L);
        
        when(dashboardService.getCustomerStatusDistribution()).thenReturn(distribution);

        ResponseEntity<Map<CustomerStatus, Long>> response = dashboardController.getCustomerStatusDistribution();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(distribution, response.getBody());
        verify(dashboardService).getCustomerStatusDistribution();
    }

    @Test
    void getDealValuesByStage_ShouldReturnDealValuesByStage() {
        Map<DealStage, BigDecimal> values = new HashMap<>();
        values.put(DealStage.PROPOSAL, new BigDecimal("10000"));
        values.put(DealStage.NEGOTIATION, new BigDecimal("50000"));
        
        when(dashboardService.getDealValuesByStage()).thenReturn(values);

        ResponseEntity<Map<DealStage, BigDecimal>> response = dashboardController.getDealValuesByStage();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(values, response.getBody());
        verify(dashboardService).getDealValuesByStage();
    }

    @Test
    void getDealCountsByStage_ShouldReturnDealCountsByStage() {
        Map<DealStage, Long> counts = new HashMap<>();
        counts.put(DealStage.PROPOSAL, 5L);
        counts.put(DealStage.NEGOTIATION, 10L);
        
        when(dashboardService.getDealCountsByStage()).thenReturn(counts);

        ResponseEntity<Map<DealStage, Long>> response = dashboardController.getDealCountsByStage();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(counts, response.getBody());
        verify(dashboardService).getDealCountsByStage();
    }

    @Test
    void getTaskStatusDistribution_ShouldReturnTaskStatusDistribution() {
        Map<TaskStatus, Long> distribution = new HashMap<>();
        distribution.put(TaskStatus.PENDING, 15L);
        distribution.put(TaskStatus.IN_PROGRESS, 25L);
        
        when(dashboardService.getTaskStatusDistribution()).thenReturn(distribution);

        ResponseEntity<Map<TaskStatus, Long>> response = dashboardController.getTaskStatusDistribution();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(distribution, response.getBody());
        verify(dashboardService).getTaskStatusDistribution();
    }

    @Test
    void getRecentActivities_ShouldReturnRecentActivities() {
        List<AuditLogDTO> activities = new ArrayList<>();
        when(dashboardService.getRecentActivities(10)).thenReturn(activities);

        ResponseEntity<List<AuditLogDTO>> response = dashboardController.getRecentActivities(10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(activities, response.getBody());
        verify(dashboardService).getRecentActivities(10);
    }

    @Test
    void getSalesPerformance_ShouldReturnSalesPerformance() {
        Map<String, BigDecimal> performance = Map.of("2023-01", new BigDecimal("50000"), "2023-02", new BigDecimal("60000"));
        when(dashboardService.getSalesPerformance(startDate, endDate)).thenReturn(performance);

        ResponseEntity<Map<String, BigDecimal>> response = dashboardController.getSalesPerformance(startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(performance, response.getBody());
        verify(dashboardService).getSalesPerformance(startDate, endDate);
    }

    @Test
    void getTasksDueDistribution_ShouldReturnTasksDueDistribution() {
        Map<String, Long> distribution = Map.of("Today", 5L, "Tomorrow", 8L, "Next Week", 15L);
        when(dashboardService.getTasksDueDistribution(7)).thenReturn(distribution);

        ResponseEntity<Map<String, Long>> response = dashboardController.getTasksDueDistribution(7);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(distribution, response.getBody());
        verify(dashboardService).getTasksDueDistribution(7);
    }

    @Test
    void getConversionRates_ShouldReturnConversionRates() {
        when(dashboardService.getConversionRates()).thenReturn(testData);

        ResponseEntity<Map<String, Object>> response = dashboardController.getConversionRates();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testData, response.getBody());
        verify(dashboardService).getConversionRates();
    }

    @Test
    void getRevenueMetrics_ShouldReturnRevenueMetrics() {
        when(dashboardService.getRevenueMetrics(startDate, endDate)).thenReturn(testData);

        ResponseEntity<Map<String, Object>> response = dashboardController.getRevenueMetrics(startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testData, response.getBody());
        verify(dashboardService).getRevenueMetrics(startDate, endDate);
    }
} 