package com.project.SaasCRM.repository;

import com.project.SaasCRM.domain.DealStatus;
import com.project.SaasCRM.domain.DealStage;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {
    List<Deal> findByCustomer(Customer customer);

    @Query("SELECT d FROM Deal d WHERE d.customer.id = :customerId")
    List<Deal> findByCustomer_Id(@Param("customerId") Long customerId);

    List<Deal> findByAssignedUser(User user);

    @Query("SELECT d FROM Deal d WHERE d.assignedUser.id = :userId")
    List<Deal> findByAssignedUser_Id(@Param("userId") Long userId);

    @Query("SELECT d FROM Deal d WHERE d.assignedUser.id = :userId")
    Page<Deal> findByAssignedUser_Id(@Param("userId") Long userId, Pageable pageable);

    List<Deal> findByPipelineStage(PipelineStage pipelineStage);

    List<Deal> findByStatus(DealStatus status);

    @Query("SELECT d FROM Deal d WHERE d.expectedCloseDate >= :startDate AND d.expectedCloseDate <= :endDate")
    List<Deal> findByExpectedCloseDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(d.value) FROM Deal d WHERE d.status = :status")
    Double sumValueAmountByStatus(@Param("status") DealStatus status);

    @Query("SELECT d FROM Deal d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(d.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Deal> search(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT COUNT(d) FROM Deal d WHERE d.status = :status")
    Long countByStatus(@Param("status") DealStatus status);

    @Query("SELECT d FROM Deal d WHERE d.createdAt BETWEEN :startDate AND :endDate")
    List<Deal> findByCreatedAtBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT d FROM Deal d WHERE d.stage NOT IN :stages")
    List<Deal> findByStageNotIn(@Param("stages") List<DealStage> stages);

    @Query("SELECT d.assignedUser as user, COUNT(d) as wonDeals, SUM(d.value) as totalValue " +
           "FROM Deal d WHERE d.stage = :stage GROUP BY d.assignedUser ORDER BY totalValue DESC")
    List<Object[]> findTopPerformingUsers(@Param("stage") DealStage stage, Pageable pageable);

    @Query("SELECT d FROM Deal d WHERE d.stage = :stage")
    List<Deal> findByStage(@Param("stage") DealStage stage);

    @Query("SELECT COUNT(d) FROM Deal d WHERE d.stage = :stage")
    Long countByStage(@Param("stage") DealStage stage);

    @Query("SELECT SUM(d.value) FROM Deal d WHERE d.stage = :stage")
    BigDecimal calculateTotalValueByStage(@Param("stage") DealStage stage);

    @Query("SELECT d FROM Deal d WHERE d.expectedCloseDate BETWEEN :startDate AND :endDate")
    List<Deal> findByExpectedCloseDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT d.stage as stage, COUNT(d) as count, SUM(d.value) as totalValue " +
           "FROM Deal d GROUP BY d.stage")
    List<Map<String, Object>> getDealStatsByStage();
    
    @Query("SELECT d FROM Deal d ORDER BY d.createdAt DESC")
    List<Deal> findRecentDeals(Pageable pageable);
    
    @Query("SELECT d FROM Deal d WHERE d.stage IN (com.project.SaasCRM.domain.DealStage.CLOSED_WON, com.project.SaasCRM.domain.DealStage.CLOSED_LOST) " +
           "AND d.actualCloseDate BETWEEN :startDate AND :endDate")
    List<Deal> findClosedDealsByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT SUM(d.value) FROM Deal d WHERE d.stage NOT IN (com.project.SaasCRM.domain.DealStage.CLOSED_WON, com.project.SaasCRM.domain.DealStage.CLOSED_LOST)")
    BigDecimal calculateTotalPipelineValue();
}
