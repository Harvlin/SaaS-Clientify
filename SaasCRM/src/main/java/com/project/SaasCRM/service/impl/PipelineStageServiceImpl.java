package com.project.SaasCRM.service.impl;

import com.project.SaasCRM.domain.entity.PipelineStage;
import com.project.SaasCRM.domain.dto.PipelineStageDTO;
import com.project.SaasCRM.repository.PipelineStageRepository;
import com.project.SaasCRM.service.PipelineStageService;
import com.project.SaasCRM.service.AuditLogService;
import com.project.SaasCRM.mapper.PipelineStageMapper;
import com.project.SaasCRM.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class PipelineStageServiceImpl implements PipelineStageService {

    private final PipelineStageRepository pipelineStageRepository;
    private final PipelineStageMapper pipelineStageMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public PipelineStageDTO savePipelineStage(PipelineStageDTO pipelineStageDTO) {
        PipelineStage pipelineStage = pipelineStageMapper.toEntity(pipelineStageDTO);
        validatePipelineStage(pipelineStage);
        
        // Set default display order if not provided
        if (pipelineStage.getDisplayOrder() == null) {
            Long maxOrder = pipelineStageRepository.findAll().stream()
                .map(PipelineStage::getDisplayOrder)
                .filter(order -> order != null)
                .mapToLong(Integer::longValue)
                .max()
                .orElse(-1L);
            pipelineStage.setDisplayOrder((int) (maxOrder + 1));
        }
        
        // Set default probability if not provided
        if (pipelineStage.getDefaultProbabilityPercentage() == null) {
            pipelineStage.setDefaultProbabilityPercentage(50);
        }
        
        PipelineStage savedStage = pipelineStageRepository.save(pipelineStage);
        auditLogService.logSystemActivity("PIPELINE_STAGE_CREATED", "PIPELINE_STAGE", savedStage.getId());
        return pipelineStageMapper.toDto(savedStage);
    }

    @Override
    @Transactional
    public PipelineStageDTO updatePipelineStage(PipelineStageDTO pipelineStageDTO) {
        if (pipelineStageDTO.getId() == null) {
            throw new IllegalArgumentException("Pipeline stage ID cannot be null for update operation");
        }
        
        PipelineStage existingStage = pipelineStageRepository.findById(pipelineStageDTO.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Pipeline stage not found with id: " + pipelineStageDTO.getId()));
        
        PipelineStage pipelineStage = pipelineStageMapper.toEntity(pipelineStageDTO);
        validatePipelineStage(pipelineStage);
        
        // Preserve existing values if not provided in update
        if (pipelineStage.getDisplayOrder() == null) {
            pipelineStage.setDisplayOrder(existingStage.getDisplayOrder());
        }
        if (pipelineStage.getDefaultProbabilityPercentage() == null) {
            pipelineStage.setDefaultProbabilityPercentage(existingStage.getDefaultProbabilityPercentage());
        }
        
        PipelineStage updatedStage = pipelineStageRepository.save(pipelineStage);
        auditLogService.logSystemActivity("PIPELINE_STAGE_UPDATED", "PIPELINE_STAGE", updatedStage.getId());
        return pipelineStageMapper.toDto(updatedStage);
    }

    @Override
    @Transactional
    public void deletePipelineStage(Long stageId) {
        PipelineStage stage = pipelineStageRepository.findById(stageId)
            .orElseThrow(() -> new ResourceNotFoundException("Pipeline stage not found with id: " + stageId));
            
        // Check if stage has any deals
        if (!stage.getDeals().isEmpty()) {
            throw new IllegalStateException("Cannot delete pipeline stage with existing deals");
        }
        
        pipelineStageRepository.deleteById(stageId);
        auditLogService.logSystemActivity("PIPELINE_STAGE_DELETED", "PIPELINE_STAGE", stageId);
        
        // Reorder remaining stages
        List<PipelineStage> remainingStages = pipelineStageRepository.findAllByOrderByDisplayOrderAsc();
        int order = 0;
        for (PipelineStage remainingStage : remainingStages) {
            remainingStage.setDisplayOrder(order++);
            pipelineStageRepository.save(remainingStage);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PipelineStageDTO> findById(Long stageId) {
        return pipelineStageRepository.findById(stageId)
            .map(pipelineStageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PipelineStageDTO> findByName(String name) {
        return pipelineStageRepository.findByName(name)
            .map(pipelineStageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PipelineStageDTO> findAllPipelineStages() {
        return pipelineStageMapper.toDtoList(pipelineStageRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PipelineStageDTO> findAllPipelineStagesOrdered() {
        return pipelineStageMapper.toDtoList(pipelineStageRepository.findAllByOrderByDisplayOrderAsc());
    }

    @Override
    @Transactional
    public void reorderPipelineStages(List<Long> stageIds) {
        if (stageIds == null || stageIds.isEmpty()) {
            throw new IllegalArgumentException("Stage IDs list cannot be null or empty");
        }
        
        // Verify all stages exist
        List<PipelineStage> existingStages = pipelineStageRepository.findAllById(stageIds);
        if (existingStages.size() != stageIds.size()) {
            throw new ResourceNotFoundException("One or more pipeline stages not found");
        }
        
        // Update display order
        int order = 0;
        for (Long stageId : stageIds) {
            PipelineStage stage = existingStages.stream()
                .filter(s -> s.getId().equals(stageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline stage not found with id: " + stageId));
                
            stage.setDisplayOrder(order++);
            pipelineStageRepository.save(stage);
        }
        
        auditLogService.logSystemActivity("PIPELINE_STAGES_REORDERED", "PIPELINE_STAGE", null);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getDealCountsByStage() {
        List<Object[]> results = pipelineStageRepository.getDealCountsByStage();
        Map<String, Long> countsByStage = new HashMap<>();
        for (Object[] result : results) {
            String stageName = (String) result[0];
            Long count = (Long) result[1];
            countsByStage.put(stageName, count);
        }
        return countsByStage;
    }

    private void validatePipelineStage(PipelineStage pipelineStage) {
        if (pipelineStage == null) {
            throw new IllegalArgumentException("Pipeline stage cannot be null");
        }
        
        if (!StringUtils.hasText(pipelineStage.getName())) {
            throw new IllegalArgumentException("Pipeline stage name is required");
        }
        
        if (pipelineStage.getDefaultProbabilityPercentage() != null) {
            if (pipelineStage.getDefaultProbabilityPercentage() < 0 || 
                pipelineStage.getDefaultProbabilityPercentage() > 100) {
                throw new IllegalArgumentException("Default probability percentage must be between 0 and 100");
            }
        }
        
        // Check for duplicate names
        Optional<PipelineStage> existingStage = pipelineStageRepository.findByName(pipelineStage.getName());
        if (existingStage.isPresent() && 
            (pipelineStage.getId() == null || !pipelineStage.getId().equals(existingStage.get().getId()))) {
            throw new IllegalArgumentException("Pipeline stage with name '" + pipelineStage.getName() + "' already exists");
        }
    }
} 