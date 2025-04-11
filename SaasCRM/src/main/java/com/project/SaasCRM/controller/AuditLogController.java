package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.dto.AuditLogDTO;
import com.project.SaasCRM.exception.UnauthorizedException;
import com.project.SaasCRM.security.SecurityService;
import com.project.SaasCRM.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit Log Management", description = "APIs for accessing system audit logs")
public class AuditLogController {
    private final AuditLogService auditLogService;
    private final SecurityService securityService;

    @Operation(summary = "Get activity logs by user", description = "Returns audit logs for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved audit logs",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access these logs")
    })
    @GetMapping("/user/{id}")
    public ResponseEntity<List<AuditLogDTO>> getUserActivityLogs(
            @Parameter(description = "ID of the user") @PathVariable Long id,
            @Parameter(description = "Maximum number of logs to return") @RequestParam(required = false, defaultValue = "20") int limit,
            @Parameter(description = "Start date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        // Ensure user can only access their own logs unless they're an admin
        if (!securityService.isAdmin() && !securityService.isCurrentUser(id)) {
            throw new UnauthorizedException("You are not authorized to access these activity logs");
        }
        
        if (startDate != null && endDate != null) {
            return ResponseEntity.ok(auditLogService.getUserActivityLogs(id, startDate, endDate));
        } else {
            return ResponseEntity.ok(auditLogService.getUserActivityLogs(id, limit));
        }
    }

    @Operation(summary = "Get activity logs by entity", description = "Returns audit logs for a specific entity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved audit logs",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/entity/{type}/{id}")
    public ResponseEntity<List<AuditLogDTO>> getEntityActivityLogs(
            @Parameter(description = "Type of the entity") @PathVariable String type,
            @Parameter(description = "ID of the entity") @PathVariable Long id,
            @Parameter(description = "Maximum number of logs to return") @RequestParam(required = false, defaultValue = "0") int limit) {
        
        // Implement appropriate access control for entity logs
        // For example, verify if user can access the specified entity (this can be expanded based on security requirements)
        if (!securityService.isAdmin() && !canAccessEntityLogs(type, id)) {
            throw new UnauthorizedException("You are not authorized to access these entity logs");
        }
        
        if (limit > 0) {
            return ResponseEntity.ok(auditLogService.getEntityActivityLogs(type, id, limit));
        } else {
            return ResponseEntity.ok(auditLogService.getEntityActivityLogs(type, id));
        }
    }
    
    @Operation(summary = "Get recent activities", description = "Returns recent system activity logs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved recent activities",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access system logs")
    })
    @GetMapping("/recent")
    public ResponseEntity<List<AuditLogDTO>> getRecentActivities(
            @Parameter(description = "Maximum number of logs to return") @RequestParam(required = false, defaultValue = "20") int limit) {
        // Only admins can access system-wide activity logs
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You are not authorized to access system-wide activity logs");
        }
        
        return ResponseEntity.ok(auditLogService.getRecentActivities(limit));
    }
    
    /**
     * Determines if the current user can access audit logs for the given entity.
     * This method can be expanded with more detailed logic based on entity types.
     */
    private boolean canAccessEntityLogs(String entityType, Long entityId) {
        // Example implementation - customize based on your security requirements
        switch (entityType.toUpperCase()) {
            case "CUSTOMER":
                return securityService.canAccessCustomer(entityId);
            case "DEAL":
                return securityService.canAccessDeal(entityId);
            case "TASK":
                // Security service doesn't have canAccessTask, so fall back to a safe default
                // A better approach might be to check if the user is assigned to the task
                return false;
            case "USER":
                return securityService.isCurrentUser(entityId);
            default:
                // For other entity types, deny access by default unless admin
                return false;
        }
    }
} 