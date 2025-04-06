package com.project.SaasCRM.mapper;

import com.project.SaasCRM.domain.entity.Report;
import com.project.SaasCRM.domain.dto.ReportDTO;
import org.mapstruct.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReportMapper {
    
    @Mapping(target = "reportType", source = "type")
    @Mapping(target = "format", source = "format")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "parameters", source = "parameters")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "fileName", source = "fileName")
    @Mapping(target = "mimeType", source = "mimeType")
    ReportDTO toDto(Report report);

    @Mapping(target = "type", source = "reportType")
    @Mapping(target = "format", source = "format")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "parameters", source = "parameters")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "fileName", source = "fileName")
    @Mapping(target = "mimeType", source = "mimeType")
    @Mapping(target = "createdBy", ignore = true)
    Report toEntity(ReportDTO reportDTO);

    @AfterMapping
    default void handleNullValues(ReportDTO dto, @MappingTarget Report report) {
        if (dto.getStartDate() == null) {
            report.setStartDate(LocalDateTime.now());
        }
        if (dto.getEndDate() == null) {
            report.setEndDate(LocalDateTime.now());
        }
        if (dto.getParameters() == null) {
            report.setParameters(new HashMap<>());
        }
        if (dto.getFileName() == null) {
            report.setFileName("report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
        }
        if (dto.getMimeType() == null) {
            report.setMimeType("application/pdf");
        }
        if (dto.getReportType() == null) {
            report.setType("GENERAL");
        }
        if (dto.getFormat() == null) {
            report.setFormat("PDF");
        }
    }
} 