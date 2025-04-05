package com.project.SaasCRM.repository;

import com.project.SaasCRM.domain.DealStatus;
import com.project.SaasCRM.domain.entity.Customer;
import com.project.SaasCRM.domain.entity.Deal;
import com.project.SaasCRM.domain.entity.PipelineStage;
import com.project.SaasCRM.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {
    List<Deal> findByCustomer(Customer customer);

    List<Deal> findByAssignedUser(User user);

    List<Deal> findByPipelineStage(PipelineStage pipelineStage);

    List<Deal> findByStatus(DealStatus status);

    @Query("SELECT d FROM Deal d WHERE " +
            "d.expectedCloseDate >= :startDate AND d.expectedCloseDate <= :endDate")
    List<Deal> findByExpectedCloseDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(d.valueAmount) FROM Deal d WHERE d.status = :status")
    Double sumValueAmountByStatus(@Param("status") DealStatus status);

    @Query("SELECT d FROM Deal d WHERE " +
            "LOWER(d.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(d.customer.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(d.customer.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Deal> search(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT d FROM Deal d WHERE d.assignedUser.id = :userId")
    Page<Deal> findByAssignedUserIdPaginated(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(d) FROM Deal d WHERE d.status = :status")
    Long countByStatus(@Param("status") DealStatus status);
}
