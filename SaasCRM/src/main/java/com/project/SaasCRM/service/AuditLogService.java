package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.dto.AuditLogDTO;
import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogService {
    void logUserActivity(Long userId, String activity, String entityType, Long entityId);

    void logSystemActivity(String activity, String entityType, Long entityId);

    List<AuditLogDTO> getUserActivityLogs(Long userId, int limit);

    List<AuditLogDTO> getUserActivityLogs(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    List<AuditLogDTO> getEntityActivityLogs(String entityType, Long entityId);

    List<AuditLogDTO> getEntityActivityLogs(String entityType, Long entityId, int limit);

    List<AuditLogDTO> getRecentActivities(int limit);
}
