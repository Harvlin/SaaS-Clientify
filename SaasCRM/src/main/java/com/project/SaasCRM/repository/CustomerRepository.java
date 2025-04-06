package com.project.SaasCRM.repository;

import com.project.SaasCRM.domain.CustomerStatus;
import com.project.SaasCRM.domain.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmail(String email);
    
    Optional<Customer> findByEmail(String email);
    
    List<Customer> findByStatus(CustomerStatus status);
    
    Page<Customer> findByStatus(CustomerStatus status, Pageable pageable);
    
    @Query("SELECT c FROM Customer c WHERE c.createdAt BETWEEN :startDate AND :endDate")
    List<Customer> findByCreatedDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT c FROM Customer c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Customer> search(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.status = :status")
    long countByStatus(@Param("status") CustomerStatus status);
    
    List<Customer> findByAssignedUsers_Id(Long userId);
    
    Page<Customer> findByAssignedUsers_Id(Long userId, Pageable pageable);
    
    @Query("SELECT c FROM Customer c ORDER BY c.createdAt DESC")
    List<Customer> findRecentCustomers(Pageable pageable);
    
    @Query("SELECT c.status as status, COUNT(c) as count FROM Customer c GROUP BY c.status")
    List<Object[]> getCustomerStatusCounts();
    
    @Query("SELECT COUNT(c) FROM Customer c")
    long getTotalCustomersCount();

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.createdAt BETWEEN :startDate AND :endDate")
    long countByCreatedAtBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.status = :status AND c.updatedAt BETWEEN :startDate AND :endDate")
    long countByStatusAndUpdatedAtBetween(
        @Param("status") CustomerStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
