package com.project.SaasCRM.mapper;

import com.project.SaasCRM.domain.entity.EmailTemplate;
import com.project.SaasCRM.domain.dto.EmailTemplateDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EmailTemplateMapper extends BaseMapper<EmailTemplate, EmailTemplateDTO> {
    
    @Override
    EmailTemplateDTO toDto(EmailTemplate emailTemplate);

    @Override
    EmailTemplate toEntity(EmailTemplateDTO dto);
} 