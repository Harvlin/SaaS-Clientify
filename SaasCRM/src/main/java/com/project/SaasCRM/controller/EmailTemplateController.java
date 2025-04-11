package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.dto.EmailTemplateDTO;
import com.project.SaasCRM.exception.ResourceNotFoundException;
import com.project.SaasCRM.exception.UnauthorizedException;
import com.project.SaasCRM.security.SecurityService;
import com.project.SaasCRM.service.EmailTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/email-templates")
@RequiredArgsConstructor
@Tag(name = "Email Template Management", description = "APIs for managing email templates")
public class EmailTemplateController {
    private final EmailTemplateService emailTemplateService;
    private final SecurityService securityService;

    @Operation(summary = "Get all email templates with pagination", description = "Returns a paginated list of all email templates")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved email template list",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<Page<EmailTemplateDTO>> getAllEmailTemplates(
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(emailTemplateService.findAllTemplatesPaginated(pageable));
    }

    @Operation(summary = "Create a new email template", description = "Creates a new email template in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Email template successfully created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmailTemplateDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmailTemplateDTO> createEmailTemplate(@Valid @RequestBody EmailTemplateDTO emailTemplateDTO) {
        return new ResponseEntity<>(emailTemplateService.createTemplate(emailTemplateDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Get email template by ID", description = "Returns an email template by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved email template",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmailTemplateDTO.class))),
        @ApiResponse(responseCode = "404", description = "Email template not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmailTemplateDTO> getEmailTemplateById(@Parameter(description = "ID of the email template") @PathVariable Long id) {
        return emailTemplateService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update an email template", description = "Updates email template information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email template successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmailTemplateDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Email template not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmailTemplateDTO> updateEmailTemplate(
            @Parameter(description = "ID of the email template") @PathVariable Long id,
            @Valid @RequestBody EmailTemplateDTO emailTemplateDTO) {
        // Ensure the ID in the path matches the ID in the body
        if (!id.equals(emailTemplateDTO.getId())) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(emailTemplateService.updateTemplate(emailTemplateDTO));
    }

    @Operation(summary = "Delete an email template", description = "Deletes an email template from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Email template successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Email template not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteEmailTemplate(@Parameter(description = "ID of the email template") @PathVariable Long id) {
        emailTemplateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get email templates by type", description = "Returns email templates filtered by type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved email templates",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/type/{type}")
    public ResponseEntity<List<EmailTemplateDTO>> getEmailTemplatesByType(
            @Parameter(description = "Email template type") @PathVariable String type) {
        return ResponseEntity.ok(emailTemplateService.findEmailTemplatesByType(type));
    }

    @Operation(summary = "Get email templates by creator", description = "Returns email templates created by the specified user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved email templates",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access these templates")
    })
    @GetMapping("/user/{id}")
    public ResponseEntity<List<EmailTemplateDTO>> getEmailTemplatesByUser(
            @Parameter(description = "ID of the user") @PathVariable Long id) {
        // Ensure user can only access their own templates unless they're an admin
        if (!securityService.isAdmin() && !securityService.isCurrentUser(id)) {
            throw new UnauthorizedException("You are not authorized to access these email templates");
        }
        
        return ResponseEntity.ok(emailTemplateService.findEmailTemplatesByCreatedBy(id));
    }

    @Operation(summary = "Process template for customer", description = "Processes an email template for a specific customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully processed email template",
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "403", description = "Not authorized to access this customer"),
        @ApiResponse(responseCode = "404", description = "Email template or customer not found")
    })
    @GetMapping("/{id}/process/{customerId}")
    public ResponseEntity<String> processTemplateForCustomer(
            @Parameter(description = "ID of the email template") @PathVariable Long id,
            @Parameter(description = "ID of the customer") @PathVariable Long customerId) {
        // Check if user has access to this customer
        if (!securityService.isAdmin() && !securityService.canAccessCustomer(customerId)) {
            throw new UnauthorizedException("You are not authorized to process templates for this customer");
        }
        
        return ResponseEntity.ok(emailTemplateService.processTemplateForCustomer(id, customerId));
    }
    
    @Operation(summary = "Get active email templates", description = "Returns a list of active email templates")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved active email templates",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/active")
    public ResponseEntity<List<EmailTemplateDTO>> getActiveEmailTemplates() {
        return ResponseEntity.ok(emailTemplateService.findActiveTemplates());
    }
    
    @Operation(summary = "Activate email template", description = "Activates an email template")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Email template successfully activated"),
        @ApiResponse(responseCode = "404", description = "Email template not found")
    })
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> activateEmailTemplate(
            @Parameter(description = "ID of the email template") @PathVariable Long id) {
        emailTemplateService.activateTemplate(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Deactivate email template", description = "Deactivates an email template")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Email template successfully deactivated"),
        @ApiResponse(responseCode = "404", description = "Email template not found")
    })
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deactivateEmailTemplate(
            @Parameter(description = "ID of the email template") @PathVariable Long id) {
        emailTemplateService.deactivateTemplate(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Get most used email templates", description = "Returns a list of most frequently used email templates")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved most used email templates",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/most-used")
    public ResponseEntity<List<EmailTemplateDTO>> getMostUsedEmailTemplates() {
        return ResponseEntity.ok(emailTemplateService.findMostUsedTemplates());
    }
} 