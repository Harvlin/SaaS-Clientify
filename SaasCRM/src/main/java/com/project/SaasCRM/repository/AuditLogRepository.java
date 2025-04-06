package com.project.SaasCRM.repository;

import com.project.SaasCRM.domain.entity.AuditLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserIdAndTimestampBetweenOrderByTimestampDesc(
        Long userId, LocalDateTime startDate, LocalDateTime endDate);

    List<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(
        String entityType, Long entityId);

    List<AuditLog> findByUserIdOrderByTimestampDesc(
        Long userId,
        Pageable pageable
    );

    List<AuditLog> findAllByOrderByTimestampDesc(Pageable pageable);
} 