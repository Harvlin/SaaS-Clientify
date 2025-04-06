package com.project.SaasCRM.service.impl;

import com.project.SaasCRM.domain.InteractionType;
import com.project.SaasCRM.domain.entity.Interaction;
import com.project.SaasCRM.domain.entity.User;
import com.project.SaasCRM.domain.entity.Customer;
import com.project.SaasCRM.domain.dto.InteractionDTO;
import com.project.SaasCRM.exception.ResourceNotFoundException;
import com.project.SaasCRM.repository.InteractionRepository;
import com.project.SaasCRM.repository.UserRepository;
import com.project.SaasCRM.repository.CustomerRepository;
import com.project.SaasCRM.service.InteractionService;
import com.project.SaasCRM.service.AuditLogService;
import com.project.SaasCRM.service.NotificationService;
import com.project.SaasCRM.mapper.InteractionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InteractionServiceImpl implements InteractionService {

    private final InteractionRepository interactionRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final InteractionMapper interactionMapper;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public InteractionDTO saveInteraction(InteractionDTO interactionDTO) {
        validateInteractionDTO(interactionDTO);

        User user = userRepository.findById(interactionDTO.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + interactionDTO.getUserId()));

        Customer customer = customerRepository.findById(interactionDTO.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + interactionDTO.getCustomerId()));

        Interaction interaction = interactionMapper.toEntity(interactionDTO);
        interaction.setUser(user);
        interaction.setCustomer(customer);

        Interaction savedInteraction = interactionRepository.save(interaction);
        auditLogService.logSystemActivity("INTERACTION_CREATED", "INTERACTION", savedInteraction.getId());

        // Send notification to assigned users
        notificationService.sendInteractionNotification(
            user.getId(),
            savedInteraction.getId(),
            "New interaction created: " + savedInteraction.getTitle()
        );

        return interactionMapper.toDto(savedInteraction);
    }

    @Override
    @Transactional
    public InteractionDTO updateInteraction(InteractionDTO interactionDTO) {
        if (interactionDTO.getId() == null) {
            throw new IllegalArgumentException("Interaction ID cannot be null for update operation");
        }

        validateInteractionDTO(interactionDTO);

        Interaction existingInteraction = interactionRepository.findById(interactionDTO.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Interaction not found with id: " + interactionDTO.getId()));

        User user = userRepository.findById(interactionDTO.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + interactionDTO.getUserId()));

        Customer customer = customerRepository.findById(interactionDTO.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + interactionDTO.getCustomerId()));

        Interaction interaction = interactionMapper.toEntity(interactionDTO);
        interaction.setUser(user);
        interaction.setCustomer(customer);
        interaction.setCreatedAt(existingInteraction.getCreatedAt());

        Interaction updatedInteraction = interactionRepository.save(interaction);
        auditLogService.logSystemActivity("INTERACTION_UPDATED", "INTERACTION", updatedInteraction.getId());

        return interactionMapper.toDto(updatedInteraction);
    }

    @Override
    @Transactional
    public void deleteInteraction(Long interactionId) {
        if (!interactionRepository.existsById(interactionId)) {
            throw new ResourceNotFoundException("Interaction not found with id: " + interactionId);
        }

        interactionRepository.deleteById(interactionId);
        auditLogService.logSystemActivity("INTERACTION_DELETED", "INTERACTION", interactionId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InteractionDTO> findById(Long interactionId) {
        return interactionRepository.findById(interactionId)
            .map(interactionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InteractionDTO> findAllInteractions() {
        return interactionMapper.toDtoList(interactionRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InteractionDTO> findAllInteractionsPaginated(Pageable pageable) {
        return interactionRepository.findAll(pageable)
            .map(interactionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InteractionDTO> findInteractionsByCustomer(Long customerId) {
        return interactionMapper.toDtoList(interactionRepository.findByCustomerId(customerId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InteractionDTO> findInteractionsByCustomerPaginated(Long customerId, Pageable pageable) {
        return interactionRepository.findByCustomerId(customerId, pageable)
            .map(interactionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InteractionDTO> findInteractionsByUser(Long userId) {
        return interactionMapper.toDtoList(interactionRepository.findByUserId(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InteractionDTO> findInteractionsByType(InteractionType type) {
        return interactionMapper.toDtoList(interactionRepository.findByType(type));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InteractionDTO> findRecentInteractions(int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return interactionMapper.toDtoList(interactionRepository.findAll(pageRequest).getContent());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InteractionDTO> findInteractionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return interactionMapper.toDtoList(
            interactionRepository.findByCreatedAtBetween(startDate, endDate)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<InteractionType, Long> getInteractionTypeCounts(Long customerId) {
        return interactionRepository.findByCustomerId(customerId).stream()
            .collect(Collectors.groupingBy(
                Interaction::getType,
                Collectors.counting()
            ));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getInteractionCountsByDate(LocalDateTime startDate, LocalDateTime endDate) {
        return interactionRepository.findByCreatedAtBetween(startDate, endDate).stream()
            .collect(Collectors.groupingBy(
                interaction -> interaction.getCreatedAt().toLocalDate().toString(),
                Collectors.counting()
            ));
    }

    private void validateInteractionDTO(InteractionDTO interactionDTO) {
        if (interactionDTO == null) {
            throw new IllegalArgumentException("Interaction cannot be null");
        }

        if (!StringUtils.hasText(interactionDTO.getTitle())) {
            throw new IllegalArgumentException("Interaction title is required");
        }

        if (interactionDTO.getType() == null) {
            throw new IllegalArgumentException("Interaction type is required");
        }

        if (interactionDTO.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (interactionDTO.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }
    }
} 