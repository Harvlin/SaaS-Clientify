package com.project.SaasCRM.repository;

import com.project.SaasCRM.domain.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(Long userId);
    
    Page<Notification> findByUserId(Long userId, Pageable pageable);
    
    long countByUserIdAndReadFalse(Long userId);
    
    void deleteByUserId(Long userId);
    
    @Modifying
    @Query("UPDATE Notification n SET n.read = true, n.readAt = :readAt WHERE n.user.id = :userId AND n.read = false")
    void markAllAsRead(@Param("userId") Long userId, @Param("readAt") LocalDateTime readAt);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.dueDate <= :now AND n.read = false")
    List<Notification> findDueNotifications(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    @Query("SELECT n FROM Notification n WHERE n.entityType = :entityType AND n.entityId = :entityId")
    List<Notification> findByEntityTypeAndEntityId(@Param("entityType") String entityType, @Param("entityId") Long entityId);
} 