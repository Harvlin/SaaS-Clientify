package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.dto.PipelineStageDTO;
import com.project.SaasCRM.exception.ResourceNotFoundException;
import com.project.SaasCRM.service.PipelineStageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PipelineStageControllerTest {

    @Mock
    private PipelineStageService pipelineStageService;

    @InjectMocks
    private PipelineStageController pipelineStageController;

    private PipelineStageDTO testStage;
    private List<PipelineStageDTO> stageList;

    @BeforeEach
    void setUp() {
        testStage = new PipelineStageDTO();
        testStage.setId(1L);
        testStage.setName("Test Stage");
        testStage.setDescription("Test stage description");
        testStage.setDisplayOrder(1);
        testStage.setDefaultProbabilityPercentage(50);
        
        stageList = new ArrayList<>();
        stageList.add(testStage);
    }

    @Test
    void getAllPipelineStages_ShouldReturnAllStages() {
        when(pipelineStageService.findAllPipelineStages()).thenReturn(stageList);

        ResponseEntity<List<PipelineStageDTO>> response = pipelineStageController.getAllPipelineStages();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(stageList, response.getBody());
        verify(pipelineStageService).findAllPipelineStages();
    }

    @Test
    void getOrderedPipelineStages_ShouldReturnOrderedStages() {
        when(pipelineStageService.findAllPipelineStagesOrdered()).thenReturn(stageList);

        ResponseEntity<List<PipelineStageDTO>> response = pipelineStageController.getOrderedPipelineStages();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(stageList, response.getBody());
        verify(pipelineStageService).findAllPipelineStagesOrdered();
    }

    @Test
    void createPipelineStage_WithValidData_ShouldCreateStage() {
        when(pipelineStageService.savePipelineStage(any(PipelineStageDTO.class))).thenReturn(testStage);

        ResponseEntity<PipelineStageDTO> response = pipelineStageController.createPipelineStage(testStage);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testStage, response.getBody());
        verify(pipelineStageService).savePipelineStage(testStage);
    }

    @Test
    void getPipelineStageById_WhenStageExists_ShouldReturnStage() {
        when(pipelineStageService.findById(1L)).thenReturn(Optional.of(testStage));

        ResponseEntity<PipelineStageDTO> response = pipelineStageController.getPipelineStageById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testStage, response.getBody());
        verify(pipelineStageService).findById(1L);
    }

    @Test
    void getPipelineStageById_WhenStageDoesNotExist_ShouldReturnNotFound() {
        when(pipelineStageService.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<PipelineStageDTO> response = pipelineStageController.getPipelineStageById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(pipelineStageService).findById(99L);
    }

    @Test
    void getPipelineStageByName_WhenStageExists_ShouldReturnStage() {
        when(pipelineStageService.findByName("Test Stage")).thenReturn(Optional.of(testStage));

        ResponseEntity<PipelineStageDTO> response = pipelineStageController.getPipelineStageByName("Test Stage");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testStage, response.getBody());
        verify(pipelineStageService).findByName("Test Stage");
    }

    @Test
    void getPipelineStageByName_WhenStageDoesNotExist_ShouldReturnNotFound() {
        when(pipelineStageService.findByName("Non Existent")).thenReturn(Optional.empty());

        ResponseEntity<PipelineStageDTO> response = pipelineStageController.getPipelineStageByName("Non Existent");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(pipelineStageService).findByName("Non Existent");
    }

    @Test
    void updatePipelineStage_WithValidData_ShouldUpdateStage() {
        when(pipelineStageService.updatePipelineStage(any(PipelineStageDTO.class))).thenReturn(testStage);

        ResponseEntity<PipelineStageDTO> response = pipelineStageController.updatePipelineStage(1L, testStage);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testStage, response.getBody());
        verify(pipelineStageService).updatePipelineStage(testStage);
    }

    @Test
    void updatePipelineStage_WithMismatchedIds_ShouldReturnBadRequest() {
        PipelineStageDTO differentStage = new PipelineStageDTO();
        differentStage.setId(2L);

        ResponseEntity<PipelineStageDTO> response = pipelineStageController.updatePipelineStage(1L, differentStage);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(pipelineStageService, never()).updatePipelineStage(any(PipelineStageDTO.class));
    }

    @Test
    void deletePipelineStage_WhenStageExists_ShouldDeleteStage() {
        doNothing().when(pipelineStageService).deletePipelineStage(1L);

        ResponseEntity<Void> response = pipelineStageController.deletePipelineStage(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(pipelineStageService).deletePipelineStage(1L);
    }

    @Test
    void deletePipelineStage_WhenStageNotFound_ShouldReturnNotFound() {
        doThrow(ResourceNotFoundException.class).when(pipelineStageService).deletePipelineStage(99L);

        ResponseEntity<Void> response = pipelineStageController.deletePipelineStage(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(pipelineStageService).deletePipelineStage(99L);
    }

    @Test
    void deletePipelineStage_WhenStageHasDeals_ShouldReturnBadRequest() {
        doThrow(IllegalStateException.class).when(pipelineStageService).deletePipelineStage(1L);

        ResponseEntity<Void> response = pipelineStageController.deletePipelineStage(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(pipelineStageService).deletePipelineStage(1L);
    }

    @Test
    void reorderPipelineStages_WithValidData_ShouldReorderStages() {
        List<Long> stageIds = Arrays.asList(1L, 2L, 3L);
        doNothing().when(pipelineStageService).reorderPipelineStages(stageIds);

        ResponseEntity<Void> response = pipelineStageController.reorderPipelineStages(stageIds);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(pipelineStageService).reorderPipelineStages(stageIds);
    }

    @Test
    void reorderPipelineStages_WithEmptyList_ShouldReturnBadRequest() {
        ResponseEntity<Void> response = pipelineStageController.reorderPipelineStages(Collections.emptyList());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(pipelineStageService, never()).reorderPipelineStages(anyList());
    }

    @Test
    void getDealCountsByStage_ShouldReturnCounts() {
        Map<String, Long> dealCounts = new HashMap<>();
        dealCounts.put("Lead", 10L);
        dealCounts.put("Negotiation", 5L);
        
        when(pipelineStageService.getDealCountsByStage()).thenReturn(dealCounts);

        ResponseEntity<Map<String, Long>> response = pipelineStageController.getDealCountsByStage();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dealCounts, response.getBody());
        verify(pipelineStageService).getDealCountsByStage();
    }
} 