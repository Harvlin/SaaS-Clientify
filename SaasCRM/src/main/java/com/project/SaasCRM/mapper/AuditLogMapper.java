package com.project.SaasCRM.mapper;

import com.project.SaasCRM.domain.entity.AuditLog;
import com.project.SaasCRM.domain.dto.AuditLogDTO;
import org.mapstruct.*;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AuditLogMapper {
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "activity", source = "activity")
    @Mapping(target = "entityType", source = "entityType")
    @Mapping(target = "entityId", source = "entityId")
    @Mapping(target = "timestamp", source = "timestamp")
    @Mapping(target = "details", source = "details")
    @Mapping(target = "systemActivity", source = "systemActivity")
    AuditLogDTO toDto(AuditLog auditLog);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "activity", source = "activity")
    @Mapping(target = "entityType", source = "entityType")
    @Mapping(target = "entityId", source = "entityId")
    @Mapping(target = "timestamp", source = "timestamp")
    @Mapping(target = "details", source = "details")
    @Mapping(target = "systemActivity", source = "systemActivity")
    AuditLog toEntity(AuditLogDTO auditLogDTO);

    @AfterMapping
    default void handleNullValues(AuditLogDTO dto, @MappingTarget AuditLog auditLog) {
        if (dto.getTimestamp() == null) {
            auditLog.setTimestamp(LocalDateTime.now());
        }
        if (dto.getDetails() == null) {
            auditLog.setDetails("");
        }
        if (dto.getActivity() == null) {
            auditLog.setActivity("UNKNOWN");
        }
        if (dto.getEntityType() == null) {
            auditLog.setEntityType("UNKNOWN");
        }
        if (dto.getEntityId() == null) {
            auditLog.setEntityId(0L);
        }
    }
} 