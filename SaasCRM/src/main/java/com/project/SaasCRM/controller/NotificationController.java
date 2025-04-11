package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.dto.NotificationDTO;
import com.project.SaasCRM.exception.UnauthorizedException;
import com.project.SaasCRM.security.SecurityService;
import com.project.SaasCRM.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "APIs for managing user notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final SecurityService securityService;

    /**
     * Get notifications for a user
     *
     * @param id User ID
     * @param page Page number (optional)
     * @param size Page size (optional)
     * @return List of user notifications
     */
    @Operation(summary = "Get notifications for user", description = "Returns a list of notifications for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved notifications",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access these notifications")
    })
    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<List<NotificationDTO>> getNotificationsForUser(
            @Parameter(description = "ID of the user") @PathVariable Long id,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        List<NotificationDTO> notifications = notificationService.getAllNotifications(id, page, size);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Mark a notification as read
     *
     * @param id Notification ID
     * @return No content response
     */
    @Operation(summary = "Mark notification as read", description = "Marks a specific notification as read")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Notification successfully marked as read"),
        @ApiResponse(responseCode = "403", description = "Not authorized to modify this notification"),
        @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markNotificationAsRead(@Parameter(description = "ID of the notification") @PathVariable Long id) {
        // Get the notification to check if it belongs to the current user
        NotificationDTO notification = notificationService.getNotificationById(id);
        
        // Verify that the current user can modify this notification
        if (!securityService.isAdmin() && !securityService.isCurrentUser(notification.getUserId())) {
            throw new UnauthorizedException("You are not authorized to modify this notification");
        }
        
        notificationService.markNotificationAsRead(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get count of unread notifications for a user
     *
     * @param userId User ID
     * @return Count of unread notifications
     */
    @Operation(summary = "Get unread notification count", description = "Returns the count of unread notifications for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved unread notification count"),
        @ApiResponse(responseCode = "403", description = "Not authorized to access this data")
    })
    @GetMapping("/user/{id}/unread/count")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<Long> getUnreadNotificationCount(
            @Parameter(description = "ID of the user") @PathVariable("id") Long id) {
        
        long count = notificationService.getUnreadNotificationCount(id);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Mark all notifications as read for a user
     *
     * @param id User ID
     * @return No content response
     */
    @Operation(summary = "Mark all notifications as read", description = "Marks all notifications as read for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Notifications successfully marked as read"),
        @ApiResponse(responseCode = "403", description = "Not authorized to modify these notifications")
    })
    @PutMapping("/user/{id}/read-all")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<Void> markAllNotificationsAsRead(
            @Parameter(description = "ID of the user") @PathVariable Long id) {
        
        notificationService.markAllNotificationsAsRead(id);
        return ResponseEntity.noContent().build();
    }
} 