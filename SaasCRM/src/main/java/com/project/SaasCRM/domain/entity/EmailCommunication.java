package com.project.SaasCRM.domain.entity;

import com.project.SaasCRM.domain.SendStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_communications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailCommunication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "sender_email", nullable = false)
    private String senderEmail;

    @Column(name = "recipient_email", nullable = false)
    private String recipientEmail;

    @Column(name = "cc_emails")
    private String ccEmails;

    @Column(name = "bcc_emails")
    private String bccEmails;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "is_opened")
    private Boolean isOpened;

    @Column(name = "opened_at")
    private LocalDateTime openedAt;

    @Column(name = "click_count")
    private Integer clickCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sent_by_user_id")
    private User sentBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_template_id")
    private EmailTemplate emailTemplate;

    @Column(name = "scheduled_for")
    private LocalDateTime scheduledFor;

    @Column(name = "send_status")
    @Enumerated(EnumType.STRING)
    private SendStatus sendStatus;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.clickCount = 0;
        this.isOpened = false;
    }
}