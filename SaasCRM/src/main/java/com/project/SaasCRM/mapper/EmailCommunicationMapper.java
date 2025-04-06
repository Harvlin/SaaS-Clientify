package com.project.SaasCRM.mapper;

import com.project.SaasCRM.domain.entity.EmailCommunication;
import com.project.SaasCRM.domain.dto.EmailCommunicationDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EmailCommunicationMapper extends BaseMapper<EmailCommunication, EmailCommunicationDTO> {
    
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "sentByUserId", source = "sentBy.id")
    @Mapping(target = "emailTemplateId", source = "emailTemplate.id")
    @Override
    EmailCommunicationDTO toDto(EmailCommunication emailCommunication);

    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "sentBy", ignore = true)
    @Mapping(target = "emailTemplate", ignore = true)
    @Override
    EmailCommunication toEntity(EmailCommunicationDTO dto);
} 