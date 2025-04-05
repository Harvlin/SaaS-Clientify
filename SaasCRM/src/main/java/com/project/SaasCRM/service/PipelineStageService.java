package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.entity.PipelineStage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PipelineStageService {
    PipelineStage savePipelineStage(PipelineStage pipelineStage);

    PipelineStage updatePipelineStage(PipelineStage pipelineStage);

    void deletePipelineStage(Long stageId);

    Optional<PipelineStage> findById(Long stageId);

    Optional<PipelineStage> findByName(String name);

    List<PipelineStage> findAllPipelineStages();

    List<PipelineStage> findAllPipelineStagesOrdered();

    void reorderPipelineStages(List<Long> stageIds);

    Map<String, Long> getDealCountsByStage();
}
