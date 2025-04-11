package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.SendStatus;
import com.project.SaasCRM.domain.dto.EmailCommunicationDTO;
import com.project.SaasCRM.exception.ResourceNotFoundException;
import com.project.SaasCRM.exception.UnauthorizedException;
import com.project.SaasCRM.security.SecurityService;
import com.project.SaasCRM.service.EmailCommunicationService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
@Tag(name = "Email Communication Management", description = "APIs for managing email communications")
public class EmailCommunicationController {
    private final EmailCommunicationService emailCommunicationService;
    private final SecurityService securityService;

    @Operation(summary = "Get all emails with pagination", description = "Returns a paginated list of all emails")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved email list",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<Page<EmailCommunicationDTO>> getAllEmails(
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(emailCommunicationService.findAllEmailCommunicationsPaginated(pageable));
    }

    @Operation(summary = "Create a new email", description = "Creates a new email in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Email successfully created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmailCommunicationDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    public ResponseEntity<EmailCommunicationDTO> createEmail(@Valid @RequestBody EmailCommunicationDTO emailDTO) {
        return new ResponseEntity<>(emailCommunicationService.saveEmailCommunication(emailDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Get email by ID", description = "Returns an email by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved email",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmailCommunicationDTO.class))),
        @ApiResponse(responseCode = "404", description = "Email not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmailCommunicationDTO> getEmailById(@Parameter(description = "ID of the email") @PathVariable Long id) {
        return emailCommunicationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update an email", description = "Updates email information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmailCommunicationDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Email not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EmailCommunicationDTO> updateEmail(
            @Parameter(description = "ID of the email") @PathVariable Long id,
            @Valid @RequestBody EmailCommunicationDTO emailDTO) {
        // Ensure the ID in the path matches the ID in the body
        if (!id.equals(emailDTO.getId())) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(emailCommunicationService.updateEmailCommunication(emailDTO));
    }

    @Operation(summary = "Delete an email", description = "Deletes an email from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Email successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Email not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmail(@Parameter(description = "ID of the email") @PathVariable Long id) {
        emailCommunicationService.deleteEmailCommunication(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get emails by customer", description = "Returns emails for a specific customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved emails",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access this customer")
    })
    @GetMapping("/customer/{id}")
    public ResponseEntity<List<EmailCommunicationDTO>> getEmailsByCustomer(
            @Parameter(description = "ID of the customer") @PathVariable Long id) {
        // Check if user has access to this customer
        if (!securityService.isAdmin() && !securityService.canAccessCustomer(id)) {
            throw new UnauthorizedException("You are not authorized to access this customer's emails");
        }
        
        return ResponseEntity.ok(emailCommunicationService.findEmailsByCustomer(id));
    }

    @Operation(summary = "Get emails by sender", description = "Returns emails sent by a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved emails",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Not authorized to access these emails")
    })
    @GetMapping("/user/{id}")
    public ResponseEntity<List<EmailCommunicationDTO>> getEmailsByUser(
            @Parameter(description = "ID of the user") @PathVariable Long id) {
        // Ensure user can only access their own emails unless they're an admin
        if (!securityService.isAdmin() && !securityService.isCurrentUser(id)) {
            throw new UnauthorizedException("You are not authorized to access these emails");
        }
        
        return ResponseEntity.ok(emailCommunicationService.findEmailsBySentBy(id));
    }

    @Operation(summary = "Get emails by template", description = "Returns emails generated from a specific template")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved emails",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/template/{id}")
    public ResponseEntity<List<EmailCommunicationDTO>> getEmailsByTemplate(
            @Parameter(description = "ID of the email template") @PathVariable Long id) {
        return ResponseEntity.ok(emailCommunicationService.findEmailsByTemplate(id));
    }

    @Operation(summary = "Get emails by status", description = "Returns emails filtered by send status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved emails",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmailCommunicationDTO>> getEmailsByStatus(
            @Parameter(description = "Email send status") @PathVariable SendStatus status) {
        return ResponseEntity.ok(emailCommunicationService.findEmailsByStatus(status));
    }

    @Operation(summary = "Get opened emails", description = "Returns emails that have been opened by recipients")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved opened emails",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/opened")
    public ResponseEntity<List<EmailCommunicationDTO>> getOpenedEmails() {
        return ResponseEntity.ok(emailCommunicationService.findOpenedEmails());
    }

    @Operation(summary = "Send email", description = "Sends a previously created email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email successfully sent",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmailCommunicationDTO.class))),
        @ApiResponse(responseCode = "404", description = "Email not found")
    })
    @PostMapping("/{id}/send")
    public ResponseEntity<EmailCommunicationDTO> sendEmail(
            @Parameter(description = "ID of the email") @PathVariable Long id) {
        return ResponseEntity.ok(emailCommunicationService.sendEmail(id));
    }

    @Operation(summary = "Schedule email", description = "Schedules an email to be sent at a later time")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email successfully scheduled",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmailCommunicationDTO.class))),
        @ApiResponse(responseCode = "404", description = "Email not found")
    })
    @PostMapping("/{id}/schedule")
    public ResponseEntity<EmailCommunicationDTO> scheduleEmail(
            @Parameter(description = "ID of the email") @PathVariable Long id,
            @Parameter(description = "Scheduled date and time") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledTime) {
        return ResponseEntity.ok(emailCommunicationService.scheduleEmail(id, scheduledTime));
    }

    @Operation(summary = "Mark email as opened", description = "Records that an email has been opened by the recipient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Email successfully marked as opened"),
        @ApiResponse(responseCode = "404", description = "Email not found")
    })
    @PutMapping("/{id}/opened")
    public ResponseEntity<Void> markEmailAsOpened(
            @Parameter(description = "ID of the email") @PathVariable Long id) {
        emailCommunicationService.markEmailAsOpened(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Increment email click count", description = "Records that a link in the email has been clicked")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Email click count successfully incremented"),
        @ApiResponse(responseCode = "404", description = "Email not found")
    })
    @PutMapping("/{id}/clicked")
    public ResponseEntity<Void> incrementEmailClickCount(
            @Parameter(description = "ID of the email") @PathVariable Long id) {
        emailCommunicationService.incrementEmailClickCount(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Create email from template", description = "Creates a new email based on a template for a specific customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Email successfully created from template",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmailCommunicationDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Not authorized to access this customer"),
        @ApiResponse(responseCode = "404", description = "Template or customer not found")
    })
    @PostMapping("/from-template")
    public ResponseEntity<EmailCommunicationDTO> createEmailFromTemplate(
            @Parameter(description = "ID of the email template") @RequestParam Long templateId,
            @Parameter(description = "ID of the customer") @RequestParam Long customerId,
            @Parameter(description = "Custom subject (optional)") @RequestParam(required = false) String subject) {
        // Check if user has access to this customer
        if (!securityService.isAdmin() && !securityService.canAccessCustomer(customerId)) {
            throw new UnauthorizedException("You are not authorized to create emails for this customer");
        }
        
        return new ResponseEntity<>(emailCommunicationService.createEmailFromTemplate(templateId, customerId, subject), HttpStatus.CREATED);
    }

    @Operation(summary = "Get email statistics", description = "Returns statistics about emails by date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved email statistics",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getEmailStats(
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(emailCommunicationService.getEmailCountsByDate(startDate, endDate));
    }

    @Operation(summary = "Get email open rate", description = "Returns the percentage of opened emails for the specified date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved email open rate",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Double.class)))
    })
    @GetMapping("/open-rate")
    public ResponseEntity<Double> getEmailOpenRate(
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(emailCommunicationService.calculateEmailOpenRate(startDate, endDate));
    }

    @Operation(summary = "Get email click rate", description = "Returns the percentage of emails with clicked links for the specified date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved email click rate",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Double.class)))
    })
    @GetMapping("/click-rate")
    public ResponseEntity<Double> getEmailClickRate(
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(emailCommunicationService.calculateEmailClickRate(startDate, endDate));
    }
} 