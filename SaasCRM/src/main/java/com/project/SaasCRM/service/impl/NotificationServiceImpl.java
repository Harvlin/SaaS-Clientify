package com.project.SaasCRM.service.impl;

import com.project.SaasCRM.domain.entity.Deal;
import com.project.SaasCRM.domain.entity.Task;
import com.project.SaasCRM.domain.entity.Customer;
import com.project.SaasCRM.domain.entity.User;
import com.project.SaasCRM.domain.entity.Notification;
import com.project.SaasCRM.domain.dto.NotificationDTO;
import com.project.SaasCRM.repository.DealRepository;
import com.project.SaasCRM.repository.TaskRepository;
import com.project.SaasCRM.repository.CustomerRepository;
import com.project.SaasCRM.repository.UserRepository;
import com.project.SaasCRM.repository.NotificationRepository;
import com.project.SaasCRM.service.NotificationService;
import com.project.SaasCRM.service.EmailService;
import com.project.SaasCRM.mapper.NotificationMapper;
import com.project.SaasCRM.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final TaskRepository taskRepository;
    private final DealRepository dealRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public void sendTaskReminder(Long taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
            
        String message = String.format("Task '%s' is due on %s", 
            task.getTitle(), 
            task.getDueDate().toString());
            
        Notification notification = Notification.builder()
            .user(task.getAssignee())
            .message(message)
            .entityType("TASK")
            .entityId(taskId)
            .createdAt(LocalDateTime.now())
            .dueDate(task.getDueDate())
            .notificationType("REMINDER")
            .build();
            
        notificationRepository.save(notification);
        emailService.sendEmail(task.getAssignee().getEmail(), "Task Reminder", message);
    }

    @Override
    @Transactional
    public void sendDealStageChangedNotification(Long dealId) {
        Deal deal = dealRepository.findById(dealId)
            .orElseThrow(() -> new ResourceNotFoundException("Deal not found with id: " + dealId));
            
        String message = String.format("Deal '%s' has moved to stage '%s'", 
            deal.getName(), 
            deal.getStage());
            
        for (User user : deal.getAssignedUsers()) {
            Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .entityType("DEAL")
                .entityId(dealId)
                .createdAt(LocalDateTime.now())
                .notificationType("DEAL")
                .build();
                
            notificationRepository.save(notification);
            emailService.sendEmail(user.getEmail(), "Deal Stage Changed", message);
        }
    }

    @Override
    @Transactional
    public void sendNewCustomerAssignedNotification(Long customerId, Long userId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
            
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            
        String message = String.format("You have been assigned to customer '%s'", 
            customer.getName());
            
        Notification notification = Notification.builder()
            .user(user)
            .message(message)
            .entityType("CUSTOMER")
            .entityId(customerId)
            .createdAt(LocalDateTime.now())
            .notificationType("ASSIGNMENT")
            .build();
            
        notificationRepository.save(notification);
        emailService.sendEmail(user.getEmail(), "New Customer Assignment", message);
    }

    @Override
    @Transactional
    public void sendTaskAssignedNotification(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
            
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            
        String message = String.format("You have been assigned to task '%s'", 
            task.getTitle());
            
        Notification notification = Notification.builder()
            .user(user)
            .message(message)
            .entityType("TASK")
            .entityId(taskId)
            .createdAt(LocalDateTime.now())
            .notificationType("ASSIGNMENT")
            .build();
            
        notificationRepository.save(notification);
        emailService.sendEmail(user.getEmail(), "New Task Assignment", message);
    }

    @Override
    @Transactional
    public void sendEmailOpenedNotification(Long emailId) {
        // Assuming we have an EmailCommunication entity
        String message = "Your email has been opened";
            
        Notification notification = Notification.builder()
            .message(message)
            .entityType("EMAIL")
            .entityId(emailId)
            .createdAt(LocalDateTime.now())
            .notificationType("EMAIL")
            .build();
            
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void sendDealClosedNotification(Long dealId) {
        Deal deal = dealRepository.findById(dealId)
            .orElseThrow(() -> new ResourceNotFoundException("Deal not found with id: " + dealId));
            
        String message = String.format("Deal '%s' has been closed", 
            deal.getName());
            
        for (User user : deal.getAssignedUsers()) {
            Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .entityType("DEAL")
                .entityId(dealId)
                .createdAt(LocalDateTime.now())
                .notificationType("DEAL")
                .build();
                
            notificationRepository.save(notification);
            emailService.sendEmail(user.getEmail(), "Deal Closed", message);
        }
    }

    @Override
    @Transactional
    public void sendInteractionNotification(Long userId, Long interactionId, String message) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            
        Notification notification = Notification.builder()
            .user(user)
            .message(message)
            .entityType("INTERACTION")
            .entityId(interactionId)
            .createdAt(LocalDateTime.now())
            .notificationType("INTERACTION")
            .build();
            
        notificationRepository.save(notification);
        emailService.sendEmail(user.getEmail(), "New Interaction", message);
    }

    @Override
    @Transactional
    public void sendDealNotification(Long userId, Long dealId, String message) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            
        Notification notification = Notification.builder()
            .user(user)
            .message(message)
            .entityType("DEAL")
            .entityId(dealId)
            .createdAt(LocalDateTime.now())
            .notificationType("DEAL")
            .build();
            
        notificationRepository.save(notification);
        emailService.sendEmail(user.getEmail(), "Deal Update", message);
    }

    @Override
    @Transactional
    public void sendTaskNotification(Long userId, Long taskId, String message) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            
        Notification notification = Notification.builder()
            .user(user)
            .message(message)
            .entityType("TASK")
            .entityId(taskId)
            .createdAt(LocalDateTime.now())
            .notificationType("TASK")
            .build();
            
        notificationRepository.save(notification);
        emailService.sendEmail(user.getEmail(), "Task Update", message);
    }

    @Override
    @Transactional
    public void sendCustomerNotification(Long userId, Long customerId, String message) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            
        Notification notification = Notification.builder()
            .user(user)
            .message(message)
            .entityType("CUSTOMER")
            .entityId(customerId)
            .createdAt(LocalDateTime.now())
            .notificationType("CUSTOMER")
            .build();
            
        notificationRepository.save(notification);
        emailService.sendEmail(user.getEmail(), "Customer Update", message);
    }

    @Override
    @Transactional
    public void sendSystemNotification(Long userId, String message) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            
        Notification notification = Notification.builder()
            .user(user)
            .message(message)
            .createdAt(LocalDateTime.now())
            .notificationType("SYSTEM")
            .build();
            
        notificationRepository.save(notification);
        emailService.sendEmail(user.getEmail(), "System Notification", message);
    }

    @Override
    @Transactional
    public void sendReminderNotification(Long userId, String entityType, Long entityId, LocalDateTime dueDate, String message) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            
        Notification notification = Notification.builder()
            .user(user)
            .message(message)
            .entityType(entityType)
            .entityId(entityId)
            .createdAt(LocalDateTime.now())
            .dueDate(dueDate)
            .notificationType("REMINDER")
            .build();
            
        notificationRepository.save(notification);
        emailService.sendEmail(user.getEmail(), "Reminder", message);
    }

    @Override
    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
            
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllNotificationsAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
        LocalDateTime now = LocalDateTime.now();
        
        notifications.forEach(notification -> {
            notification.setRead(true);
            notification.setReadAt(now);
        });
        
        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId).stream()
            .map(notificationMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getAllNotifications(Long userId, int page, int size) {
        return notificationRepository.findByUserId(
            userId, 
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).stream()
            .map(notificationMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadNotificationCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
            
        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public void deleteAllNotifications(Long userId) {
        notificationRepository.deleteByUserId(userId);
    }
} 