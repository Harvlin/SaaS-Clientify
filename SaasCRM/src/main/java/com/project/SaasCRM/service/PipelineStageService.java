package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.dto.PipelineStageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PipelineStageService {
    PipelineStageDTO savePipelineStage(PipelineStageDTO pipelineStage);

    PipelineStageDTO updatePipelineStage(PipelineStageDTO pipelineStage);

    void deletePipelineStage(Long stageId);

    Optional<PipelineStageDTO> findById(Long stageId);

    Optional<PipelineStageDTO> findByName(String name);

    List<PipelineStageDTO> findAllPipelineStages();

    List<PipelineStageDTO> findAllPipelineStagesOrdered();

    void reorderPipelineStages(List<Long> stageIds);

    Map<String, Long> getDealCountsByStage();
}
