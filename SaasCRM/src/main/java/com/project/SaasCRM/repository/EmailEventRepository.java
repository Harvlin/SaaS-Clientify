package com.project.SaasCRM.repository;

import com.project.SaasCRM.domain.EmailEventType;
import com.project.SaasCRM.domain.entity.EmailEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailEventRepository extends JpaRepository<EmailEvent, Long> {
    
    List<EmailEvent> findByEmailId(String emailId);
    
    List<EmailEvent> findByRecipient(String recipient);
    
    List<EmailEvent> findByEventType(EmailEventType eventType);
    
    List<EmailEvent> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT e.eventType, COUNT(e) FROM EmailEvent e " +
           "WHERE e.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY e.eventType")
    List<Object[]> countByEventType(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT DATE(e.createdAt) as date, e.eventType, COUNT(e) FROM EmailEvent e " +
           "WHERE e.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(e.createdAt), e.eventType")
    List<Object[]> countByDateAndEventType(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT e.recipient, COUNT(e) FROM EmailEvent e " +
           "WHERE e.eventType = :eventType AND e.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY e.recipient")
    List<Object[]> countByRecipientAndEventType(
        @Param("eventType") EmailEventType eventType,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
} 