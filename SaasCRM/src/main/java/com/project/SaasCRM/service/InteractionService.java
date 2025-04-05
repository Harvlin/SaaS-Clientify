package com.project.SaasCRM.service;

import com.project.SaasCRM.domain.InteractionType;
import com.project.SaasCRM.domain.entity.Interaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface InteractionService {
    Interaction saveInteraction(Interaction interaction);

    Interaction updateInteraction(Interaction interaction);

    void deleteInteraction(Long interactionId);

    Optional<Interaction> findById(Long interactionId);

    List<Interaction> findAllInteractions();

    Page<Interaction> findAllInteractionsPaginated(Pageable pageable);

    List<Interaction> findInteractionsByCustomer(Long customerId);

    Page<Interaction> findInteractionsByCustomerPaginated(Long customerId, Pageable pageable);

    List<Interaction> findInteractionsByUser(Long userId);

    List<Interaction> findInteractionsByType(InteractionType type);

    List<Interaction> findRecentInteractions(int limit);

    List<Interaction> findInteractionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Map<InteractionType, Long> getInteractionTypeCounts(Long customerId);

    Map<String, Long> getInteractionCountsByDate(LocalDateTime startDate, LocalDateTime endDate);
}