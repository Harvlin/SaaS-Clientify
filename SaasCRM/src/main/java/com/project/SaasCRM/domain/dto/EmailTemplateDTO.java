package com.project.SaasCRM.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EmailTemplateDTO {
    private Long id;
    private String name;
    private String subjectTemplate;
    private String contentTemplate;
    private String description;
    private boolean active;
    private String variables;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastUsedAt;
    private Long usageCount;
    private Long version;
} 