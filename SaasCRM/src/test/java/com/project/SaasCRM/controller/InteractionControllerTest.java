package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.InteractionType;
import com.project.SaasCRM.domain.dto.InteractionDTO;
import com.project.SaasCRM.exception.UnauthorizedException;
import com.project.SaasCRM.security.SecurityService;
import com.project.SaasCRM.service.InteractionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InteractionControllerTest {

    @Mock
    private InteractionService interactionService;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private InteractionController interactionController;

    private InteractionDTO testInteraction;
    private List<InteractionDTO> interactionList;
    private Page<InteractionDTO> interactionPage;

    @BeforeEach
    void setUp() {
        testInteraction = new InteractionDTO();
        testInteraction.setId(1L);
        testInteraction.setTitle("Test Interaction");
        testInteraction.setDescription("Test interaction description");
        testInteraction.setType(InteractionType.MEETING);
        testInteraction.setCustomerId(1L);
        testInteraction.setUserId(1L);
        testInteraction.setCreatedAt(LocalDateTime.now());
        testInteraction.setUpdatedAt(LocalDateTime.now());

        interactionList = new ArrayList<>();
        interactionList.add(testInteraction);

        interactionPage = new PageImpl<>(interactionList);
    }

    @Test
    void getAllInteractions_ShouldReturnAllInteractions() {
        when(interactionService.findAllInteractionsPaginated(any(Pageable.class))).thenReturn(interactionPage);

        ResponseEntity<Page<InteractionDTO>> response = interactionController.getAllInteractions(Pageable.unpaged());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(interactionPage, response.getBody());
        verify(interactionService).findAllInteractionsPaginated(any(Pageable.class));
    }

    @Test
    void createInteraction_WithValidData_ShouldCreateInteraction() {
        when(interactionService.saveInteraction(any(InteractionDTO.class))).thenReturn(testInteraction);

        ResponseEntity<InteractionDTO> response = interactionController.createInteraction(testInteraction);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testInteraction, response.getBody());
        verify(interactionService).saveInteraction(testInteraction);
    }

    @Test
    void getInteractionById_WhenInteractionExists_ShouldReturnInteraction() {
        when(interactionService.findById(1L)).thenReturn(Optional.of(testInteraction));

        ResponseEntity<InteractionDTO> response = interactionController.getInteractionById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testInteraction, response.getBody());
        verify(interactionService).findById(1L);
    }

    @Test
    void getInteractionById_WhenInteractionDoesNotExist_ShouldReturnNotFound() {
        when(interactionService.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<InteractionDTO> response = interactionController.getInteractionById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(interactionService).findById(99L);
    }

    @Test
    void updateInteraction_WithValidData_ShouldUpdateInteraction() {
        when(interactionService.updateInteraction(any(InteractionDTO.class))).thenReturn(testInteraction);

        ResponseEntity<InteractionDTO> response = interactionController.updateInteraction(1L, testInteraction);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testInteraction, response.getBody());
        verify(interactionService).updateInteraction(testInteraction);
    }

    @Test
    void updateInteraction_WithMismatchedIds_ShouldReturnBadRequest() {
        InteractionDTO differentInteraction = new InteractionDTO();
        differentInteraction.setId(2L);

        ResponseEntity<InteractionDTO> response = interactionController.updateInteraction(1L, differentInteraction);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(interactionService, never()).updateInteraction(any(InteractionDTO.class));
    }

    @Test
    void deleteInteraction_ShouldDeleteInteraction() {
        doNothing().when(interactionService).deleteInteraction(1L);

        ResponseEntity<Void> response = interactionController.deleteInteraction(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(interactionService).deleteInteraction(1L);
    }

    @Test
    void getInteractionsByCustomer_WhenAuthorized_ShouldReturnInteractions() {
        when(securityService.isAdmin()).thenReturn(true);
        when(interactionService.findInteractionsByCustomerPaginated(anyLong(), any(Pageable.class))).thenReturn(interactionPage);

        ResponseEntity<Page<InteractionDTO>> response = interactionController.getInteractionsByCustomer(1L, Pageable.unpaged());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(interactionPage, response.getBody());
        verify(securityService).isAdmin();
        verify(interactionService).findInteractionsByCustomerPaginated(eq(1L), any(Pageable.class));
    }

    @Test
    void getInteractionsByCustomer_WhenUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.canAccessCustomer(1L)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> interactionController.getInteractionsByCustomer(1L, Pageable.unpaged()));

        verify(securityService).isAdmin();
        verify(securityService).canAccessCustomer(1L);
        verify(interactionService, never()).findInteractionsByCustomerPaginated(anyLong(), any(Pageable.class));
    }

    @Test
    void getInteractionsByUser_WhenAuthorized_ShouldReturnInteractions() {
        when(securityService.isAdmin()).thenReturn(true);
        when(interactionService.findInteractionsByUser(1L)).thenReturn(interactionList);

        ResponseEntity<List<InteractionDTO>> response = interactionController.getInteractionsByUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(interactionList, response.getBody());
        verify(securityService).isAdmin();
        verify(interactionService).findInteractionsByUser(1L);
    }

    @Test
    void getInteractionsByUser_WhenUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isCurrentUser(1L)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> interactionController.getInteractionsByUser(1L));

        verify(securityService).isAdmin();
        verify(securityService).isCurrentUser(1L);
        verify(interactionService, never()).findInteractionsByUser(anyLong());
    }

    @Test
    void getInteractionsByType_ShouldReturnInteractions() {
        when(interactionService.findInteractionsByType(InteractionType.MEETING)).thenReturn(interactionList);

        ResponseEntity<List<InteractionDTO>> response = interactionController.getInteractionsByType(InteractionType.MEETING);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(interactionList, response.getBody());
        verify(interactionService).findInteractionsByType(InteractionType.MEETING);
    }

    @Test
    void getRecentInteractions_ShouldReturnRecentInteractions() {
        when(interactionService.findRecentInteractions(10)).thenReturn(interactionList);

        ResponseEntity<List<InteractionDTO>> response = interactionController.getRecentInteractions(10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(interactionList, response.getBody());
        verify(interactionService).findRecentInteractions(10);
    }

    @Test
    void getInteractionStatsByType_WhenAuthorized_ShouldReturnStats() {
        Map<InteractionType, Long> typeStats = new EnumMap<>(InteractionType.class);
        typeStats.put(InteractionType.MEETING, 5L);
        typeStats.put(InteractionType.CALL, 3L);
        
        when(securityService.isAdmin()).thenReturn(true);
        when(interactionService.getInteractionTypeCounts(1L)).thenReturn(typeStats);

        ResponseEntity<Map<InteractionType, Long>> response = interactionController.getInteractionStatsByType(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(typeStats, response.getBody());
        verify(securityService).isAdmin();
        verify(interactionService).getInteractionTypeCounts(1L);
    }

    @Test
    void getInteractionStatsByType_WhenUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.canAccessCustomer(1L)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> interactionController.getInteractionStatsByType(1L));

        verify(securityService).isAdmin();
        verify(securityService).canAccessCustomer(1L);
        verify(interactionService, never()).getInteractionTypeCounts(anyLong());
    }

    @Test
    void getInteractionStatsByDate_ShouldReturnStats() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        Map<String, Long> dateStats = new HashMap<>();
        dateStats.put("2023-07-01", 3L);
        dateStats.put("2023-07-02", 5L);
        
        when(interactionService.getInteractionCountsByDate(startDate, endDate)).thenReturn(dateStats);

        ResponseEntity<Map<String, Long>> response = interactionController.getInteractionStatsByDate(startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dateStats, response.getBody());
        verify(interactionService).getInteractionCountsByDate(startDate, endDate);
    }

    @Test
    void getInteractionsByDateRange_ShouldReturnInteractions() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        
        when(interactionService.findInteractionsByDateRange(startDate, endDate)).thenReturn(interactionList);

        ResponseEntity<List<InteractionDTO>> response = interactionController.getInteractionsByDateRange(startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(interactionList, response.getBody());
        verify(interactionService).findInteractionsByDateRange(startDate, endDate);
    }
} 