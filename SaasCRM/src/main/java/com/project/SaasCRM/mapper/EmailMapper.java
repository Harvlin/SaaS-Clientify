package com.project.SaasCRM.mapper;

import com.project.SaasCRM.domain.entity.EmailCommunication;
import com.project.SaasCRM.domain.dto.EmailDTO;
import com.project.SaasCRM.domain.SendStatus;
import org.mapstruct.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmailMapper {
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "to", source = "recipientEmail")
    @Mapping(target = "subject", source = "subject")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "templateName", source = "emailTemplate.name")
    @Mapping(target = "templateVariables", source = "emailTemplate.variables")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "status", source = "sendStatus")
    @Mapping(target = "sentAt", source = "sentAt")
    @Mapping(target = "scheduledAt", source = "scheduledFor")
    @Mapping(target = "openCount", expression = "java(email.getIsOpened() != null && email.getIsOpened() ? 1 : 0)")
    @Mapping(target = "clickCount", source = "clickCount")
    @Mapping(target = "errorMessage", ignore = true)
    @Mapping(target = "userId", source = "sentBy.id")
    @Mapping(target = "customerId", source = "customer.id")
    EmailDTO toDto(EmailCommunication email);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "recipientEmail", source = "to")
    @Mapping(target = "subject", source = "subject")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "emailTemplate", ignore = true)
    @Mapping(target = "sendStatus", source = "status")
    @Mapping(target = "sentAt", source = "sentAt")
    @Mapping(target = "scheduledFor", source = "scheduledAt")
    @Mapping(target = "isOpened", expression = "java(dto.getOpenCount() != null && dto.getOpenCount() > 0)")
    @Mapping(target = "clickCount", source = "clickCount")
    @Mapping(target = "sentBy", ignore = true)
    @Mapping(target = "customer", ignore = true)
    EmailCommunication toEntity(EmailDTO dto);

    @AfterMapping
    default void handleNullValues(EmailDTO dto, @MappingTarget EmailCommunication email) {
        if (dto.getSentAt() == null) {
            email.setSentAt(LocalDateTime.now());
        }
        if (dto.getScheduledAt() == null) {
            email.setScheduledFor(LocalDateTime.now());
        }
        if (dto.getClickCount() == null) {
            email.setClickCount(0);
        }
        if (dto.getStatus() == null) {
            email.setSendStatus(SendStatus.PENDING);
        }
    }
} 