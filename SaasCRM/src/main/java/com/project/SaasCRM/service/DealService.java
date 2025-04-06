package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.DealStage;
import com.project.SaasCRM.domain.dto.DealDTO;
import com.project.SaasCRM.domain.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface DealService {
    DealDTO createDeal(DealDTO deal);
    
    DealDTO updateDeal(DealDTO deal);
    
    DealDTO saveDeal(DealDTO deal);
    
    void deleteDeal(Long dealId);
    
    Optional<DealDTO> findById(Long dealId);
    
    List<DealDTO> findAllDeals();
    
    Page<DealDTO> findAllDeals(Pageable pageable);
    
    Page<DealDTO> findAllDealsPaginated(Pageable pageable);
    
    List<DealDTO> findDealsByStage(DealStage stage);
    
    List<DealDTO> findDealsByCustomer(Long customerId);
    
    List<DealDTO> findDealsByAssignedUser(Long userId);
    
    Page<DealDTO> findDealsByAssignedUserPaginated(Long userId, Pageable pageable);
    
    DealDTO assignUserToDeal(Long dealId, Long userId);
    
    DealDTO removeUserFromDeal(Long dealId, Long userId);
    
    DealDTO updateDealStage(Long dealId, DealStage newStage);
    
    DealDTO closeDealAsWon(Long dealId, LocalDateTime closeDate);
    
    DealDTO closeDealAsLost(Long dealId, LocalDateTime closeDate, String reason);
    
    BigDecimal calculateTotalDealValue(List<DealDTO> deals);
    
    Map<DealStage, Long> getDealCountsByStage();
    
    Set<UserDTO> getAssignedUsers(Long dealId);
    
    List<DealDTO> findRecentDeals(int limit);
    
    Page<DealDTO> searchDeals(String searchTerm, Pageable pageable);
    
    List<DealDTO> findDealsByExpectedCloseDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    Map<DealStage, BigDecimal> getDealValuesByStage();
    
    Map<String, BigDecimal> getDealValueSumsByStatus();
}