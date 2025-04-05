package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.DealStatus;
import com.project.SaasCRM.domain.entity.Deal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DealService {
    Deal saveDeal(Deal deal);

    Deal updateDeal(Deal deal);

    void deleteDeal(Long dealId);

    Optional<Deal> findById(Long dealId);

    List<Deal> findAllDeals();

    Page<Deal> findAllDealsPaginated(Pageable pageable);

    Page<Deal> searchDeals(String searchTerm, Pageable pageable);

    List<Deal> findDealsByCustomer(Long customerId);

    List<Deal> findDealsByAssignedUser(Long userId);

    Page<Deal> findDealsByAssignedUserPaginated(Long userId, Pageable pageable);

    List<Deal> findDealsByPipelineStage(Long stageId);

    List<Deal> findDealsByStatus(DealStatus status);

    Deal assignUserToDeal(Long dealId, Long userId);

    Deal updateDealStage(Long dealId, Long stageId);

    Deal updateDealStatus(Long dealId, DealStatus status);

    List<Deal> findDealsByExpectedCloseDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Double calculateTotalValueByStatus(DealStatus status);

    Map<DealStatus, Long> getDealStatusCounts();

    Map<DealStatus, Double> getDealValueSumsByStatus();

    Map<String, Double> getDealValuesByStage();

    List<Deal> findRecentDeals(int limit);

    Deal closeDealAsWon(Long dealId, LocalDateTime closeDate);

    Deal closeDealAsLost(Long dealId, LocalDateTime closeDate, String reason);
}