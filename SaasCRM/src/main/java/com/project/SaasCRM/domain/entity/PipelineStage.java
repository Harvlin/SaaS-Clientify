package com.project.SaasCRM.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "pipeline_stages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PipelineStage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "default_probability_percentage")
    private Integer defaultProbabilityPercentage;

    @OneToMany(mappedBy = "pipelineStage")
    private Set<Deal> deals = new HashSet<>();
}
