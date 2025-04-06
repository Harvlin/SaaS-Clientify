package com.project.SaasCRM.service.impl;

import com.project.SaasCRM.domain.entity.AuditLog;
import com.project.SaasCRM.domain.dto.AuditLogDTO;
import com.project.SaasCRM.repository.AuditLogRepository;
import com.project.SaasCRM.service.AuditLogService;
import com.project.SaasCRM.mapper.AuditLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {
    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    @Transactional
    public void logUserActivity(Long userId, String activity, String entityType, Long entityId) {
        Assert.notNull(userId, "User ID cannot be null");
        Assert.hasText(activity, "Activity cannot be empty");
        Assert.hasText(entityType, "Entity type cannot be empty");
        Assert.notNull(entityId, "Entity ID cannot be null");

        AuditLog log = AuditLog.builder()
            .userId(userId)
            .activity(activity)
            .entityType(entityType)
            .entityId(entityId)
            .systemActivity(false)
            .build();
        
        auditLogRepository.save(log);
    }

    @Override
    @Transactional
    public void logSystemActivity(String activity, String entityType, Long entityId) {
        Assert.hasText(activity, "Activity cannot be empty");
        Assert.hasText(entityType, "Entity type cannot be empty");
        Assert.notNull(entityId, "Entity ID cannot be null");

        AuditLog log = AuditLog.builder()
            .activity(activity)
            .entityType(entityType)
            .entityId(entityId)
            .systemActivity(true)
            .build();
        
        auditLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogDTO> getUserActivityLogs(Long userId, int limit) {
        Assert.notNull(userId, "User ID cannot be null");
        Assert.isTrue(limit > 0, "Limit must be greater than 0");

        return auditLogRepository.findByUserIdOrderByTimestampDesc(userId, PageRequest.of(0, limit))
            .stream()
            .map(auditLogMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogDTO> getUserActivityLogs(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        Assert.notNull(userId, "User ID cannot be null");
        Assert.notNull(startDate, "Start date cannot be null");
        Assert.notNull(endDate, "End date cannot be null");
        Assert.isTrue(!endDate.isBefore(startDate), "End date must not be before start date");

        return auditLogRepository.findByUserIdAndTimestampBetweenOrderByTimestampDesc(userId, startDate, endDate)
            .stream()
            .map(auditLogMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogDTO> getEntityActivityLogs(String entityType, Long entityId) {
        Assert.hasText(entityType, "Entity type cannot be empty");
        Assert.notNull(entityId, "Entity ID cannot be null");

        return auditLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId)
            .stream()
            .map(auditLogMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogDTO> getEntityActivityLogs(String entityType, Long entityId, int limit) {
        Assert.hasText(entityType, "Entity type cannot be empty");
        Assert.notNull(entityId, "Entity ID cannot be null");
        Assert.isTrue(limit > 0, "Limit must be greater than 0");

        return auditLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId)
            .stream()
            .limit(limit)
            .map(auditLogMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogDTO> getRecentActivities(int limit) {
        Assert.isTrue(limit > 0, "Limit must be greater than 0");

        return auditLogRepository.findAllByOrderByTimestampDesc(PageRequest.of(0, limit))
            .stream()
            .map(auditLogMapper::toDto)
            .collect(Collectors.toList());
    }
} 