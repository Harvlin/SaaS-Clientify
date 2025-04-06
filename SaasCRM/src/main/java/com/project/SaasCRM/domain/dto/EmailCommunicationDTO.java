package com.project.SaasCRM.domain.dto;

import com.project.SaasCRM.domain.SendStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EmailCommunicationDTO {
    private Long id;
    private String subject;
    private String content;
    private String senderEmail;
    private String recipientEmail;
    private String ccEmails;
    private String bccEmails;
    private LocalDateTime sentAt;
    private Boolean isOpened;
    private LocalDateTime openedAt;
    private Integer clickCount;
    private LocalDateTime createdAt;
    private Long customerId;
    private Long sentByUserId;
    private Long emailTemplateId;
    private LocalDateTime scheduledFor;
    private SendStatus sendStatus;
} 