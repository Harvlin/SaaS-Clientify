package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.dto.PipelineStageDTO;
import com.project.SaasCRM.exception.ResourceNotFoundException;
import com.project.SaasCRM.service.PipelineStageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pipeline-stages")
@RequiredArgsConstructor
@Tag(name = "Pipeline Stage Management", description = "APIs for managing sales pipeline stages")
public class PipelineStageController {
    private final PipelineStageService pipelineStageService;

    @Operation(summary = "Get all pipeline stages", description = "Returns a list of all pipeline stages")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved pipeline stages",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping
    public ResponseEntity<List<PipelineStageDTO>> getAllPipelineStages() {
        return ResponseEntity.ok(pipelineStageService.findAllPipelineStages());
    }

    @Operation(summary = "Get ordered pipeline stages", description = "Returns a list of all pipeline stages ordered by display order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved ordered pipeline stages",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/ordered")
    public ResponseEntity<List<PipelineStageDTO>> getOrderedPipelineStages() {
        return ResponseEntity.ok(pipelineStageService.findAllPipelineStagesOrdered());
    }

    @Operation(summary = "Create a new pipeline stage", description = "Creates a new pipeline stage in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pipeline stage successfully created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PipelineStageDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PipelineStageDTO> createPipelineStage(@Valid @RequestBody PipelineStageDTO pipelineStageDTO) {
        return new ResponseEntity<>(pipelineStageService.savePipelineStage(pipelineStageDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Get pipeline stage by ID", description = "Returns a pipeline stage by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved pipeline stage",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PipelineStageDTO.class))),
        @ApiResponse(responseCode = "404", description = "Pipeline stage not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PipelineStageDTO> getPipelineStageById(@Parameter(description = "ID of the pipeline stage") @PathVariable Long id) {
        return pipelineStageService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get pipeline stage by name", description = "Returns a pipeline stage by its name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved pipeline stage",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PipelineStageDTO.class))),
        @ApiResponse(responseCode = "404", description = "Pipeline stage not found")
    })
    @GetMapping("/by-name/{name}")
    public ResponseEntity<PipelineStageDTO> getPipelineStageByName(
            @Parameter(description = "Name of the pipeline stage") @PathVariable String name) {
        return pipelineStageService.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update a pipeline stage", description = "Updates pipeline stage information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pipeline stage successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PipelineStageDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Pipeline stage not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PipelineStageDTO> updatePipelineStage(
            @Parameter(description = "ID of the pipeline stage") @PathVariable Long id,
            @Valid @RequestBody PipelineStageDTO pipelineStageDTO) {
        // Ensure the ID in the path matches the ID in the body
        if (!id.equals(pipelineStageDTO.getId())) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(pipelineStageService.updatePipelineStage(pipelineStageDTO));
    }

    @Operation(summary = "Delete a pipeline stage", description = "Deletes a pipeline stage from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Pipeline stage successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Pipeline stage not found"),
        @ApiResponse(responseCode = "400", description = "Cannot delete a pipeline stage with existing deals")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePipelineStage(@Parameter(description = "ID of the pipeline stage") @PathVariable Long id) {
        try {
            pipelineStageService.deletePipelineStage(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Reorder pipeline stages", description = "Updates the order of pipeline stages")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pipeline stages successfully reordered"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PutMapping("/reorder")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> reorderPipelineStages(@RequestBody List<Long> stageIds) {
        if (stageIds == null || stageIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        pipelineStageService.reorderPipelineStages(stageIds);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get deal counts by stage", description = "Returns statistics about deals by pipeline stage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved deal counts",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/stats/deal-counts")
    public ResponseEntity<Map<String, Long>> getDealCountsByStage() {
        return ResponseEntity.ok(pipelineStageService.getDealCountsByStage());
    }
} 