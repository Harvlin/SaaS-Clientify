package com.project.SaasCRM.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private String reportType;
    private String format;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Map<String, Object> parameters;
    private byte[] content;
    private String fileName;
    private String mimeType;
} 