package com.project.SaasCRM.controller;

import com.project.SaasCRM.domain.CustomerStatus;
import com.project.SaasCRM.domain.dto.CustomerDTO;
import com.project.SaasCRM.domain.dto.DealDTO;
import com.project.SaasCRM.domain.dto.TaskDTO;
import com.project.SaasCRM.domain.dto.UserDTO;
import com.project.SaasCRM.exception.UnauthorizedException;
import com.project.SaasCRM.security.SecurityService;
import com.project.SaasCRM.service.CustomerService;
import com.project.SaasCRM.service.DealService;
import com.project.SaasCRM.service.EmailCommunicationService;
import com.project.SaasCRM.service.InteractionService;
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

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;
    
    @Mock
    private SecurityService securityService;
    
    @Mock
    private InteractionService interactionService;
    
    @Mock
    private DealService dealService;
    
    @Mock
    private TaskService taskService;
    
    @Mock
    private EmailCommunicationService emailService;
    
    @InjectMocks
    private CustomerController customerController;
    
    private CustomerDTO testCustomer;
    private List<CustomerDTO> customerList;
    private Page<CustomerDTO> customerPage;
    private UserDTO testUser;
    
    @BeforeEach
    void setUp() {
        testCustomer = new CustomerDTO();
        testCustomer.setId(1L);
        testCustomer.setName("Test Customer");
        testCustomer.setEmail("customer@example.com");
        testCustomer.setStatus(CustomerStatus.ACTIVE);
        
        customerList = new ArrayList<>();
        customerList.add(testCustomer);
        
        customerPage = new PageImpl<>(customerList);
        
        testUser = new UserDTO();
        testUser.setId(1L);
        testUser.setUsername("testuser");
    }
    
    @Test
    void getAllCustomers_ShouldReturnPageOfCustomers() {
        when(customerService.findAllCustomersPaginated(any(Pageable.class))).thenReturn(customerPage);
        
        ResponseEntity<Page<CustomerDTO>> response = customerController.getAllCustomers(Pageable.unpaged());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customerPage, response.getBody());
        verify(customerService).findAllCustomersPaginated(any(Pageable.class));
    }
    
    @Test
    void createCustomer_ShouldCreateCustomer() {
        when(customerService.createCustomer(any(CustomerDTO.class))).thenReturn(testCustomer);
        
        ResponseEntity<CustomerDTO> response = customerController.createCustomer(testCustomer);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testCustomer, response.getBody());
        verify(customerService).createCustomer(testCustomer);
    }
    
    @Test
    void getCustomerById_WhenAuthorized_ShouldReturnCustomer() {
        when(securityService.isAdmin()).thenReturn(true);
        when(customerService.findById(1L)).thenReturn(Optional.of(testCustomer));
        
        ResponseEntity<CustomerDTO> response = customerController.getCustomerById(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCustomer, response.getBody());
        verify(securityService).isAdmin();
        verify(customerService).findById(1L);
    }
    
    @Test
    void getCustomerById_WhenUnauthorized_ShouldThrowException() {
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.canAccessCustomer(anyLong())).thenReturn(false);
        
        assertThrows(UnauthorizedException.class, () -> customerController.getCustomerById(1L));
        
        verify(securityService).isAdmin();
        verify(securityService).canAccessCustomer(1L);
        verify(customerService, never()).findById(anyLong());
    }
    
    @Test
    void updateCustomer_WhenAuthorized_ShouldUpdateCustomer() {
        when(securityService.isAdmin()).thenReturn(true);
        when(customerService.updateCustomer(any(CustomerDTO.class))).thenReturn(testCustomer);
        
        ResponseEntity<CustomerDTO> response = customerController.updateCustomer(1L, testCustomer);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCustomer, response.getBody());
        verify(securityService).isAdmin();
        verify(customerService).updateCustomer(testCustomer);
    }
    
    @Test
    void updateCustomer_WithMismatchedIds_ShouldReturnBadRequest() {
        testCustomer.setId(2L);
        
        ResponseEntity<CustomerDTO> response = customerController.updateCustomer(1L, testCustomer);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(customerService, never()).updateCustomer(any(CustomerDTO.class));
    }
    
    @Test
    void deleteCustomer_WhenAuthorized_ShouldDeleteCustomer() {
        when(securityService.isAdmin()).thenReturn(true);
        doNothing().when(customerService).deleteCustomer(1L);
        
        ResponseEntity<Void> response = customerController.deleteCustomer(1L);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(securityService).isAdmin();
        verify(customerService).deleteCustomer(1L);
    }
    
    @Test
    void searchCustomers_ShouldReturnSearchResults() {
        when(customerService.searchCustomers(anyString(), any(Pageable.class))).thenReturn(customerPage);
        
        ResponseEntity<Page<CustomerDTO>> response = customerController.searchCustomers("test", Pageable.unpaged());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customerPage, response.getBody());
        verify(customerService).searchCustomers("test", Pageable.unpaged());
    }
    
    @Test
    void getCustomersByStatus_ShouldReturnCustomersWithStatus() {
        when(customerService.findCustomersByStatusPaginated(any(CustomerStatus.class), any(Pageable.class)))
            .thenReturn(customerPage);
        
        ResponseEntity<Page<CustomerDTO>> response = customerController.getCustomersByStatus(
                CustomerStatus.ACTIVE, Pageable.unpaged());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customerPage, response.getBody());
        verify(customerService).findCustomersByStatusPaginated(CustomerStatus.ACTIVE, Pageable.unpaged());
    }
    
    @Test
    void updateCustomerStatus_WhenAuthorized_ShouldUpdateStatus() {
        when(securityService.isAdmin()).thenReturn(true);
        when(customerService.updateCustomerStatus(anyLong(), any(CustomerStatus.class))).thenReturn(testCustomer);
        
        ResponseEntity<CustomerDTO> response = customerController.updateCustomerStatus(1L, CustomerStatus.INACTIVE);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCustomer, response.getBody());
        verify(securityService).isAdmin();
        verify(customerService).updateCustomerStatus(1L, CustomerStatus.INACTIVE);
    }
    
    @Test
    void getRecentCustomers_ShouldReturnRecentCustomers() {
        when(customerService.findRecentCustomers(10)).thenReturn(customerList);
        
        ResponseEntity<List<CustomerDTO>> response = customerController.getRecentCustomers(10);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customerList, response.getBody());
        verify(customerService).findRecentCustomers(10);
    }
    
    @Test
    void getCustomerStatistics_ShouldReturnStatistics() {
        Map<CustomerStatus, Long> stats = new HashMap<>();
        stats.put(CustomerStatus.ACTIVE, 10L);
        stats.put(CustomerStatus.INACTIVE, 5L);
        
        when(customerService.getCustomerStatusCounts()).thenReturn(stats);
        
        ResponseEntity<Map<CustomerStatus, Long>> response = customerController.getCustomerStatistics();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(stats, response.getBody());
        verify(customerService).getCustomerStatusCounts();
    }
} 