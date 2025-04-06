package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.CustomerStatus;
import com.project.SaasCRM.domain.dto.CustomerDTO;
import com.project.SaasCRM.domain.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface CustomerService {
    CustomerDTO createCustomer(CustomerDTO customer);
    
    CustomerDTO updateCustomer(CustomerDTO customer);
    
    Optional<CustomerDTO> findById(Long customerId);
    
    List<CustomerDTO> findAllCustomers();
    
    Page<CustomerDTO> findAllCustomers(Pageable pageable);
    
    Page<CustomerDTO> findAllCustomersPaginated(Pageable pageable);
    
    void deleteCustomer(Long customerId);
    
    CustomerDTO saveCustomer(CustomerDTO customer);
    
    List<CustomerDTO> findCustomersByStatus(CustomerStatus status);
    
    Page<CustomerDTO> findCustomersByStatusPaginated(CustomerStatus status, Pageable pageable);
    
    List<CustomerDTO> findCustomersByCreatedDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    Page<CustomerDTO> searchCustomers(String searchTerm, Pageable pageable);
    
    CustomerDTO assignUserToCustomer(Long customerId, Long userId);
    
    CustomerDTO removeUserFromCustomer(Long customerId, Long userId);
    
    CustomerDTO updateCustomerStatus(Long customerId, CustomerStatus newStatus);
    
    void updateLastContact(Long customerId);
    
    Set<UserDTO> getAssignedUsers(Long customerId);
    
    Optional<CustomerDTO> findByEmail(String email);
    
    List<CustomerDTO> findRecentCustomers(int limit);
    
    Map<CustomerStatus, Long> getCustomerStatusCounts();
    
    long getTotalCustomersCount();
    
    List<CustomerDTO> findCustomersByAssignedUser(Long userId);
    
    Page<CustomerDTO> findCustomersByAssignedUserPaginated(Long userId, Pageable pageable);
}

