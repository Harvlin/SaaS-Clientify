package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.CustomerStatus;
import com.project.SaasCRM.domain.DealStage;
import com.project.SaasCRM.domain.TaskStatus;
import com.project.SaasCRM.domain.dto.AuditLogDTO;
import com.project.SaasCRM.domain.dto.DashboardDTO;
import com.project.SaasCRM.exception.UnauthorizedException;
import com.project.SaasCRM.security.SecurityService;
import com.project.SaasCRM.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "APIs for dashboard data and metrics")
public class DashboardController {
    private final DashboardService dashboardService;
    private final SecurityService securityService;

    @Operation(summary = "Get dashboard summary", description = "Returns a summary of key metrics for the dashboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved dashboard summary",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DashboardDTO.class)))
    })
    @GetMapping("/summary")
    public ResponseEntity<DashboardDTO> getDashboardSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }

    @Operation(summary = "Get sales forecast", description = "Returns sales forecast data for upcoming periods")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved sales forecast",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/sales-forecast")
    public ResponseEntity<Map<String, Object>> getSalesForecast(
            @Parameter(description = "Number of months to forecast") @RequestParam(defaultValue = "6") int months) {
        return ResponseEntity.ok(dashboardService.getSalesForecast(months));
    }

    @Operation(summary = "Get customer growth data", description = "Returns customer growth data over a specified time period")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved customer growth data",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/customer-growth")
    public ResponseEntity<Map<String, Object>> getCustomerGrowth(
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(dashboardService.getCustomerGrowth(startDate, endDate));
    }

    @Operation(summary = "Get deal performance data", description = "Returns deal performance data over a specified time period")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved deal performance data",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/deal-performance")
    public ResponseEntity<Map<String, Object>> getDealPerformance(
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(dashboardService.getDealPerformance(startDate, endDate));
    }

    @Operation(summary = "Get customer overview", description = "Returns an overview of customer data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved customer overview",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/customer-overview")
    public ResponseEntity<Map<String, Object>> getCustomerOverview() {
        return ResponseEntity.ok(dashboardService.getCustomerOverview());
    }

    @Operation(summary = "Get task overview", description = "Returns an overview of task data for a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved task overview",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access this data")
    })
    @GetMapping("/task-overview")
    public ResponseEntity<Map<String, Object>> getTaskOverview(
            @Parameter(description = "User ID (optional)") @RequestParam(required = false) Long userId) {
        // If userId is provided, ensure user can only access their own data unless they're an admin
        if (userId != null && !securityService.isAdmin() && !securityService.isCurrentUser(userId)) {
            throw new UnauthorizedException("You are not authorized to access this task data");
        }
        
        // If no userId is provided, use the current user's ID
        if (userId == null) {
            userId = securityService.getCurrentUserId();
        }
        
        return ResponseEntity.ok(dashboardService.getTaskOverview(userId));
    }

    @Operation(summary = "Get deal value by stage", description = "Returns the value of deals grouped by pipeline stage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved deal value by stage",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/deal-value-by-stage")
    public ResponseEntity<Map<String, Object>> getDealValueByStage() {
        return ResponseEntity.ok(dashboardService.getDealValueByStage());
    }

    @Operation(summary = "Get deals won/lost ratio", description = "Returns the ratio of won to lost deals over a specified time period")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved deals won/lost ratio",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/deals-won-lost")
    public ResponseEntity<Map<String, Object>> getDealsWonLostRatio(
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(dashboardService.getDealsWonLostRatio(startDate, endDate));
    }

    @Operation(summary = "Get top performing users", description = "Returns data on top performing users based on various metrics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved top performers",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/top-performers")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> getTopPerformers(
            @Parameter(description = "Limit of users to return") @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(dashboardService.getTopPerformingUsers(limit));
    }

    @Operation(summary = "Get user activity summary", description = "Returns a summary of activities for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user activity summary",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access this data")
    })
    @GetMapping("/user-activity")
    public ResponseEntity<Map<String, Object>> getUserActivitySummary(
            @Parameter(description = "User ID (optional)") @RequestParam(required = false) Long userId) {
        // If userId is provided, ensure user can only access their own data unless they're an admin
        if (userId != null && !securityService.isAdmin() && !securityService.isCurrentUser(userId)) {
            throw new UnauthorizedException("You are not authorized to access this user activity data");
        }
        
        // If no userId is provided, use the current user's ID
        if (userId == null) {
            userId = securityService.getCurrentUserId();
        }
        
        return ResponseEntity.ok(dashboardService.getUserActivitySummary(userId));
    }

    @Operation(summary = "Get customer status distribution", description = "Returns the distribution of customers by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved customer status distribution",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/customer-status-distribution")
    public ResponseEntity<Map<CustomerStatus, Long>> getCustomerStatusDistribution() {
        return ResponseEntity.ok(dashboardService.getCustomerStatusDistribution());
    }

    @Operation(summary = "Get deal values by stage", description = "Returns the total value of deals by pipeline stage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved deal values by stage",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/deal-values-by-stage")
    public ResponseEntity<Map<DealStage, BigDecimal>> getDealValuesByStage() {
        return ResponseEntity.ok(dashboardService.getDealValuesByStage());
    }

    @Operation(summary = "Get deal counts by stage", description = "Returns the count of deals by pipeline stage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved deal counts by stage",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/deal-counts-by-stage")
    public ResponseEntity<Map<DealStage, Long>> getDealCountsByStage() {
        return ResponseEntity.ok(dashboardService.getDealCountsByStage());
    }

    @Operation(summary = "Get task status distribution", description = "Returns the distribution of tasks by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved task status distribution",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/task-status-distribution")
    public ResponseEntity<Map<TaskStatus, Long>> getTaskStatusDistribution() {
        return ResponseEntity.ok(dashboardService.getTaskStatusDistribution());
    }

    @Operation(summary = "Get recent activities", description = "Returns a list of recent activities in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved recent activities",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/recent-activities")
    public ResponseEntity<List<AuditLogDTO>> getRecentActivities(
            @Parameter(description = "Limit of activities to return") @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(dashboardService.getRecentActivities(limit));
    }

    @Operation(summary = "Get sales performance", description = "Returns sales performance data over a specified time period")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved sales performance",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/sales-performance")
    public ResponseEntity<Map<String, BigDecimal>> getSalesPerformance(
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(dashboardService.getSalesPerformance(startDate, endDate));
    }

    @Operation(summary = "Get tasks due distribution", description = "Returns the distribution of tasks by due date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks due distribution",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/tasks-due-distribution")
    public ResponseEntity<Map<String, Long>> getTasksDueDistribution(
            @Parameter(description = "Number of days to look ahead") @RequestParam(defaultValue = "7") int nextDays) {
        return ResponseEntity.ok(dashboardService.getTasksDueDistribution(nextDays));
    }

    @Operation(summary = "Get conversion rates", description = "Returns various conversion rates in the sales process")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved conversion rates",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/conversion-rates")
    public ResponseEntity<Map<String, Object>> getConversionRates() {
        return ResponseEntity.ok(dashboardService.getConversionRates());
    }

    @Operation(summary = "Get revenue metrics", description = "Returns revenue metrics over a specified time period")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved revenue metrics",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/revenue-metrics")
    public ResponseEntity<Map<String, Object>> getRevenueMetrics(
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(dashboardService.getRevenueMetrics(startDate, endDate));
    }
} 