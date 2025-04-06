package com.project.SaasCRM.service.impl;

import com.project.SaasCRM.domain.EmailEventType;
import com.project.SaasCRM.domain.SendStatus;
import com.project.SaasCRM.domain.entity.EmailEvent;
import com.project.SaasCRM.domain.entity.EmailCommunication;
import com.project.SaasCRM.domain.dto.EmailDTO;
import com.project.SaasCRM.domain.dto.EmailStatsDTO;
import com.project.SaasCRM.repository.EmailEventRepository;
import com.project.SaasCRM.repository.EmailCommunicationRepository;
import com.project.SaasCRM.service.EmailService;
import com.project.SaasCRM.service.EmailValidationService;
import com.project.SaasCRM.service.AuditLogService;
import com.project.SaasCRM.mapper.EmailMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    private final TaskScheduler taskScheduler;
    private final AuditLogService auditLogService;
    private final EmailEventRepository emailEventRepository;
    private final EmailCommunicationRepository emailCommunicationRepository;
    private final EmailValidationService emailValidationService;
    private final EmailMapper emailMapper;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.email.templates.path}")
    private String templatesPath;

    @Override
    @Transactional
    public EmailDTO sendEmail(String to, String subject, String content) {
        validateAndCheckRateLimit(to);
        
        EmailCommunication email = EmailCommunication.builder()
            .subject(subject)
            .content(content)
            .senderEmail(fromEmail)
            .recipientEmail(to)
            .sentAt(LocalDateTime.now())
            .isOpened(false)
            .clickCount(0)
            .createdAt(LocalDateTime.now())
            .sendStatus(SendStatus.SENT)
            .build();
        
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            
            emailSender.send(message);
            emailValidationService.recordEmailSent(fromEmail);
            createEmailEvent(email.getId().toString(), to, subject, EmailEventType.SENT, null);
            auditLogService.logSystemActivity("EMAIL_SENT", "EMAIL", null);
            
            email = emailCommunicationRepository.save(email);
            return emailMapper.toDto(email);
        } catch (MessagingException e) {
            createEmailEvent(email.getId().toString(), to, subject, EmailEventType.FAILED, e.getMessage());
            auditLogService.logSystemActivity("EMAIL_SEND_FAILED", "EMAIL", null);
            
            email.setSendStatus(SendStatus.FAILED);
            email = emailCommunicationRepository.save(email);
            return emailMapper.toDto(email);
        }
    }

    @Override
    @Transactional
    public EmailDTO sendEmailWithAttachment(String to, String subject, String content, String attachmentPath) {
        validateAndCheckRateLimit(to);
        
        EmailCommunication email = EmailCommunication.builder()
            .subject(subject)
            .content(content)
            .senderEmail(fromEmail)
            .recipientEmail(to)
            .sentAt(LocalDateTime.now())
            .isOpened(false)
            .clickCount(0)
            .createdAt(LocalDateTime.now())
            .sendStatus(SendStatus.SENT)
            .build();
        
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            
            FileSystemResource file = new FileSystemResource(new File(attachmentPath));
            helper.addAttachment(file.getFilename(), file);
            
            emailSender.send(message);
            emailValidationService.recordEmailSent(fromEmail);
            createEmailEvent(email.getId().toString(), to, subject, EmailEventType.SENT, "Attachment: " + file.getFilename());
            auditLogService.logSystemActivity("EMAIL_WITH_ATTACHMENT_SENT", "EMAIL", null);
            
            email = emailCommunicationRepository.save(email);
            return emailMapper.toDto(email);
        } catch (MessagingException e) {
            createEmailEvent(email.getId().toString(), to, subject, EmailEventType.FAILED, e.getMessage());
            auditLogService.logSystemActivity("EMAIL_SEND_FAILED", "EMAIL", null);
            
            email.setSendStatus(SendStatus.FAILED);
            email = emailCommunicationRepository.save(email);
            return emailMapper.toDto(email);
        }
    }

    @Override
    public EmailDTO sendTemplatedEmail(String to, String templateName, Map<String, Object> templateVariables) {
        Context context = new Context();
        context.setVariables(templateVariables);
        
        String content = templateEngine.process(templateName, context);
        return sendEmail(to, (String) templateVariables.get("subject"), content);
    }

    @Override
    @Transactional
    public List<EmailDTO> sendBulkEmail(List<String> recipients, String subject, String content) {
        return recipients.stream()
            .map(recipient -> {
                try {
                    validateAndCheckRateLimit(recipient);
                    return sendEmail(recipient, subject, content);
                } catch (RuntimeException e) {
                    auditLogService.logSystemActivity("BULK_EMAIL_RECIPIENT_FAILED", "EMAIL", null);
                    return null;
                }
            })
            .filter(email -> email != null)
            .collect(Collectors.toList());
    }

    @Override
    public List<EmailDTO> sendBulkTemplatedEmail(List<String> recipients, String templateName, Map<String, Object> templateVariables) {
        return recipients.stream()
            .map(recipient -> sendTemplatedEmail(recipient, templateName, templateVariables))
            .collect(Collectors.toList());
    }

    @Override
    public EmailDTO scheduleEmail(String to, String subject, String content, LocalDateTime scheduledTime) {
        EmailCommunication email = EmailCommunication.builder()
            .subject(subject)
            .content(content)
            .senderEmail(fromEmail)
            .recipientEmail(to)
            .scheduledFor(scheduledTime)
            .isOpened(false)
            .clickCount(0)
            .createdAt(LocalDateTime.now())
            .sendStatus(SendStatus.SCHEDULED)
            .build();
        
        email = emailCommunicationRepository.save(email);
        
        Date scheduledDate = Date.from(scheduledTime.atZone(ZoneId.systemDefault()).toInstant());
        taskScheduler.schedule(
            () -> sendEmail(to, subject, content),
            scheduledDate
        );
        auditLogService.logSystemActivity("EMAIL_SCHEDULED", "EMAIL", null);
        
        return emailMapper.toDto(email);
    }

    @Override
    public EmailDTO scheduleTemplatedEmail(String to, String templateName, Map<String, Object> templateVariables, LocalDateTime scheduledTime) {
        Context context = new Context();
        context.setVariables(templateVariables);
        String content = templateEngine.process(templateName, context);
        
        return scheduleEmail(to, (String) templateVariables.get("subject"), content, scheduledTime);
    }

    @Override
    @Transactional
    public void trackEmailOpen(String emailId) {
        createEmailEvent(emailId, null, null, EmailEventType.OPENED, null);
        auditLogService.logSystemActivity("EMAIL_OPENED", "EMAIL", null);
        
        EmailCommunication email = emailCommunicationRepository.findById(Long.parseLong(emailId))
            .orElseThrow(() -> new RuntimeException("Email not found"));
        email.setIsOpened(true);
        email.setOpenedAt(LocalDateTime.now());
        emailCommunicationRepository.save(email);
    }

    @Override
    @Transactional
    public void trackEmailClick(String emailId, String linkId) {
        createEmailEvent(emailId, null, null, EmailEventType.CLICKED, "Link: " + linkId);
        auditLogService.logSystemActivity("EMAIL_LINK_CLICKED", "EMAIL", null);
        
        EmailCommunication email = emailCommunicationRepository.findById(Long.parseLong(emailId))
            .orElseThrow(() -> new RuntimeException("Email not found"));
        email.setClickCount(email.getClickCount() + 1);
        emailCommunicationRepository.save(email);
    }

    @Override
    @Transactional(readOnly = true)
    public EmailStatsDTO getEmailStats(LocalDateTime startDate, LocalDateTime endDate) {
        EmailStatsDTO stats = new EmailStatsDTO();
        
        List<Object[]> eventCounts = emailEventRepository.countByEventType(startDate, endDate);
        Map<String, Long> statusCounts = new HashMap<>();
        for (Object[] result : eventCounts) {
            EmailEventType type = (EmailEventType) result[0];
            Long count = (Long) result[1];
            statusCounts.put(type.name(), count);
        }
        stats.setStatusCounts(statusCounts);
        
        List<Object[]> dailyStats = emailEventRepository.countByDateAndEventType(startDate, endDate);
        Map<LocalDateTime, Long> sentByDate = new HashMap<>();
        Map<LocalDateTime, Long> openedByDate = new HashMap<>();
        Map<LocalDateTime, Long> clickedByDate = new HashMap<>();
        
        for (Object[] result : dailyStats) {
            LocalDateTime date = (LocalDateTime) result[0];
            EmailEventType type = (EmailEventType) result[1];
            Long count = (Long) result[2];
            
            switch (type) {
                case SENT -> sentByDate.put(date, count);
                case OPENED -> openedByDate.put(date, count);
                case CLICKED -> clickedByDate.put(date, count);
            }
        }
        
        stats.setSentByDate(sentByDate);
        stats.setOpenedByDate(openedByDate);
        stats.setClickedByDate(clickedByDate);
        
        long totalSent = sentByDate.values().stream().mapToLong(Long::longValue).sum();
        long totalOpened = openedByDate.values().stream().mapToLong(Long::longValue).sum();
        long totalClicked = clickedByDate.values().stream().mapToLong(Long::longValue).sum();
        
        stats.setTotalSent(totalSent);
        stats.setTotalOpened(totalOpened);
        stats.setTotalClicked(totalClicked);
        stats.setOpenRate(totalSent > 0 ? (double) totalOpened / totalSent : 0.0);
        stats.setClickRate(totalSent > 0 ? (double) totalClicked / totalSent : 0.0);
        
        return stats;
    }

    @Override
    public EmailDTO sendPasswordResetEmail(String to, String resetToken) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("subject", "Password Reset Request");
        templateVariables.put("resetToken", resetToken);
        templateVariables.put("resetLink", "/reset-password?token=" + resetToken);
        
        return sendTemplatedEmail(to, "password-reset", templateVariables);
    }

    @Override
    public EmailDTO sendWelcomeEmail(String to, String username) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("subject", "Welcome to SaaS CRM");
        templateVariables.put("username", username);
        
        return sendTemplatedEmail(to, "welcome", templateVariables);
    }

    @Override
    public EmailDTO sendDealUpdateEmail(String to, String dealTitle, String status) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("subject", "Deal Update: " + dealTitle);
        templateVariables.put("dealTitle", dealTitle);
        templateVariables.put("status", status);
        
        return sendTemplatedEmail(to, "deal-update", templateVariables);
    }

    @Override
    public EmailDTO sendTaskAssignmentEmail(String to, String taskTitle, LocalDateTime dueDate) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("subject", "New Task Assignment: " + taskTitle);
        templateVariables.put("taskTitle", taskTitle);
        templateVariables.put("dueDate", dueDate);
        
        return sendTemplatedEmail(to, "task-assignment", templateVariables);
    }

    @Override
    public EmailDTO sendMeetingReminder(String to, String meetingTitle, LocalDateTime meetingTime, String location) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("subject", "Meeting Reminder: " + meetingTitle);
        templateVariables.put("meetingTitle", meetingTitle);
        templateVariables.put("meetingTime", meetingTime);
        templateVariables.put("location", location);
        
        return sendTemplatedEmail(to, "meeting-reminder", templateVariables);
    }

    private void createEmailEvent(String emailId, String recipient, String subject, EmailEventType eventType, String metadata) {
        EmailEvent event = new EmailEvent();
        event.setEmailId(emailId);
        event.setRecipient(recipient);
        event.setSubject(subject);
        event.setEventType(eventType);
        event.setMetadata(metadata);
        emailEventRepository.save(event);
    }

    private void validateAndCheckRateLimit(String to) {
        if (!emailValidationService.isValidEmail(to)) {
            throw new IllegalArgumentException("Invalid email address: " + to);
        }
        
        String domain = to.substring(to.indexOf("@") + 1);
        if (!emailValidationService.isValidDomain(domain)) {
            throw new IllegalArgumentException("Invalid email domain: " + domain);
        }
        
        if (emailValidationService.isDisposableEmail(to)) {
            throw new IllegalArgumentException("Disposable email addresses are not allowed: " + to);
        }
        
        if (emailValidationService.isRateLimited(fromEmail)) {
            throw new RuntimeException("Email rate limit exceeded. Please try again later.");
        }
    }
} 