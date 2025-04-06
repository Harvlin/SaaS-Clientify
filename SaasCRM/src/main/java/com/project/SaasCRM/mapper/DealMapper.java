package com.project.SaasCRM.mapper;

import com.project.SaasCRM.domain.entity.Deal;
import com.project.SaasCRM.domain.dto.DealDTO;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DealMapper extends BaseMapper<Deal, DealDTO> {
    
    @Mapping(target = "assignedUserIds", expression = "java(getAssignedUserIds(deal))")
    @Mapping(target = "taskIds", expression = "java(getTaskIds(deal))")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "probabilityPercentage", source = "probabilityPercentage")
    @Override
    DealDTO toDto(Deal deal);

    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "assignedUsers", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Override
    Deal toEntity(DealDTO dto);

    default Set<Long> getAssignedUserIds(Deal deal) {
        if (deal == null || deal.getAssignedUsers() == null) {
            return null;
        }
        return deal.getAssignedUsers().stream()
                .map(user -> user.getId())
                .collect(Collectors.toSet());
    }

    default Set<Long> getTaskIds(Deal deal) {
        if (deal == null || deal.getTasks() == null) {
            return null;
        }
        return deal.getTasks().stream()
                .map(task -> task.getId())
                .collect(Collectors.toSet());
    }

    @AfterMapping
    default void handleNullValues(DealDTO dto, @MappingTarget Deal deal) {
        if (dto.getValue() == null) {
            deal.setValue(BigDecimal.ZERO);
        }
        if (dto.getProbabilityPercentage() == null) {
            deal.setProbabilityPercentage(0);
        }
    }
} 