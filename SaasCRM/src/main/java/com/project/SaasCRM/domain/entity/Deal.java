package com.project.SaasCRM.domain.entity;

import com.project.SaasCRM.domain.DealStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "deals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_stage_id", nullable = false)
    private PipelineStage pipelineStage;

    @Column(name = "value_amount")
    private Double valueAmount;

    @Column(name = "value_currency")
    private String valueCurrency;

    @Column(name = "expected_close_date")
    private LocalDateTime expectedCloseDate;

    @Column(name = "actual_close_date")
    private LocalDateTime actualCloseDate;

    @Column(name = "deal_status")
    @Enumerated(EnumType.STRING)
    private DealStatus status;

    @Column(name = "probability_percentage")
    private Integer probabilityPercentage;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "deal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasks = new HashSet<>();

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
