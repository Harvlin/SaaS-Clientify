package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.dto.EmailDTO;
import com.project.SaasCRM.domain.dto.EmailStatsDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EmailService {
    EmailDTO sendEmail(String to, String subject, String content);
    
    EmailDTO sendEmailWithAttachment(String to, String subject, String content, String attachmentPath);
    
    EmailDTO sendTemplatedEmail(String to, String templateName, Map<String, Object> templateVariables);
    
    List<EmailDTO> sendBulkEmail(List<String> recipients, String subject, String content);
    
    List<EmailDTO> sendBulkTemplatedEmail(List<String> recipients, String templateName, Map<String, Object> templateVariables);
    
    EmailDTO scheduleEmail(String to, String subject, String content, LocalDateTime scheduledTime);
    
    EmailDTO scheduleTemplatedEmail(String to, String templateName, Map<String, Object> templateVariables, LocalDateTime scheduledTime);
    
    void trackEmailOpen(String emailId);
    
    void trackEmailClick(String emailId, String linkId);
    
    EmailStatsDTO getEmailStats(LocalDateTime startDate, LocalDateTime endDate);
    
    EmailDTO sendPasswordResetEmail(String to, String resetToken);
    
    EmailDTO sendWelcomeEmail(String to, String username);
    
    EmailDTO sendDealUpdateEmail(String to, String dealTitle, String status);
    
    EmailDTO sendTaskAssignmentEmail(String to, String taskTitle, LocalDateTime dueDate);
    
    EmailDTO sendMeetingReminder(String to, String meetingTitle, LocalDateTime meetingTime, String location);
} 