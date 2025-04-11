package com.project.SaasCRM.mapper;

import com.project.SaasCRM.domain.entity.EmailCommunication;
import com.project.SaasCRM.domain.dto.EmailDTO;
import com.project.SaasCRM.domain.SendStatus;
import org.mapstruct.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;
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
    @Mapping(target = "openCount", expression = "java(email.isOpened() != null && email.isOpened() ? 1 : 0)")
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
    
    default Map<String, Object> stringToMap(String value) {
        if (value == null || value.isEmpty()) {
            return new HashMap<>();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(value, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }
    }
    
    default String mapToString(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}