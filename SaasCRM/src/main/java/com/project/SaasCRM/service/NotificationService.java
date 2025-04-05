package com.project.SaasCRM.service;

import java.util.List;
import java.util.Map;

public interface NotificationService {
    void sendTaskReminder(Long taskId);

    void sendDealStageChangedNotification(Long dealId);

    void sendNewCustomerAssignedNotification(Long customerId, Long userId);

    void sendTaskAssignedNotification(Long taskId, Long userId);

    void sendEmailOpenedNotification(Long emailId);

    void sendDealClosedNotification(Long dealId);

    List<Map<String, Object>> getUserNotifications(Long userId);

    void markNotificationAsRead(Long notificationId);
}