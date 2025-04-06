package com.project.SaasCRM.mapper;

import com.project.SaasCRM.domain.CustomerStatus;
import com.project.SaasCRM.domain.DealStage;
import com.project.SaasCRM.domain.TaskStatus;
import com.project.SaasCRM.domain.dto.DashboardDTO;
import com.project.SaasCRM.domain.dto.AuditLogDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface DashboardMapper {
    
    @Mapping(target = "recentActivities", source = "recentActivities")
    @Mapping(target = "customersByStatus", source = "customersByStatus")
    @Mapping(target = "dealsByStage", source = "dealsByStage")
    @Mapping(target = "dealValuesByStage", source = "dealValuesByStage")
    @Mapping(target = "tasksByStatus", source = "tasksByStatus")
    DashboardDTO toDto(
        Long totalCustomers,
        Map<CustomerStatus, Long> customersByStatus,
        Map<DealStage, Long> dealsByStage,
        Map<DealStage, BigDecimal> dealValuesByStage,
        Map<TaskStatus, Long> tasksByStatus,
        List<AuditLogDTO> recentActivities
    );

    @Named("defaultIfNull")
    default Long defaultIfNull(Long value) {
        return value != null ? value : 0L;
    }

    @Named("defaultIfNull")
    default BigDecimal defaultIfNull(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    @Named("defaultIfNull")
    default <T> Map<T, Long> defaultIfNull(Map<T, Long> value) {
        return value != null ? value : Map.of();
    }

    @Named("defaultIfNull")
    default <T> Map<T, BigDecimal> defaultIfNullBigDecimal(Map<T, BigDecimal> value) {
        return value != null ? value : Map.of();
    }

    @Named("defaultIfNull")
    default List<AuditLogDTO> defaultIfNull(List<AuditLogDTO> value) {
        return value != null ? value : List.of();
    }
} 