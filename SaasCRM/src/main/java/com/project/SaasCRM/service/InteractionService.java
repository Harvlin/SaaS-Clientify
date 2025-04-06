package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.InteractionType;
import com.project.SaasCRM.domain.dto.InteractionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface InteractionService {
    InteractionDTO saveInteraction(InteractionDTO interaction);

    InteractionDTO updateInteraction(InteractionDTO interaction);

    void deleteInteraction(Long interactionId);

    Optional<InteractionDTO> findById(Long interactionId);

    List<InteractionDTO> findAllInteractions();

    Page<InteractionDTO> findAllInteractionsPaginated(Pageable pageable);

    List<InteractionDTO> findInteractionsByCustomer(Long customerId);

    Page<InteractionDTO> findInteractionsByCustomerPaginated(Long customerId, Pageable pageable);

    List<InteractionDTO> findInteractionsByUser(Long userId);

    List<InteractionDTO> findInteractionsByType(InteractionType type);

    List<InteractionDTO> findRecentInteractions(int limit);

    List<InteractionDTO> findInteractionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Map<InteractionType, Long> getInteractionTypeCounts(Long customerId);

    Map<String, Long> getInteractionCountsByDate(LocalDateTime startDate, LocalDateTime endDate);
}