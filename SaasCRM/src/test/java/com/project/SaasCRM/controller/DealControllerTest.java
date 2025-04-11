package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.DealStage;
import com.project.SaasCRM.domain.DealStatus;
import com.project.SaasCRM.domain.dto.DealDTO;
import com.project.SaasCRM.domain.dto.UserDTO;
import com.project.SaasCRM.exception.UnauthorizedException;
import com.project.SaasCRM.security.SecurityService;
import com.project.SaasCRM.service.DealService;
import com.project.SaasCRM.service.TaskService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealControllerTest {

    @Mock
    private DealService dealService;
    
    @Mock
    private SecurityService securityService;
    
    @Mock
    private TaskService taskService;
    
    @InjectMocks
    private DealController dealController;
    
    private DealDTO testDeal;
    private List<DealDTO> dealList;
    private Page<DealDTO> dealPage;
    private Set<UserDTO> assignedUsers;
    
    @BeforeEach
    void setUp() {
        testDeal = new DealDTO();
        testDeal.setId(1L);
        testDeal.setName("Test Deal");
        testDeal.setDescription("Test deal description");
        testDeal.setValue(new BigDecimal("10000.00"));
        testDeal.setStage(DealStage.PROPOSAL);
        testDeal.setStatus(DealStatus.OPEN);
        testDeal.setCustomerId(1L);
        testDeal.setExpectedCloseDate(LocalDateTime.now().plusMonths(1));
        testDeal.setCreatedAt(LocalDateTime.now());
        testDeal.setUpdatedAt(LocalDateTime.now());
        
        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setFullName("John Doe");
        
        assignedUsers = new HashSet<>();
        assignedUsers.add(user);
        
        dealList = new ArrayList<>();
        dealList.add(testDeal);
        
        dealPage = new PageImpl<>(dealList);
    }
    
    @Test
    void getAllDeals_ShouldReturnAllDeals() {
        when(dealService.findAllDealsPaginated(any(Pageable.class))).thenReturn(dealPage);
        
        ResponseEntity<Page<DealDTO>> response = dealController.getAllDeals(Pageable.unpaged());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dealPage, response.getBody());
        verify(dealService).findAllDealsPaginated(any(Pageable.class));
    }
    
    @Test
    void createDeal_WithValidData_ShouldCreateDeal() {
        when(dealService.createDeal(any(DealDTO.class))).thenReturn(testDeal);
        
        ResponseEntity<DealDTO> response = dealController.createDeal(testDeal);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testDeal, response.getBody());
        verify(dealService).createDeal(testDeal);
    }
    
    @Test
    void getDealById_WhenAuthorizedAndDealExists_ShouldReturnDeal() {
        when(securityService.isAdmin()).thenReturn(true);
        when(dealService.findById(1L)).thenReturn(Optional.of(testDeal));
        
        ResponseEntity<DealDTO> response = dealController.getDealById(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testDeal, response.getBody());
        verify(securityService).isAdmin();
        verify(dealService).findById(1L);
    }
    
    @Test
    void getDealById_WhenUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.canAccessDeal(1L)).thenReturn(false);
        
        assertThrows(UnauthorizedException.class, () -> dealController.getDealById(1L));
        
        verify(securityService).isAdmin();
        verify(securityService).canAccessDeal(1L);
        verify(dealService, never()).findById(anyLong());
    }
    
    @Test
    void getDealById_WhenDealDoesNotExist_ShouldReturnNotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(dealService.findById(99L)).thenReturn(Optional.empty());
        
        ResponseEntity<DealDTO> response = dealController.getDealById(99L);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(securityService).isAdmin();
        verify(dealService).findById(99L);
    }
    
    @Test
    void updateDeal_WhenAuthorizedAndDealExists_ShouldUpdateDeal() {
        when(securityService.isAdmin()).thenReturn(true);
        when(dealService.updateDeal(any(DealDTO.class))).thenReturn(testDeal);
        
        ResponseEntity<DealDTO> response = dealController.updateDeal(1L, testDeal);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testDeal, response.getBody());
        verify(securityService).isAdmin();
        verify(dealService).updateDeal(testDeal);
    }
    
    @Test
    void updateDeal_WithMismatchedIds_ShouldReturnBadRequest() {
        DealDTO differentDeal = new DealDTO();
        differentDeal.setId(2L);
        
        ResponseEntity<DealDTO> response = dealController.updateDeal(1L, differentDeal);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(securityService, never()).isAdmin();
        verify(dealService, never()).updateDeal(any(DealDTO.class));
    }
    
    @Test
    void updateDeal_WhenUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.canAccessDeal(1L)).thenReturn(false);
        
        assertThrows(UnauthorizedException.class, () -> dealController.updateDeal(1L, testDeal));
        
        verify(securityService).isAdmin();
        verify(securityService).canAccessDeal(1L);
        verify(dealService, never()).updateDeal(any(DealDTO.class));
    }
    
    @Test
    void deleteDeal_WhenAuthorizedAndDealExists_ShouldDeleteDeal() {
        when(securityService.isAdmin()).thenReturn(true);
        doNothing().when(dealService).deleteDeal(1L);
        
        ResponseEntity<Void> response = dealController.deleteDeal(1L);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(securityService).isAdmin();
        verify(dealService).deleteDeal(1L);
    }
    
    @Test
    void deleteDeal_WhenUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.canAccessDeal(1L)).thenReturn(false);
        
        assertThrows(UnauthorizedException.class, () -> dealController.deleteDeal(1L));
        
        verify(securityService).isAdmin();
        verify(securityService).canAccessDeal(1L);
        verify(dealService, never()).deleteDeal(anyLong());
    }
    
    @Test
    void searchDeals_ShouldReturnMatchingDeals() {
        when(dealService.searchDeals(anyString(), any(Pageable.class))).thenReturn(dealPage);
        
        ResponseEntity<Page<DealDTO>> response = dealController.searchDeals("test", Pageable.unpaged());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dealPage, response.getBody());
        verify(dealService).searchDeals("test", Pageable.unpaged());
    }
    
    @Test
    void getDealsByStage_ShouldReturnDealsWithMatchingStage() {
        when(dealService.findDealsByStage(any(DealStage.class))).thenReturn(dealList);
        
        ResponseEntity<List<DealDTO>> response = dealController.getDealsByStage(DealStage.PROPOSAL);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dealList, response.getBody());
        verify(dealService).findDealsByStage(DealStage.PROPOSAL);
    }
    
    @Test
    void getDealsByAssignedUser_WhenAuthorized_ShouldReturnDeals() {
        when(securityService.isAdmin()).thenReturn(true);
        when(dealService.findDealsByAssignedUserPaginated(anyLong(), any(Pageable.class))).thenReturn(dealPage);
        
        ResponseEntity<Page<DealDTO>> response = dealController.getDealsByAssignedUser(1L, Pageable.unpaged());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dealPage, response.getBody());
        verify(securityService).isAdmin();
        verify(dealService).findDealsByAssignedUserPaginated(1L, Pageable.unpaged());
    }
    
    @Test
    void getDealsByAssignedUser_WhenUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isCurrentUser(1L)).thenReturn(false);
        
        assertThrows(UnauthorizedException.class, 
                () -> dealController.getDealsByAssignedUser(1L, Pageable.unpaged()));
        
        verify(securityService).isAdmin();
        verify(securityService).isCurrentUser(1L);
        verify(dealService, never()).findDealsByAssignedUserPaginated(anyLong(), any(Pageable.class));
    }
    
    @Test
    void assignDealToUser_WhenDealExists_ShouldAssignUser() {
        when(dealService.findById(1L)).thenReturn(Optional.of(testDeal));
        when(dealService.assignUserToDeal(1L, 1L)).thenReturn(testDeal);
        
        ResponseEntity<DealDTO> response = dealController.assignDealToUser(1L, 1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testDeal, response.getBody());
        verify(dealService).findById(1L);
        verify(dealService).assignUserToDeal(1L, 1L);
    }
    
    @Test
    void assignDealToUser_WhenDealDoesNotExist_ShouldReturnNotFound() {
        when(dealService.findById(99L)).thenReturn(Optional.empty());
        
        ResponseEntity<DealDTO> response = dealController.assignDealToUser(99L, 1L);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(dealService).findById(99L);
        verify(dealService, never()).assignUserToDeal(anyLong(), anyLong());
    }
    
    @Test
    void updateDealStage_WhenAuthorizedAndDealExists_ShouldUpdateStage() {
        when(securityService.isAdmin()).thenReturn(true);
        when(dealService.findById(1L)).thenReturn(Optional.of(testDeal));
        when(dealService.updateDealStage(1L, DealStage.NEGOTIATION)).thenReturn(testDeal);
        
        ResponseEntity<DealDTO> response = dealController.updateDealStage(1L, DealStage.NEGOTIATION);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testDeal, response.getBody());
        verify(securityService).isAdmin();
        verify(dealService).findById(1L);
        verify(dealService).updateDealStage(1L, DealStage.NEGOTIATION);
    }
    
    @Test
    void updateDealStage_WhenUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.canAccessDeal(1L)).thenReturn(false);
        
        assertThrows(UnauthorizedException.class, 
                () -> dealController.updateDealStage(1L, DealStage.NEGOTIATION));
        
        verify(securityService).isAdmin();
        verify(securityService).canAccessDeal(1L);
        verify(dealService, never()).updateDealStage(anyLong(), any(DealStage.class));
    }
    
    @Test
    void updateDealStage_WhenDealDoesNotExist_ShouldReturnNotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(dealService.findById(99L)).thenReturn(Optional.empty());
        
        ResponseEntity<DealDTO> response = dealController.updateDealStage(99L, DealStage.NEGOTIATION);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(securityService).isAdmin();
        verify(dealService).findById(99L);
        verify(dealService, never()).updateDealStage(anyLong(), any(DealStage.class));
    }
    
    @Test
    void closeDealAsWon_WhenAuthorizedAndDealExists_ShouldCloseDeal() {
        when(securityService.isAdmin()).thenReturn(true);
        when(dealService.findById(1L)).thenReturn(Optional.of(testDeal));
        when(dealService.closeDealAsWon(eq(1L), any(LocalDateTime.class))).thenReturn(testDeal);
        
        ResponseEntity<DealDTO> response = dealController.closeDealAsWon(1L, LocalDateTime.now());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testDeal, response.getBody());
        verify(securityService).isAdmin();
        verify(dealService).findById(1L);
        verify(dealService).closeDealAsWon(eq(1L), any(LocalDateTime.class));
    }
    
    @Test
    void closeDealAsLost_WhenAuthorizedAndDealExists_ShouldCloseDeal() {
        when(securityService.isAdmin()).thenReturn(true);
        when(dealService.findById(1L)).thenReturn(Optional.of(testDeal));
        when(dealService.closeDealAsLost(eq(1L), any(LocalDateTime.class), anyString())).thenReturn(testDeal);
        
        ResponseEntity<DealDTO> response = dealController.closeDealAsLost(1L, "Lost to competitor", LocalDateTime.now());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testDeal, response.getBody());
        verify(securityService).isAdmin();
        verify(dealService).findById(1L);
        verify(dealService).closeDealAsLost(eq(1L), any(LocalDateTime.class), eq("Lost to competitor"));
    }
    
    @Test
    void getDealStatisticsByStage_ShouldReturnStatistics() {
        Map<DealStage, Long> stageStats = new EnumMap<>(DealStage.class);
        stageStats.put(DealStage.NEW, 5L);
        stageStats.put(DealStage.PROPOSAL, 3L);
        
        when(dealService.getDealCountsByStage()).thenReturn(stageStats);
        
        ResponseEntity<Map<DealStage, Long>> response = dealController.getDealStatisticsByStage();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(stageStats, response.getBody());
        verify(dealService).getDealCountsByStage();
    }
    
    @Test
    void getDealValueStatisticsByStage_ShouldReturnValueStatistics() {
        Map<DealStage, BigDecimal> valueStats = new EnumMap<>(DealStage.class);
        valueStats.put(DealStage.NEW, new BigDecimal("5000.00"));
        valueStats.put(DealStage.PROPOSAL, new BigDecimal("15000.00"));
        
        when(dealService.getDealValuesByStage()).thenReturn(valueStats);
        
        ResponseEntity<Map<DealStage, BigDecimal>> response = dealController.getDealValueStatisticsByStage();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(valueStats, response.getBody());
        verify(dealService).getDealValuesByStage();
    }
    
    @Test
    void getAssignedUsers_WhenAuthorizedAndDealExists_ShouldReturnUsers() {
        when(securityService.isAdmin()).thenReturn(true);
        when(dealService.findById(1L)).thenReturn(Optional.of(testDeal));
        when(dealService.getAssignedUsers(1L)).thenReturn(assignedUsers);
        
        ResponseEntity<Set<UserDTO>> response = dealController.getAssignedUsers(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(assignedUsers, response.getBody());
        verify(securityService).isAdmin();
        verify(dealService).findById(1L);
        verify(dealService).getAssignedUsers(1L);
    }
    
    @Test
    void removeUserFromDeal_WhenDealExists_ShouldRemoveUser() {
        when(dealService.findById(1L)).thenReturn(Optional.of(testDeal));
        when(dealService.removeUserFromDeal(1L, 1L)).thenReturn(testDeal);
        
        ResponseEntity<DealDTO> response = dealController.removeUserFromDeal(1L, 1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testDeal, response.getBody());
        verify(dealService).findById(1L);
        verify(dealService).removeUserFromDeal(1L, 1L);
    }
    
    @Test
    void getDealsByDateRange_ShouldReturnDealsInRange() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusMonths(3);
        
        when(dealService.findDealsByExpectedCloseDateRange(startDate, endDate)).thenReturn(dealList);
        
        ResponseEntity<List<DealDTO>> response = dealController.getDealsByDateRange(startDate, endDate);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dealList, response.getBody());
        verify(dealService).findDealsByExpectedCloseDateRange(startDate, endDate);
    }
    
    @Test
    void getRecentDeals_ShouldReturnRecentDeals() {
        when(dealService.findRecentDeals(10)).thenReturn(dealList);
        
        ResponseEntity<List<DealDTO>> response = dealController.getRecentDeals(10);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dealList, response.getBody());
        verify(dealService).findRecentDeals(10);
    }
} 