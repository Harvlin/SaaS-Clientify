package com.project.SaasCRM.domain.entity;

import com.project.SaasCRM.domain.PriorityLevel;
import com.project.SaasCRM.domain.TaskStatus;
import com.project.SaasCRM.domain.TaskType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "task_type")
    @Enumerated(EnumType.STRING)
    private TaskType type;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "reminder_date")
    private LocalDateTime reminderDate;

    @Column(name = "task_status")
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(name = "priority_level")
    @Enumerated(EnumType.STRING)
    private PriorityLevel priorityLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deal_id")
    private Deal deal;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
