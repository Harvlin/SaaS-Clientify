package com.project.SaasCRM.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AuditLogService {
    void logUserActivity(Long userId, String activity, String entityType, Long entityId);

    void logSystemActivity(String activity, String entityType, Long entityId);

    List<Map<String, Object>> getUserActivityLogs(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    List<Map<String, Object>> getEntityActivityLogs(String entityType, Long entityId);
}
