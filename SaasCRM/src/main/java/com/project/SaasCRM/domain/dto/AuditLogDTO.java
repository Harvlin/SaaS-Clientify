package com.project.SaasCRM.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDTO {
    private Long id;
    private Long userId;
    private String activity;
    private String entityType;
    private Long entityId;
    private LocalDateTime timestamp;
    private String details;
    private boolean systemActivity;
} 