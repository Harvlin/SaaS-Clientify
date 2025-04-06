package com.project.SaasCRM.mapper;

import com.project.SaasCRM.domain.entity.PipelineStage;
import com.project.SaasCRM.domain.dto.PipelineStageDTO;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PipelineStageMapper extends BaseMapper<PipelineStage, PipelineStageDTO> {
    
    @Mapping(target = "dealIds", expression = "java(getDealIds(pipelineStage))")
    @Override
    PipelineStageDTO toDto(PipelineStage pipelineStage);

    @Mapping(target = "deals", ignore = true)
    @Override
    PipelineStage toEntity(PipelineStageDTO dto);

    default Set<Long> getDealIds(PipelineStage pipelineStage) {
        if (pipelineStage.getDeals() == null) return null;
        return pipelineStage.getDeals().stream()
                .map(deal -> deal.getId())
                .collect(Collectors.toSet());
    }
} 