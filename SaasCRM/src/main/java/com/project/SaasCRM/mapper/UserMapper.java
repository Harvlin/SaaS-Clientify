package com.project.SaasCRM.mapper;

import com.project.SaasCRM.domain.entity.User;
import com.project.SaasCRM.domain.dto.UserDTO;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "lastLogin", source = "lastLogin")
    UserDTO toDto(User user);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "lastLogin", source = "lastLogin")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "resetToken", ignore = true)
    @Mapping(target = "resetTokenExpiry", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "assignedCustomers", ignore = true)
    @Mapping(target = "assignedDeals", ignore = true)
    @Mapping(target = "assignedTasks", ignore = true)
    User toEntity(UserDTO userDTO);

    @AfterMapping
    default void handleNullValues(UserDTO dto, @MappingTarget User user) {
        if (dto.getFullName() == null) {
            user.setFullName("");
        }
        if (dto.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        if (dto.getLastLogin() == null) {
            user.setLastLogin(LocalDateTime.now());
        }
    }

    default Set<Long> getAssignedCustomerIds(User user) {
        if (user.getAssignedCustomers() == null) return null;
        return user.getAssignedCustomers().stream()
                .map(customer -> customer.getId())
                .collect(Collectors.toSet());
    }

    default Set<Long> getAssignedDealIds(User user) {
        if (user.getAssignedDeals() == null) return null;
        return user.getAssignedDeals().stream()
                .map(deal -> deal.getId())
                .collect(Collectors.toSet());
    }

    default Set<Long> getAssignedTaskIds(User user) {
        if (user.getAssignedTasks() == null) return null;
        return user.getAssignedTasks().stream()
                .map(task -> task.getId())
                .collect(Collectors.toSet());
    }
} 