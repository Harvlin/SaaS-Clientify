package com.project.SaasCRM.mapper;

import com.project.SaasCRM.domain.entity.Interaction;
import com.project.SaasCRM.domain.dto.InteractionDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface InteractionMapper extends BaseMapper<Interaction, InteractionDTO> {
    
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "userId", source = "user.id")
    @Override
    InteractionDTO toDto(Interaction interaction);

    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Override
    Interaction toEntity(InteractionDTO dto);
} 