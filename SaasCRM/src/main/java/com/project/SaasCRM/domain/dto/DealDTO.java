package com.project.SaasCRM.domain.dto;

import com.project.SaasCRM.domain.DealStage;
import com.project.SaasCRM.domain.DealStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class DealDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal value;
    private DealStage stage;
    private DealStatus status;
    private Long customerId;
    private LocalDateTime expectedCloseDate;
    private LocalDateTime actualCloseDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<Long> assignedUserIds;
    private Set<Long> taskIds;
    private Integer probabilityPercentage;
} 