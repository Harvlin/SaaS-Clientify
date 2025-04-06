package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.dto.EmailTemplateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EmailTemplateService {
    // CRUD Operations
    EmailTemplateDTO createTemplate(EmailTemplateDTO template);
    EmailTemplateDTO updateTemplate(EmailTemplateDTO template);
    void deleteTemplate(Long templateId);
    
    // Find Operations
    Optional<EmailTemplateDTO> findById(Long templateId);
    Optional<EmailTemplateDTO> findByName(String name);
    List<EmailTemplateDTO> findAllTemplates();
    Page<EmailTemplateDTO> findAllTemplatesPaginated(Pageable pageable);
    List<EmailTemplateDTO> findActiveTemplates();
    List<EmailTemplateDTO> findEmailTemplatesByType(String templateType);
    List<EmailTemplateDTO> findEmailTemplatesByCreatedBy(Long userId);
    
    // Template Processing
    String processTemplate(String templateName, Map<String, Object> variables);
    String processTemplateForCustomer(Long templateId, Long customerId);
    void validateTemplate(String templateContent, List<String> requiredVariables);
    
    // Template Management
    void activateTemplate(Long templateId);
    void deactivateTemplate(Long templateId);
    void recordTemplateUsage(Long templateId);
    
    // Analytics
    List<EmailTemplateDTO> findUnusedTemplates(LocalDateTime since);
    List<EmailTemplateDTO> findMostUsedTemplates();
}
