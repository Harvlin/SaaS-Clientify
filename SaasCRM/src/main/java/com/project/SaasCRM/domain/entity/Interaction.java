package com.project.SaasCRM.domain.entity;

import com.project.SaasCRM.domain.InteractionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "interactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "interaction_date", nullable = false)
    private LocalDateTime interactionDate;

    @Column(name = "interaction_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private InteractionType type;

    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}