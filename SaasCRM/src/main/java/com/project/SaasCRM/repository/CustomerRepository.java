package com.project.SaasCRM.repository;

import com.project.SaasCRM.domain.CustomerStatus;
import com.project.SaasCRM.domain.entity.Customer;
import com.project.SaasCRM.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByStatus(CustomerStatus status);

    List<Customer> findByAssignedUser(User user);

    Optional<Customer> findByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.company) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.phoneNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Customer> search(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.status = :status")
    Page<Customer> findByStatusPaginated(@Param("status") CustomerStatus status, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.status = :status")
    Long countByStatus(@Param("status") CustomerStatus status);

    @Query("SELECT c FROM Customer c WHERE c.assignedUser.id = :userId")
    Page<Customer> findByAssignedUserIdPaginated(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE " +
            "c.createdAt >= :startDate AND c.createdAt <= :endDate")
    List<Customer> findByCreatedDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
