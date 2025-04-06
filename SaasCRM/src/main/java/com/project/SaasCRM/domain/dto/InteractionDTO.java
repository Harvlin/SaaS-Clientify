package com.project.SaasCRM.domain.dto;

import com.project.SaasCRM.domain.InteractionType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InteractionDTO {
    private Long id;
    private Long customerId;
    private Long userId;
    private InteractionType type;
    private String title;
    private String description;
    private String outcome;
    private String nextSteps;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime completedAt;
    private Integer durationMinutes;
    private String location;
    private boolean completed;
    private Boolean successful;
} 