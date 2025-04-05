package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.CustomerStatus;
import com.project.SaasCRM.domain.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CustomerService {
    Customer saveCustomer(Customer customer);

    Customer updateCustomer(Customer customer);

    void deleteCustomer(Long customerId);

    Optional<Customer> findById(Long customerId);

    Optional<Customer> findByEmail(String email);

    List<Customer> findAllCustomers();

    Page<Customer> findAllCustomersPaginated(Pageable pageable);

    Page<Customer> searchCustomers(String searchTerm, Pageable pageable);

    List<Customer> findCustomersByStatus(CustomerStatus status);

    Page<Customer> findCustomersByStatusPaginated(CustomerStatus status, Pageable pageable);

    List<Customer> findCustomersByAssignedUser(Long userId);

    Page<Customer> findCustomersByAssignedUserPaginated(Long userId, Pageable pageable);

    Customer assignUserToCustomer(Long customerId, Long userId);

    Customer updateCustomerStatus(Long customerId, CustomerStatus status);

    List<Customer> findRecentCustomers(int limit);

    Map<CustomerStatus, Long> getCustomerStatusCounts();

    List<Customer> findCustomersByCreatedDateRange(LocalDateTime startDate, LocalDateTime endDate);

    long getTotalCustomersCount();
}

