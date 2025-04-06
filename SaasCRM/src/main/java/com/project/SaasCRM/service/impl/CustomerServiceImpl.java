package com.project.SaasCRM.service.impl;

import com.project.SaasCRM.domain.CustomerStatus;
import com.project.SaasCRM.domain.entity.Customer;
import com.project.SaasCRM.domain.entity.User;
import com.project.SaasCRM.domain.dto.CustomerDTO;
import com.project.SaasCRM.domain.dto.UserDTO;
import com.project.SaasCRM.exception.CustomerNotFoundException;
import com.project.SaasCRM.repository.CustomerRepository;
import com.project.SaasCRM.repository.UserRepository;
import com.project.SaasCRM.service.CustomerService;
import com.project.SaasCRM.service.AuditLogService;
import com.project.SaasCRM.mapper.CustomerMapper;
import com.project.SaasCRM.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Map;
import java.util.EnumMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final CustomerMapper customerMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        if (customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        Customer customer = customerMapper.toEntity(customerDTO);
        if (customer.getStatus() == null) {
            customer.setStatus(CustomerStatus.NEW);
        }

        Customer savedCustomer = customerRepository.save(customer);
        auditLogService.logSystemActivity("CUSTOMER_CREATED", "CUSTOMER", savedCustomer.getId());
        return customerMapper.toDto(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        Customer existingCustomer = customerRepository.findById(customerDTO.getId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        if (!existingCustomer.getEmail().equals(customerDTO.getEmail()) &&
                customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        Customer customer = customerMapper.toEntity(customerDTO);
        Customer updatedCustomer = customerRepository.save(customer);
        auditLogService.logSystemActivity("CUSTOMER_UPDATED", "CUSTOMER", updatedCustomer.getId());
        return customerMapper.toDto(updatedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerDTO> findById(Long customerId) {
        return customerRepository.findById(customerId)
                .map(customerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(customerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> searchCustomers(String searchTerm, Pageable pageable) {
        return customerRepository.search(searchTerm, pageable)
                .map(customerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> findCustomersByStatus(CustomerStatus status) {
        return customerMapper.toDtoList(customerRepository.findByStatus(status));
    }

    @Override
    @Transactional
    public CustomerDTO assignUserToCustomer(Long customerId, Long userId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        customer.getAssignedUsers().add(user);
        Customer updatedCustomer = customerRepository.save(customer);
        auditLogService.logUserActivity(userId, "USER_ASSIGNED_TO_CUSTOMER", "CUSTOMER", customerId);
        return customerMapper.toDto(updatedCustomer);
    }

    @Override
    @Transactional
    public CustomerDTO removeUserFromCustomer(Long customerId, Long userId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        customer.getAssignedUsers().remove(user);
        Customer updatedCustomer = customerRepository.save(customer);
        auditLogService.logUserActivity(userId, "USER_REMOVED_FROM_CUSTOMER", "CUSTOMER", customerId);
        return customerMapper.toDto(updatedCustomer);
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomerStatus(Long customerId, CustomerStatus newStatus) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        customer.setStatus(newStatus);
        Customer updatedCustomer = customerRepository.save(customer);
        auditLogService.logSystemActivity("CUSTOMER_STATUS_UPDATED", "CUSTOMER", customerId);
        return customerMapper.toDto(updatedCustomer);
    }

    @Override
    @Transactional
    public void updateLastContact(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        
        customer.setLastContact(LocalDateTime.now());
        customerRepository.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<UserDTO> getAssignedUsers(Long customerId) {
        return customerRepository.findById(customerId)
                .map(customer -> customer.getAssignedUsers().stream()
                        .map(userMapper::toDto)
                        .collect(Collectors.toSet()))
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> findAllCustomers() {
        return customerMapper.toDtoList(customerRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findAllCustomersPaginated(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(customerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findCustomersByStatusPaginated(CustomerStatus status, Pageable pageable) {
        return customerRepository.findByStatus(status, pageable)
                .map(customerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> findCustomersByCreatedDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return customerMapper.toDtoList(customerRepository.findByCreatedDateRange(startDate, endDate));
    }

    @Override
    @Transactional
    public void deleteCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        customerRepository.delete(customer);
        auditLogService.logSystemActivity("CUSTOMER_DELETED", "CUSTOMER", customerId);
    }

    @Override
    @Transactional
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        Customer customer = customerMapper.toEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        auditLogService.logSystemActivity("CUSTOMER_SAVED", "CUSTOMER", savedCustomer.getId());
        return customerMapper.toDto(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerDTO> findByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(customerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> findRecentCustomers(int limit) {
        return customerMapper.toDtoList(customerRepository.findRecentCustomers(PageRequest.of(0, limit)));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<CustomerStatus, Long> getCustomerStatusCounts() {
        Map<CustomerStatus, Long> statusCounts = new EnumMap<>(CustomerStatus.class);
        for (CustomerStatus status : CustomerStatus.values()) {
            statusCounts.put(status, customerRepository.countByStatus(status));
        }
        return statusCounts;
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalCustomersCount() {
        return customerRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> findCustomersByAssignedUser(Long userId) {
        return customerMapper.toDtoList(customerRepository.findByAssignedUsers_Id(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findCustomersByAssignedUserPaginated(Long userId, Pageable pageable) {
        return customerRepository.findByAssignedUsers_Id(userId, pageable)
                .map(customerMapper::toDto);
    }
} 