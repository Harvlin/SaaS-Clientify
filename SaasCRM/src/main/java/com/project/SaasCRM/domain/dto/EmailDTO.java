package com.project.SaasCRM.domain.dto;

import com.project.SaasCRM.domain.SendStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDTO {
    private Long id;
    private String to;
    private String subject;
    private String content;
    private String templateName;
    private Map<String, Object> templateVariables;
    private List<String> attachments;
    private SendStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime scheduledAt;
    private Integer openCount;
    private Integer clickCount;
    private String errorMessage;
    private Long userId;
    private Long customerId;
} 