package com.project.SaasCRM.service.impl;

import com.project.SaasCRM.domain.DealStage;
import com.project.SaasCRM.domain.entity.Deal;
import com.project.SaasCRM.domain.entity.User;
import com.project.SaasCRM.domain.dto.DealDTO;
import com.project.SaasCRM.domain.dto.UserDTO;
import com.project.SaasCRM.exception.DealNotFoundException;
import com.project.SaasCRM.repository.DealRepository;
import com.project.SaasCRM.repository.UserRepository;
import com.project.SaasCRM.service.DealService;
import com.project.SaasCRM.service.AuditLogService;
import com.project.SaasCRM.mapper.DealMapper;
import com.project.SaasCRM.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealServiceImpl implements DealService {

    private final DealRepository dealRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final DealMapper dealMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    @CacheEvict(value = {"deals", "dealCounts", "dealValues"}, allEntries = true)
    public DealDTO createDeal(DealDTO dealDTO) {
        try {
            validateDealDTO(dealDTO);
            Deal deal = dealMapper.toEntity(dealDTO);
            if (deal.getStage() == null) {
                deal.setStage(DealStage.NEW);
            }

            Deal savedDeal = dealRepository.save(deal);
            auditLogService.logSystemActivity("DEAL_CREATED", "DEAL", savedDeal.getId());
            return dealMapper.toDto(savedDeal);
        } catch (Exception e) {
            log.error("Error creating deal", e);
            throw new RuntimeException("Failed to create deal", e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"deals", "dealCounts", "dealValues"}, allEntries = true)
    public DealDTO updateDeal(DealDTO dealDTO) {
        try {
            validateDealDTO(dealDTO);
            Deal existingDeal = dealRepository.findById(dealDTO.getId())
                    .orElseThrow(() -> new DealNotFoundException("Deal not found"));

            Deal deal = dealMapper.toEntity(dealDTO);
            Deal updatedDeal = dealRepository.save(deal);
            auditLogService.logSystemActivity("DEAL_UPDATED", "DEAL", updatedDeal.getId());
            return dealMapper.toDto(updatedDeal);
        } catch (Exception e) {
            log.error("Error updating deal", e);
            throw new RuntimeException("Failed to update deal", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "deals", key = "#dealId", unless = "#result == null")
    public Optional<DealDTO> findById(Long dealId) {
        try {
            return dealRepository.findById(dealId)
                    .map(dealMapper::toDto);
        } catch (Exception e) {
            log.error("Error finding deal by id", e);
            throw new RuntimeException("Failed to find deal", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "deals", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<DealDTO> findAllDeals(Pageable pageable) {
        try {
            return dealRepository.findAll(pageable)
                    .map(dealMapper::toDto);
        } catch (Exception e) {
            log.error("Error finding all deals", e);
            throw new RuntimeException("Failed to find deals", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "deals", key = "#stage")
    public List<DealDTO> findDealsByStage(DealStage stage) {
        try {
            return dealMapper.toDtoList(dealRepository.findByStage(stage));
        } catch (Exception e) {
            log.error("Error finding deals by stage", e);
            throw new RuntimeException("Failed to find deals by stage", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "deals", key = "#customerId")
    public List<DealDTO> findDealsByCustomer(Long customerId) {
        try {
            return dealMapper.toDtoList(dealRepository.findByCustomer_Id(customerId));
        } catch (Exception e) {
            log.error("Error finding deals by customer", e);
            throw new RuntimeException("Failed to find deals by customer", e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"deals", "dealCounts", "dealValues"}, allEntries = true)
    public DealDTO assignUserToDeal(Long dealId, Long userId) {
        try {
            Deal deal = dealRepository.findById(dealId)
                    .orElseThrow(() -> new DealNotFoundException("Deal not found"));
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            deal.getAssignedUsers().add(user);
            Deal updatedDeal = dealRepository.save(deal);
            auditLogService.logUserActivity(userId, "USER_ASSIGNED_TO_DEAL", "DEAL", dealId);
            return dealMapper.toDto(updatedDeal);
        } catch (Exception e) {
            log.error("Error assigning user to deal", e);
            throw new RuntimeException("Failed to assign user to deal", e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"deals", "dealCounts", "dealValues"}, allEntries = true)
    public DealDTO removeUserFromDeal(Long dealId, Long userId) {
        try {
            Deal deal = dealRepository.findById(dealId)
                    .orElseThrow(() -> new DealNotFoundException("Deal not found"));
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            deal.getAssignedUsers().remove(user);
            Deal updatedDeal = dealRepository.save(deal);
            auditLogService.logUserActivity(userId, "USER_REMOVED_FROM_DEAL", "DEAL", dealId);
            return dealMapper.toDto(updatedDeal);
        } catch (Exception e) {
            log.error("Error removing user from deal", e);
            throw new RuntimeException("Failed to remove user from deal", e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"deals", "dealCounts", "dealValues"}, allEntries = true)
    public DealDTO updateDealStage(Long dealId, DealStage newStage) {
        try {
            Deal deal = dealRepository.findById(dealId)
                    .orElseThrow(() -> new DealNotFoundException("Deal not found"));

            deal.setStage(newStage);
            if (newStage == DealStage.CLOSED_WON || newStage == DealStage.CLOSED_LOST) {
                deal.setActualCloseDate(LocalDateTime.now());
            }

            Deal updatedDeal = dealRepository.save(deal);
            auditLogService.logSystemActivity("DEAL_STAGE_UPDATED", "DEAL", dealId);
            return dealMapper.toDto(updatedDeal);
        } catch (Exception e) {
            log.error("Error updating deal stage", e);
            throw new RuntimeException("Failed to update deal stage", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "dealCounts")
    public Map<DealStage, Long> getDealCountsByStage() {
        try {
            Map<DealStage, Long> countsByStage = new EnumMap<>(DealStage.class);
            for (DealStage stage : DealStage.values()) {
                countsByStage.put(stage, dealRepository.countByStage(stage));
            }
            return countsByStage;
        } catch (Exception e) {
            log.error("Error getting deal counts by stage", e);
            throw new RuntimeException("Failed to get deal counts by stage", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "dealValues")
    public Map<DealStage, BigDecimal> getDealValuesByStage() {
        try {
            Map<DealStage, BigDecimal> valuesByStage = new EnumMap<>(DealStage.class);
            for (DealStage stage : DealStage.values()) {
                BigDecimal value = dealRepository.calculateTotalValueByStage(stage);
                valuesByStage.put(stage, value != null ? value : BigDecimal.ZERO);
            }
            return valuesByStage;
        } catch (Exception e) {
            log.error("Error getting deal values by stage", e);
            throw new RuntimeException("Failed to get deal values by stage", e);
        }
    }

    private void validateDealDTO(DealDTO dealDTO) {
        if (dealDTO == null) {
            throw new IllegalArgumentException("DealDTO cannot be null");
        }
        if (dealDTO.getName() == null || dealDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Deal name cannot be empty");
        }
        if (dealDTO.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (dealDTO.getValue() != null && dealDTO.getValue().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Deal value cannot be negative");
        }
        if (dealDTO.getExpectedCloseDate() != null && dealDTO.getExpectedCloseDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Expected close date cannot be in the past");
        }
    }

    @Override
    @Transactional
    public DealDTO closeDealAsWon(Long dealId, LocalDateTime closeDate) {
        Deal deal = dealRepository.findById(dealId)
                .orElseThrow(() -> new DealNotFoundException("Deal not found"));
        deal.setStage(DealStage.CLOSED_WON);
        deal.setActualCloseDate(closeDate);
        Deal updatedDeal = dealRepository.save(deal);
        auditLogService.logSystemActivity("DEAL_CLOSED_WON", "DEAL", dealId);
        return dealMapper.toDto(updatedDeal);
    }

    @Override
    @Transactional
    public DealDTO closeDealAsLost(Long dealId, LocalDateTime closeDate, String reason) {
        Deal deal = dealRepository.findById(dealId)
                .orElseThrow(() -> new DealNotFoundException("Deal not found"));
        deal.setStage(DealStage.CLOSED_LOST);
        deal.setActualCloseDate(closeDate);
        // Assuming there's a reason field in the Deal entity
        // deal.setLostReason(reason);
        Deal updatedDeal = dealRepository.save(deal);
        auditLogService.logSystemActivity("DEAL_CLOSED_LOST", "DEAL", dealId);
        return dealMapper.toDto(updatedDeal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DealDTO> findRecentDeals(int limit) {
        return dealMapper.toDtoList(dealRepository.findRecentDeals(PageRequest.of(0, limit)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DealDTO> searchDeals(String searchTerm, Pageable pageable) {
        return dealRepository.search(searchTerm, pageable)
                .map(dealMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DealDTO> findDealsByExpectedCloseDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return dealMapper.toDtoList(dealRepository.findByExpectedCloseDateRange(startDate, endDate));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getDealValueSumsByStatus() {
        List<Map<String, Object>> stats = dealRepository.getDealStatsByStage();
        Map<String, BigDecimal> valueSums = new HashMap<>();
        for (Map<String, Object> stat : stats) {
            String stage = ((DealStage) stat.get("stage")).toString();
            BigDecimal totalValue = (BigDecimal) stat.get("totalValue");
            valueSums.put(stage, totalValue != null ? totalValue : BigDecimal.ZERO);
        }
        return valueSums;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DealDTO> findAllDeals() {
        return dealMapper.toDtoList(dealRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DealDTO> findAllDealsPaginated(Pageable pageable) {
        return dealRepository.findAll(pageable)
                .map(dealMapper::toDto);
    }

    @Override
    @Transactional
    public DealDTO saveDeal(DealDTO dealDTO) {
        Deal deal = dealMapper.toEntity(dealDTO);
        Deal savedDeal = dealRepository.save(deal);
        auditLogService.logSystemActivity("DEAL_SAVED", "DEAL", savedDeal.getId());
        return dealMapper.toDto(savedDeal);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "deals", key = "#userId + '-assigned'")
    public List<DealDTO> findDealsByAssignedUser(Long userId) {
        try {
            return dealMapper.toDtoList(dealRepository.findByAssignedUser_Id(userId));
        } catch (Exception e) {
            log.error("Error finding deals by assigned user", e);
            throw new RuntimeException("Failed to find deals by assigned user", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "deals", key = "#userId + '-assigned-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<DealDTO> findDealsByAssignedUserPaginated(Long userId, Pageable pageable) {
        try {
            return dealRepository.findByAssignedUser_Id(userId, pageable)
                    .map(dealMapper::toDto);
        } catch (Exception e) {
            log.error("Error finding deals by assigned user paginated", e);
            throw new RuntimeException("Failed to find deals by assigned user paginated", e);
        }
    }

    @Override
    @Transactional
    public void deleteDeal(Long dealId) {
        Deal deal = dealRepository.findById(dealId)
                .orElseThrow(() -> new DealNotFoundException("Deal not found"));
        dealRepository.delete(deal);
        auditLogService.logSystemActivity("DEAL_DELETED", "DEAL", dealId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalDealValue(List<DealDTO> deals) {
        try {
            return deals.stream()
                    .map(DealDTO::getValue)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            log.error("Error calculating total deal value", e);
            throw new RuntimeException("Failed to calculate total deal value", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "deals", key = "#dealId + '-assigned-users'")
    public Set<UserDTO> getAssignedUsers(Long dealId) {
        try {
            return dealRepository.findById(dealId)
                    .map(deal -> deal.getAssignedUsers().stream()
                            .map(userMapper::toDto)
                            .collect(Collectors.toSet()))
                    .orElseThrow(() -> new DealNotFoundException("Deal not found"));
        } catch (Exception e) {
            log.error("Error getting assigned users", e);
            throw new RuntimeException("Failed to get assigned users", e);
        }
    }
} 