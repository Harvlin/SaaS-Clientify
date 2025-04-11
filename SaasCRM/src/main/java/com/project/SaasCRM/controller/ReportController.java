package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.CustomerStatus;
import com.project.SaasCRM.exception.UnauthorizedException;
import com.project.SaasCRM.security.SecurityService;
import com.project.SaasCRM.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Report Generation", description = "APIs for generating various reports")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class ReportController {
    private final ReportService reportService;
    private final SecurityService securityService;

    @Operation(summary = "Generate sales report", description = "Generates a sales report for the specified period")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report successfully generated",
            content = @Content(mediaType = "application/octet-stream")),
        @ApiResponse(responseCode = "403", description = "Not authorized to generate reports")
    })
    @GetMapping("/sales")
    public ResponseEntity<byte[]> generateSalesReport(
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Report format (pdf, xlsx, csv)") @RequestParam(defaultValue = "pdf") String format) {
        byte[] reportContent = reportService.generateSalesReport(startDate, endDate, format);
        
        return createReportResponse(reportContent, "sales_report", format);
    }

    @Operation(summary = "Generate customer report", description = "Generates a customer report with optional status filter")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report successfully generated",
            content = @Content(mediaType = "application/octet-stream")),
        @ApiResponse(responseCode = "403", description = "Not authorized to generate reports")
    })
    @GetMapping("/customers")
    public ResponseEntity<byte[]> generateCustomerReport(
            @Parameter(description = "Customer status filter") @RequestParam(required = false) CustomerStatus status,
            @Parameter(description = "Report format (pdf, xlsx, csv)") @RequestParam(defaultValue = "pdf") String format) {
        byte[] reportContent = reportService.generateCustomerReport(status, format);
        
        return createReportResponse(reportContent, "customer_report", format);
    }

    @Operation(summary = "Generate pipeline report", description = "Generates a report of the deals pipeline")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report successfully generated",
            content = @Content(mediaType = "application/octet-stream")),
        @ApiResponse(responseCode = "403", description = "Not authorized to generate reports")
    })
    @GetMapping("/pipeline")
    public ResponseEntity<byte[]> generatePipelineReport(
            @Parameter(description = "Report format (pdf, xlsx, csv)") @RequestParam(defaultValue = "pdf") String format) {
        byte[] reportContent = reportService.generateDealsPipelineReport(format);
        
        return createReportResponse(reportContent, "pipeline_report", format);
    }

    @Operation(summary = "Generate activity report", description = "Generates a report of activities for the specified period")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report successfully generated",
            content = @Content(mediaType = "application/octet-stream")),
        @ApiResponse(responseCode = "403", description = "Not authorized to generate reports")
    })
    @GetMapping("/activity")
    public ResponseEntity<byte[]> generateActivityReport(
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Report format (pdf, xlsx, csv)") @RequestParam(defaultValue = "pdf") String format) {
        byte[] reportContent = reportService.generateActivityReport(startDate, endDate, format);
        
        return createReportResponse(reportContent, "activity_report", format);
    }

    @Operation(summary = "Generate user performance report", description = "Generates a performance report for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report successfully generated",
            content = @Content(mediaType = "application/octet-stream")),
        @ApiResponse(responseCode = "403", description = "Not authorized to generate this report")
    })
    @GetMapping("/user-performance")
    public ResponseEntity<byte[]> generateUserPerformanceReport(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Report format (pdf, xlsx, csv)") @RequestParam(defaultValue = "pdf") String format) {
        // Only admins and managers can see other users' reports, and users can see their own reports
        if (!securityService.isAdmin() && !securityService.isCurrentUser(userId)) {
            throw new UnauthorizedException("You are not authorized to view this user's performance report");
        }
        
        byte[] reportContent = reportService.generateUserPerformanceReport(userId, startDate, endDate, format);
        
        return createReportResponse(reportContent, "user_performance_report", format);
    }

    @Operation(summary = "Generate email campaign report", description = "Generates a report for an email campaign based on template")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report successfully generated",
            content = @Content(mediaType = "application/octet-stream")),
        @ApiResponse(responseCode = "403", description = "Not authorized to generate this report")
    })
    @GetMapping("/email-campaign")
    public ResponseEntity<byte[]> generateEmailCampaignReport(
            @Parameter(description = "Email template ID") @RequestParam Long templateId,
            @Parameter(description = "Report format (pdf, xlsx, csv)") @RequestParam(defaultValue = "pdf") String format) {
        byte[] reportContent = reportService.generateEmailCampaignReport(templateId, format);
        
        return createReportResponse(reportContent, "email_campaign_report", format);
    }
    
    /**
     * Creates a proper HTTP response for a report with the appropriate headers
     */
    private ResponseEntity<byte[]> createReportResponse(byte[] content, String reportName, String format) {
        HttpHeaders headers = new HttpHeaders();
        String filename = reportName + "." + format.toLowerCase();
        headers.setContentDispositionFormData("attachment", filename);
        
        MediaType mediaType = switch (format.toLowerCase()) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "xlsx" -> MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "csv" -> MediaType.parseMediaType("text/csv");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
        
        headers.setContentType(mediaType);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(content);
    }
} 