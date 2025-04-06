package com.project.SaasCRM.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailStatsDTO {
    private Long totalSent;
    private Long totalOpened;
    private Long totalClicked;
    private Double openRate;
    private Double clickRate;
    private Map<LocalDateTime, Long> sentByDate;
    private Map<LocalDateTime, Long> openedByDate;
    private Map<LocalDateTime, Long> clickedByDate;
    private Map<String, Long> statusCounts;
} 