package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.entity.EmailTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EmailTemplateService {
    EmailTemplate saveEmailTemplate(EmailTemplate emailTemplate);

    EmailTemplate updateEmailTemplate(EmailTemplate emailTemplate);

    void deleteEmailTemplate(Long templateId);

    Optional<EmailTemplate> findById(Long templateId);

    Optional<EmailTemplate> findByName(String name);

    List<EmailTemplate> findAllEmailTemplates();

    Page<EmailTemplate> findAllEmailTemplatesPaginated(Pageable pageable);

    List<EmailTemplate> findEmailTemplatesByType(String templateType);

    List<EmailTemplate> findEmailTemplatesByCreatedBy(Long userId);

    String processTemplateForCustomer(Long templateId, Long customerId);
}
