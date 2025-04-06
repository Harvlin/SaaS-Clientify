package com.project.SaasCRM.domain.dto;

import com.project.SaasCRM.domain.CustomerStatus;
import com.project.SaasCRM.domain.DealStage;
import com.project.SaasCRM.domain.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private Long totalCustomers;
    private Map<CustomerStatus, Long> customersByStatus;
    private Map<DealStage, Long> dealsByStage;
    private Map<DealStage, BigDecimal> dealValuesByStage;
    private Map<TaskStatus, Long> tasksByStatus;
    private List<AuditLogDTO> recentActivities;
} 