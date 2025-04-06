package com.project.SaasCRM.service.impl;

import com.project.SaasCRM.domain.entity.EmailTemplate;
import com.project.SaasCRM.domain.dto.EmailTemplateDTO;
import com.project.SaasCRM.repository.EmailTemplateRepository;
import com.project.SaasCRM.service.EmailTemplateService;
import com.project.SaasCRM.service.AuditLogService;
import com.project.SaasCRM.mapper.EmailTemplateMapper;
import com.project.SaasCRM.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private final EmailTemplateRepository emailTemplateRepository;
    private final TemplateEngine templateEngine;
    private final EmailTemplateMapper emailTemplateMapper;
    private final AuditLogService auditLogService;

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    @Override
    @Transactional
    public EmailTemplateDTO createTemplate(EmailTemplateDTO templateDTO) {
        if (emailTemplateRepository.existsByName(templateDTO.getName())) {
            throw new IllegalArgumentException("Template with name " + templateDTO.getName() + " already exists");
        }
        validateTemplate(templateDTO.getContentTemplate(), null);
        EmailTemplate template = emailTemplateMapper.toEntity(templateDTO);
        EmailTemplate savedTemplate = emailTemplateRepository.save(template);
        auditLogService.logSystemActivity("TEMPLATE_CREATED", "EMAIL_TEMPLATE", savedTemplate.getId());
        return emailTemplateMapper.toDto(savedTemplate);
    }

    @Override
    @Transactional
    public EmailTemplateDTO updateTemplate(EmailTemplateDTO templateDTO) {
        EmailTemplate existingTemplate = emailTemplateRepository.findById(templateDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + templateDTO.getId()));
        
        validateTemplate(templateDTO.getContentTemplate(), null);
        EmailTemplate template = emailTemplateMapper.toEntity(templateDTO);
        EmailTemplate updatedTemplate = emailTemplateRepository.save(template);
        auditLogService.logSystemActivity("TEMPLATE_UPDATED", "EMAIL_TEMPLATE", updatedTemplate.getId());
        return emailTemplateMapper.toDto(updatedTemplate);
    }

    @Override
    @Transactional
    public void deleteTemplate(Long templateId) {
        EmailTemplate template = emailTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + templateId));
        emailTemplateRepository.delete(template);
        auditLogService.logSystemActivity("TEMPLATE_DELETED", "EMAIL_TEMPLATE", templateId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmailTemplateDTO> findById(Long templateId) {
        return emailTemplateRepository.findById(templateId)
                .map(emailTemplateMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmailTemplateDTO> findByName(String name) {
        return emailTemplateRepository.findByName(name)
                .map(emailTemplateMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailTemplateDTO> findAllTemplates() {
        return emailTemplateMapper.toDtoList(emailTemplateRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailTemplateDTO> findActiveTemplates() {
        return emailTemplateMapper.toDtoList(emailTemplateRepository.findByActive(true));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailTemplateDTO> findUnusedTemplates(LocalDateTime since) {
        return emailTemplateMapper.toDtoList(emailTemplateRepository.findUnusedActiveTemplates(since));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailTemplateDTO> findMostUsedTemplates() {
        return emailTemplateMapper.toDtoList(emailTemplateRepository.findMostUsedTemplates());
    }

    @Override
    @Transactional
    public String processTemplate(String templateName, Map<String, Object> variables) {
        EmailTemplate template = emailTemplateRepository.findByName(templateName)
            .orElseThrow(() -> new ResourceNotFoundException("Template not found: " + templateName));
            
        validateTemplate(template.getContentTemplate(), variables.keySet().stream().toList());
        
        Context context = new Context();
        context.setVariables(variables);
        
        recordTemplateUsage(template.getId());
        
        return templateEngine.process(templateName, context);
    }

    @Override
    public void validateTemplate(String templateContent, List<String> requiredVariables) {
        if (templateContent == null || templateContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Template content cannot be empty");
        }

        // Extract variables from template
        Matcher matcher = VARIABLE_PATTERN.matcher(templateContent);
        while (matcher.find()) {
            String variable = matcher.group(1);
            if (requiredVariables != null && !requiredVariables.contains(variable)) {
                throw new IllegalArgumentException("Template contains undefined variable: " + variable);
            }
        }

        // Check if all required variables are used
        if (requiredVariables != null) {
            for (String variable : requiredVariables) {
                if (!templateContent.contains("${" + variable + "}")) {
                    throw new IllegalArgumentException("Required variable not used in template: " + variable);
                }
            }
        }
    }

    @Override
    @Transactional
    public void activateTemplate(Long templateId) {
        EmailTemplate template = emailTemplateRepository.findById(templateId)
            .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + templateId));
        template.setActive(true);
        emailTemplateRepository.save(template);
        auditLogService.logSystemActivity("TEMPLATE_ACTIVATED", "EMAIL_TEMPLATE", templateId);
    }

    @Override
    @Transactional
    public void deactivateTemplate(Long templateId) {
        EmailTemplate template = emailTemplateRepository.findById(templateId)
            .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + templateId));
        template.setActive(false);
        emailTemplateRepository.save(template);
        auditLogService.logSystemActivity("TEMPLATE_DEACTIVATED", "EMAIL_TEMPLATE", templateId);
    }

    @Override
    @Transactional
    public void recordTemplateUsage(Long templateId) {
        emailTemplateRepository.incrementUsageCount(templateId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmailTemplateDTO> findAllTemplatesPaginated(Pageable pageable) {
        return emailTemplateRepository.findAll(pageable)
                .map(emailTemplateMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailTemplateDTO> findEmailTemplatesByType(String templateType) {
        return emailTemplateMapper.toDtoList(emailTemplateRepository.findByType(templateType));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailTemplateDTO> findEmailTemplatesByCreatedBy(Long userId) {
        return emailTemplateMapper.toDtoList(emailTemplateRepository.findByCreatedBy(userId));
    }

    @Override
    @Transactional
    public String processTemplateForCustomer(Long templateId, Long customerId) {
        EmailTemplate template = emailTemplateRepository.findById(templateId)
            .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + templateId));
            
        // Add customer-specific variables here
        Map<String, Object> variables = Map.of(
            "customerId", customerId,
            "timestamp", LocalDateTime.now()
        );
        
        validateTemplate(template.getContentTemplate(), variables.keySet().stream().toList());
        
        Context context = new Context();
        context.setVariables(variables);
        
        recordTemplateUsage(template.getId());
        
        return templateEngine.process(template.getName(), context);
    }
} 