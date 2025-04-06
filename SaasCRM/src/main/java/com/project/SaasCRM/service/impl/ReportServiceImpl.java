package com.project.SaasCRM.service.impl;

import com.project.SaasCRM.domain.CustomerStatus;
import com.project.SaasCRM.domain.dto.ReportDTO;
import com.project.SaasCRM.domain.entity.Report;
import com.project.SaasCRM.domain.entity.User;
import com.project.SaasCRM.mapper.ReportMapper;
import com.project.SaasCRM.repository.ReportRepository;
import com.project.SaasCRM.service.ReportService;
import com.project.SaasCRM.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public byte[] generateSalesReport(LocalDateTime startDate, LocalDateTime endDate, String format) {
        // Get the current user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Create report parameters
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("startDate", startDate);
        parameters.put("endDate", endDate);
        parameters.put("format", format);
        
        // Generate report content (this would be implemented with a report generation library)
        byte[] content = generateReportContent("sales", parameters);
        
        // Create and save report entity
        Report report = Report.builder()
            .type("SALES")
            .format(format)
            .startDate(startDate)
            .endDate(endDate)
            .parameters(parameters)
            .content(content)
            .fileName("sales_report." + format.toLowerCase())
            .mimeType(getMimeType(format))
            .createdBy(currentUser)
            .build();
        
        report = reportRepository.save(report);
        auditLogService.logUserActivity(currentUser.getId(), "GENERATE_SALES_REPORT", "REPORT", report.getId());
        
        return content;
    }

    @Override
    @Transactional
    public byte[] generateCustomerReport(CustomerStatus status, String format) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("status", status);
        parameters.put("format", format);
        
        byte[] content = generateReportContent("customer", parameters);
        
        Report report = Report.builder()
            .type("CUSTOMER")
            .format(format)
            .parameters(parameters)
            .content(content)
            .fileName("customer_report." + format.toLowerCase())
            .mimeType(getMimeType(format))
            .createdBy(currentUser)
            .build();
        
        report = reportRepository.save(report);
        auditLogService.logUserActivity(currentUser.getId(), "GENERATE_CUSTOMER_REPORT", "REPORT", report.getId());
        
        return content;
    }

    @Override
    @Transactional
    public byte[] generateDealsPipelineReport(String format) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("format", format);
        
        byte[] content = generateReportContent("deals_pipeline", parameters);
        
        Report report = Report.builder()
            .type("DEALS_PIPELINE")
            .format(format)
            .parameters(parameters)
            .content(content)
            .fileName("deals_pipeline_report." + format.toLowerCase())
            .mimeType(getMimeType(format))
            .createdBy(currentUser)
            .build();
        
        report = reportRepository.save(report);
        auditLogService.logUserActivity(currentUser.getId(), "GENERATE_DEALS_PIPELINE_REPORT", "REPORT", report.getId());
        
        return content;
    }

    @Override
    @Transactional
    public byte[] generateActivityReport(LocalDateTime startDate, LocalDateTime endDate, String format) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("startDate", startDate);
        parameters.put("endDate", endDate);
        parameters.put("format", format);
        
        byte[] content = generateReportContent("activity", parameters);
        
        Report report = Report.builder()
            .type("ACTIVITY")
            .format(format)
            .startDate(startDate)
            .endDate(endDate)
            .parameters(parameters)
            .content(content)
            .fileName("activity_report." + format.toLowerCase())
            .mimeType(getMimeType(format))
            .createdBy(currentUser)
            .build();
        
        report = reportRepository.save(report);
        auditLogService.logUserActivity(currentUser.getId(), "GENERATE_ACTIVITY_REPORT", "REPORT", report.getId());
        
        return content;
    }

    @Override
    @Transactional
    public byte[] generateUserPerformanceReport(Long userId, LocalDateTime startDate, LocalDateTime endDate, String format) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("userId", userId);
        parameters.put("startDate", startDate);
        parameters.put("endDate", endDate);
        parameters.put("format", format);
        
        byte[] content = generateReportContent("user_performance", parameters);
        
        Report report = Report.builder()
            .type("USER_PERFORMANCE")
            .format(format)
            .startDate(startDate)
            .endDate(endDate)
            .parameters(parameters)
            .content(content)
            .fileName("user_performance_report." + format.toLowerCase())
            .mimeType(getMimeType(format))
            .createdBy(currentUser)
            .build();
        
        report = reportRepository.save(report);
        auditLogService.logUserActivity(currentUser.getId(), "GENERATE_USER_PERFORMANCE_REPORT", "REPORT", report.getId());
        
        return content;
    }

    @Override
    @Transactional
    public byte[] generateEmailCampaignReport(Long templateId, String format) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("templateId", templateId);
        parameters.put("format", format);
        
        byte[] content = generateReportContent("email_campaign", parameters);
        
        Report report = Report.builder()
            .type("EMAIL_CAMPAIGN")
            .format(format)
            .parameters(parameters)
            .content(content)
            .fileName("email_campaign_report." + format.toLowerCase())
            .mimeType(getMimeType(format))
            .createdBy(currentUser)
            .build();
        
        report = reportRepository.save(report);
        auditLogService.logUserActivity(currentUser.getId(), "GENERATE_EMAIL_CAMPAIGN_REPORT", "REPORT", report.getId());
        
        return content;
    }

    private byte[] generateReportContent(String reportType, Map<String, Object> parameters) {
        // This would be implemented with a report generation library like JasperReports
        // For now, return an empty byte array
        return new byte[0];
    }

    private String getMimeType(String format) {
        return switch (format.toLowerCase()) {
            case "pdf" -> "application/pdf";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "csv" -> "text/csv";
            default -> "application/octet-stream";
        };
    }
} 