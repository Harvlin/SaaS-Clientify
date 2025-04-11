package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.dto.NotificationDTO;
import java.time.LocalDateTime;
import java.util.List;

public interface NotificationService {
    void sendInteractionNotification(Long userId, Long interactionId, String message);
    
    void sendDealNotification(Long userId, Long dealId, String message);
    
    void sendTaskNotification(Long userId, Long taskId, String message);
    
    void sendCustomerNotification(Long userId, Long customerId, String message);
    
    void sendSystemNotification(Long userId, String message);
    
    void sendReminderNotification(Long userId, String entityType, Long entityId, LocalDateTime dueDate, String message);
    
    void sendTaskReminder(Long taskId);
    
    void sendDealStageChangedNotification(Long dealId);
    
    void sendNewCustomerAssignedNotification(Long customerId, Long userId);
    
    void sendTaskAssignedNotification(Long taskId, Long userId);
    
    void sendEmailOpenedNotification(Long emailId);
    
    void sendDealClosedNotification(Long dealId);
    
    void markNotificationAsRead(Long notificationId);
    
    void markAllNotificationsAsRead(Long userId);
    
    List<NotificationDTO> getUnreadNotifications(Long userId);
    
    List<NotificationDTO> getAllNotifications(Long userId, int page, int size);
    
    long getUnreadNotificationCount(Long userId);
    
    void deleteNotification(Long notificationId);
    
    void deleteAllNotifications(Long userId);
    
    /**
     * Get a notification by ID
     *
     * @param notificationId Notification ID
     * @return Notification DTO
     */
    NotificationDTO getNotificationById(Long notificationId);
}