package com.project.SaasCRM.repository;

import com.project.SaasCRM.domain.SendStatus;
import com.project.SaasCRM.domain.entity.Customer;
import com.project.SaasCRM.domain.entity.EmailCommunication;
import com.project.SaasCRM.domain.entity.EmailTemplate;
import com.project.SaasCRM.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailCommunicationRepository extends JpaRepository<EmailCommunication, Long> {
    List<EmailCommunication> findByCustomer(Customer customer);

    List<EmailCommunication> findBySentBy(User user);

    List<EmailCommunication> findByEmailTemplate(EmailTemplate emailTemplate);

    List<EmailCommunication> findByIsOpened(Boolean isOpened);

    List<EmailCommunication> findBySendStatus(SendStatus sendStatus);

    @Query("SELECT e FROM EmailCommunication e WHERE " +
            "e.scheduledFor <= :now AND e.sendStatus = 'SCHEDULED'")
    List<EmailCommunication> findEmailsDueForSending(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(e) FROM EmailCommunication e WHERE " +
            "e.sentAt >= :startDate AND e.sentAt <= :endDate")
    Long countEmailsSentInPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT e FROM EmailCommunication e WHERE e.customer.id = :customerId")
    List<EmailCommunication> findByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT e FROM EmailCommunication e WHERE e.sentBy.id = :userId")
    List<EmailCommunication> findBySentById(@Param("userId") Long userId);
    
    @Query("SELECT e FROM EmailCommunication e WHERE e.emailTemplate.id = :templateId")
    List<EmailCommunication> findByEmailTemplateId(@Param("templateId") Long templateId);
    
    List<EmailCommunication> findByStatus(SendStatus status);
    
    List<EmailCommunication> findByOpenedTrue();
    
    @Query("SELECT e FROM EmailCommunication e WHERE e.sendStatus = :status AND e.scheduledFor <= :currentTime")
    List<EmailCommunication> findByStatusAndScheduledTimeLessThanEqual(
            @Param("status") SendStatus status,
            @Param("currentTime") LocalDateTime currentTime);
    
    List<EmailCommunication> findBySentAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}