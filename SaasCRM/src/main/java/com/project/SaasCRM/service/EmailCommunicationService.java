package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.SendStatus;
import com.project.SaasCRM.domain.dto.EmailCommunicationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EmailCommunicationService {
    EmailCommunicationDTO saveEmailCommunication(EmailCommunicationDTO emailCommunication);

    EmailCommunicationDTO updateEmailCommunication(EmailCommunicationDTO emailCommunication);

    void deleteEmailCommunication(Long emailId);

    Optional<EmailCommunicationDTO> findById(Long emailId);

    List<EmailCommunicationDTO> findAllEmailCommunications();

    Page<EmailCommunicationDTO> findAllEmailCommunicationsPaginated(Pageable pageable);

    List<EmailCommunicationDTO> findEmailsByCustomer(Long customerId);

    List<EmailCommunicationDTO> findEmailsBySentBy(Long userId);

    List<EmailCommunicationDTO> findEmailsByTemplate(Long templateId);

    List<EmailCommunicationDTO> findEmailsByStatus(SendStatus status);

    List<EmailCommunicationDTO> findOpenedEmails();

    EmailCommunicationDTO sendEmail(Long emailId);

    EmailCommunicationDTO scheduleEmail(Long emailId, LocalDateTime scheduledTime);

    void processScheduledEmails();

    void markEmailAsOpened(Long emailId);

    void incrementEmailClickCount(Long emailId);

    EmailCommunicationDTO createEmailFromTemplate(Long templateId, Long customerId, String subject);

    Map<String, Long> getEmailCountsByDate(LocalDateTime startDate, LocalDateTime endDate);

    Map<SendStatus, Long> getEmailStatusCounts();

    double calculateEmailOpenRate(LocalDateTime startDate, LocalDateTime endDate);

    double calculateEmailClickRate(LocalDateTime startDate, LocalDateTime endDate);
}
