package com.project.SaasCRM.mapper;

import com.project.SaasCRM.domain.entity.Customer;
import com.project.SaasCRM.domain.dto.CustomerDTO;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CustomerMapper extends BaseMapper<Customer, CustomerDTO> {
    
    @Mapping(target = "assignedUserIds", expression = "java(getAssignedUserIds(customer))")
    @Mapping(target = "dealIds", expression = "java(getDealIds(customer))")
    @Override
    CustomerDTO toDto(Customer customer);

    @Mapping(target = "assignedUsers", ignore = true)
    @Mapping(target = "deals", ignore = true)
    @Override
    Customer toEntity(CustomerDTO dto);

    default Set<Long> getAssignedUserIds(Customer customer) {
        if (customer.getAssignedUsers() == null) return null;
        return customer.getAssignedUsers().stream()
                .map(user -> user.getId())
                .collect(Collectors.toSet());
    }

    default Set<Long> getDealIds(Customer customer) {
        if (customer.getDeals() == null) return null;
        return customer.getDeals().stream()
                .map(deal -> deal.getId())
                .collect(Collectors.toSet());
    }
} 