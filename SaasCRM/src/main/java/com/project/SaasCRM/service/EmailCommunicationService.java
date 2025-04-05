package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.SendStatus;
import com.project.SaasCRM.domain.entity.EmailCommunication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EmailCommunicationService {
    EmailCommunication saveEmailCommunication(EmailCommunication emailCommunication);

    EmailCommunication updateEmailCommunication(EmailCommunication emailCommunication);

    void deleteEmailCommunication(Long emailId);

    Optional<EmailCommunication> findById(Long emailId);

    List<EmailCommunication> findAllEmailCommunications();

    Page<EmailCommunication> findAllEmailCommunicationsPaginated(Pageable pageable);

    List<EmailCommunication> findEmailsByCustomer(Long customerId);

    List<EmailCommunication> findEmailsBySentBy(Long userId);

    List<EmailCommunication> findEmailsByTemplate(Long templateId);

    List<EmailCommunication> findEmailsByStatus(SendStatus status);

    List<EmailCommunication> findOpenedEmails();

    EmailCommunication sendEmail(Long emailId);

    EmailCommunication scheduleEmail(Long emailId, LocalDateTime scheduledTime);

    void processScheduledEmails();

    void markEmailAsOpened(Long emailId);

    void incrementEmailClickCount(Long emailId);

    EmailCommunication createEmailFromTemplate(Long templateId, Long customerId, String subject);

    Map<String, Long> getEmailCountsByDate(LocalDateTime startDate, LocalDateTime endDate);

    Map<SendStatus, Long> getEmailStatusCounts();

    double calculateEmailOpenRate(LocalDateTime startDate, LocalDateTime endDate);

    double calculateEmailClickRate(LocalDateTime startDate, LocalDateTime endDate);
}
