package com.project.SaasCRM.domain.dto;

import lombok.Data;
import java.util.Set;

@Data
public class PipelineStageDTO {
    private Long id;
    private String name;
    private String description;
    private Integer displayOrder;
    private Integer defaultProbabilityPercentage;
    private Set<Long> dealIds;
} 