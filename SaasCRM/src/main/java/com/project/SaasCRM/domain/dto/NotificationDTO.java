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
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String message;
    private String entityType;
    private Long entityId;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private boolean isRead;
    private String notificationType;
    private LocalDateTime dueDate;
} 