package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.CustomerStatus;

import java.time.LocalDateTime;

public interface ReportService {
    byte[] generateSalesReport(LocalDateTime startDate, LocalDateTime endDate, String format);

    byte[] generateCustomerReport(CustomerStatus status, String format);

    byte[] generateDealsPipelineReport(String format);

    byte[] generateActivityReport(LocalDateTime startDate, LocalDateTime endDate, String format);

    byte[] generateUserPerformanceReport(Long userId, LocalDateTime startDate, LocalDateTime endDate, String format);

    byte[] generateEmailCampaignReport(Long templateId, String format);
}
