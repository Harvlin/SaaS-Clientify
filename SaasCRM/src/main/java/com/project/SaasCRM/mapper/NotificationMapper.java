package com.project.SaasCRM.mapper;

import com.project.SaasCRM.domain.entity.Notification;
import com.project.SaasCRM.domain.dto.NotificationDTO;
import org.mapstruct.*;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationMapper {
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "entityType", source = "entityType")
    @Mapping(target = "entityId", source = "entityId")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "readAt", source = "readAt")
    @Mapping(target = "isRead", source = "read")
    @Mapping(target = "notificationType", source = "notificationType")
    @Mapping(target = "dueDate", source = "dueDate")
    NotificationDTO toDto(Notification notification);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "message", source = "message")
    @Mapping(target = "entityType", source = "entityType")
    @Mapping(target = "entityId", source = "entityId")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "readAt", source = "readAt")
    @Mapping(target = "read", source = "isRead")
    @Mapping(target = "notificationType", source = "notificationType")
    @Mapping(target = "dueDate", source = "dueDate")
    Notification toEntity(NotificationDTO notificationDTO);

    @AfterMapping
    default void handleNullValues(NotificationDTO dto, @MappingTarget Notification notification) {
        if (dto.getCreatedAt() == null) {
            notification.setCreatedAt(LocalDateTime.now());
        }
        if (dto.getMessage() == null) {
            notification.setMessage("");
        }
        if (dto.getNotificationType() == null) {
            notification.setNotificationType("SYSTEM");
        }
        if (dto.getEntityId() == null) {
            notification.setEntityId(0L);
        }
        if (dto.getIsRead() == null) {
            notification.setRead(false);
        }
    }
} 