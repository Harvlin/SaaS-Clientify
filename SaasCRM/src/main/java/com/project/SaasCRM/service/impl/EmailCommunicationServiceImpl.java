package com.project.SaasCRM.service.impl;

import com.project.SaasCRM.domain.SendStatus;
import com.project.SaasCRM.domain.entity.EmailCommunication;
import com.project.SaasCRM.domain.entity.Customer;
import com.project.SaasCRM.domain.entity.User;
import com.project.SaasCRM.domain.entity.EmailTemplate;
import com.project.SaasCRM.domain.dto.EmailCommunicationDTO;
import com.project.SaasCRM.exception.ResourceNotFoundException;
import com.project.SaasCRM.repository.EmailCommunicationRepository;
import com.project.SaasCRM.repository.CustomerRepository;
import com.project.SaasCRM.repository.UserRepository;
import com.project.SaasCRM.repository.EmailTemplateRepository;
import com.project.SaasCRM.service.EmailCommunicationService;
import com.project.SaasCRM.service.AuditLogService;
import com.project.SaasCRM.mapper.EmailCommunicationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmailCommunicationServiceImpl implements EmailCommunicationService {
    private final EmailCommunicationRepository emailCommunicationRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final EmailTemplateRepository emailTemplateRepository;
    private final AuditLogService auditLogService;
    private final EmailCommunicationMapper emailCommunicationMapper;

    @Override
    @Transactional
    public EmailCommunicationDTO saveEmailCommunication(EmailCommunicationDTO emailCommunicationDTO) {
        EmailCommunication emailCommunication = emailCommunicationMapper.toEntity(emailCommunicationDTO);
        EmailCommunication savedEmail = emailCommunicationRepository.save(emailCommunication);
        auditLogService.logSystemActivity("EMAIL_CREATED", "EMAIL", savedEmail.getId());
        return emailCommunicationMapper.toDto(savedEmail);
    }

    @Override
    @Transactional
    public EmailCommunicationDTO updateEmailCommunication(EmailCommunicationDTO emailCommunicationDTO) {
        EmailCommunication existingEmail = emailCommunicationRepository.findById(emailCommunicationDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Email communication not found"));

        EmailCommunication emailCommunication = emailCommunicationMapper.toEntity(emailCommunicationDTO);
        EmailCommunication updatedEmail = emailCommunicationRepository.save(emailCommunication);
        auditLogService.logSystemActivity("EMAIL_UPDATED", "EMAIL", updatedEmail.getId());
        return emailCommunicationMapper.toDto(updatedEmail);
    }

    @Override
    @Transactional
    public void deleteEmailCommunication(Long emailId) {
        EmailCommunication emailCommunication = emailCommunicationRepository.findById(emailId)
                .orElseThrow(() -> new ResourceNotFoundException("Email communication not found"));
        emailCommunicationRepository.delete(emailCommunication);
        auditLogService.logSystemActivity("EMAIL_DELETED", "EMAIL", emailId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmailCommunicationDTO> findById(Long emailId) {
        return emailCommunicationRepository.findById(emailId)
                .map(emailCommunicationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailCommunicationDTO> findAllEmailCommunications() {
        return emailCommunicationMapper.toDtoList(emailCommunicationRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmailCommunicationDTO> findAllEmailCommunicationsPaginated(Pageable pageable) {
        return emailCommunicationRepository.findAll(pageable)
                .map(emailCommunicationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailCommunicationDTO> findEmailsByCustomer(Long customerId) {
        return emailCommunicationMapper.toDtoList(emailCommunicationRepository.findByCustomerId(customerId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailCommunicationDTO> findEmailsBySentBy(Long userId) {
        return emailCommunicationMapper.toDtoList(emailCommunicationRepository.findBySentById(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailCommunicationDTO> findEmailsByTemplate(Long templateId) {
        return emailCommunicationMapper.toDtoList(emailCommunicationRepository.findByEmailTemplateId(templateId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailCommunicationDTO> findEmailsByStatus(SendStatus status) {
        return emailCommunicationMapper.toDtoList(emailCommunicationRepository.findBySendStatus(status));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailCommunicationDTO> findOpenedEmails() {
        return emailCommunicationMapper.toDtoList(emailCommunicationRepository.findByOpenedTrue());
    }

    @Override
    @Transactional
    public EmailCommunicationDTO sendEmail(Long emailId) {
        EmailCommunication email = emailCommunicationRepository.findById(emailId)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found with id: " + emailId));
        
        try {
            // Send email logic here
            email.setSendStatus(SendStatus.SENT);
            email.setSentAt(LocalDateTime.now());
            EmailCommunication savedEmail = emailCommunicationRepository.save(email);
            auditLogService.logSystemActivity("EMAIL_SENT", "EMAIL", emailId);
            return emailCommunicationMapper.toDto(savedEmail);
        } catch (Exception e) {
            email.setSendStatus(SendStatus.FAILED);
            emailCommunicationRepository.save(email);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    @Transactional
    public EmailCommunicationDTO scheduleEmail(Long emailId, LocalDateTime scheduledTime) {
        EmailCommunication email = emailCommunicationRepository.findById(emailId)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found with id: " + emailId));
        
        email.setScheduledFor(scheduledTime);
        email.setSendStatus(SendStatus.SCHEDULED);
        EmailCommunication savedEmail = emailCommunicationRepository.save(email);
        auditLogService.logSystemActivity("EMAIL_SCHEDULED", "EMAIL", emailId);
        return emailCommunicationMapper.toDto(savedEmail);
    }

    @Override
    @Transactional
    public void processScheduledEmails() {
        LocalDateTime now = LocalDateTime.now();
        List<EmailCommunication> scheduledEmails = emailCommunicationRepository
                .findByStatusAndScheduledTimeLessThanEqual(SendStatus.SCHEDULED, now);
        
        for (EmailCommunication email : scheduledEmails) {
            try {
                // Send email logic here
                email.setSendStatus(SendStatus.SENT);
                email.setSentAt(now);
                emailCommunicationRepository.save(email);
            } catch (Exception e) {
                email.setSendStatus(SendStatus.FAILED);
                emailCommunicationRepository.save(email);
                throw new RuntimeException("Failed to send scheduled email", e);
            }
        }
    }

    @Override
    @Transactional
    public void markEmailAsOpened(Long emailId) {
        EmailCommunication email = emailCommunicationRepository.findById(emailId)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found with id: " + emailId));
        
        email.setIsOpened(true);
        email.setOpenedAt(LocalDateTime.now());
        emailCommunicationRepository.save(email);
        auditLogService.logSystemActivity("EMAIL_OPENED", "EMAIL", emailId);
    }

    @Override
    @Transactional
    public void incrementEmailClickCount(Long emailId) {
        EmailCommunication email = emailCommunicationRepository.findById(emailId)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found with id: " + emailId));
        
        email.setClickCount(email.getClickCount() + 1);
        emailCommunicationRepository.save(email);
        auditLogService.logSystemActivity("EMAIL_CLICKED", "EMAIL", emailId);
    }

    @Override
    @Transactional
    public EmailCommunicationDTO createEmailFromTemplate(Long templateId, Long customerId, String subject) {
        EmailTemplate template = emailTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Email template not found"));
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        EmailCommunication emailCommunication = new EmailCommunication();
        emailCommunication.setEmailTemplate(template);
        emailCommunication.setCustomer(customer);
        emailCommunication.setSubject(subject);
        emailCommunication.setSendStatus(SendStatus.DRAFT);
        
        EmailCommunication savedEmail = emailCommunicationRepository.save(emailCommunication);
        auditLogService.logSystemActivity("EMAIL_CREATED_FROM_TEMPLATE", "EMAIL", savedEmail.getId());
        return emailCommunicationMapper.toDto(savedEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getEmailCountsByDate(LocalDateTime startDate, LocalDateTime endDate) {
        return emailCommunicationRepository.findBySentAtBetween(startDate, endDate)
                .stream()
                .collect(Collectors.groupingBy(
                    email -> email.getSentAt().toLocalDate().toString(),
                    Collectors.counting()
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<SendStatus, Long> getEmailStatusCounts() {
        return emailCommunicationRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                    EmailCommunication::getSendStatus,
                    Collectors.counting()
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public double calculateEmailOpenRate(LocalDateTime startDate, LocalDateTime endDate) {
        List<EmailCommunication> emails = emailCommunicationRepository
                .findBySentAtBetween(startDate, endDate);
        
        if (emails.isEmpty()) {
            return 0.0;
        }
        
        long totalEmails = emails.size();
        long openedEmails = emails.stream()
                .filter(email -> email.getIsOpened())
                .count();
        
        return (double) openedEmails / totalEmails * 100;
    }

    @Override
    @Transactional(readOnly = true)
    public double calculateEmailClickRate(LocalDateTime startDate, LocalDateTime endDate) {
        List<EmailCommunication> emails = emailCommunicationRepository
                .findBySentAtBetween(startDate, endDate);
        
        if (emails.isEmpty()) {
            return 0.0;
        }
        
        long totalEmails = emails.size();
        long clickedEmails = emails.stream()
                .filter(email -> email.getClickCount() > 0)
                .count();
        
        return (double) clickedEmails / totalEmails * 100;
    }
} 