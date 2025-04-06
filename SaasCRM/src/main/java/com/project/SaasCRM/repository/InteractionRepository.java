package com.project.SaasCRM.repository;

import com.project.SaasCRM.domain.InteractionType;
import com.project.SaasCRM.domain.entity.Customer;
import com.project.SaasCRM.domain.entity.Interaction;
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
public interface InteractionRepository extends JpaRepository<Interaction, Long> {
    List<Interaction> findByCustomer(Customer customer);

    List<Interaction> findByUser(User user);

    List<Interaction> findByType(InteractionType type);

    @Query("SELECT i FROM Interaction i WHERE i.customer.id = :customerId")
    Page<Interaction> findByCustomerIdPaginated(@Param("customerId") Long customerId, Pageable pageable);

    @Query("SELECT i FROM Interaction i WHERE " +
            "i.interactionDate >= :startDate AND i.interactionDate <= :endDate")
    List<Interaction> findByInteractionDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    List<Interaction> findByCustomerId(Long customerId);
    
    Page<Interaction> findByCustomerId(Long customerId, Pageable pageable);
    
    List<Interaction> findByUserId(Long userId);
    
    List<Interaction> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    List<Interaction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT i.type, COUNT(i) FROM Interaction i WHERE i.customer.id = :customerId GROUP BY i.type")
    List<Object[]> countByTypeForCustomer(@Param("customerId") Long customerId);
    
    @Query("SELECT DATE(i.createdAt) as date, COUNT(i) FROM Interaction i " +
           "WHERE i.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(i.createdAt)")
    List<Object[]> countByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    List<Interaction> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    
    List<Interaction> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Interaction> findByTypeOrderByCreatedAtDesc(InteractionType type);
    
    List<Interaction> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT i FROM Interaction i " +
           "WHERE i.customer.id = :customerId " +
           "AND i.type = :type " +
           "ORDER BY i.createdAt DESC")
    List<Interaction> findByCustomerIdAndType(
        @Param("customerId") Long customerId,
        @Param("type") InteractionType type
    );
    
    @Query("SELECT i FROM Interaction i " +
           "WHERE i.user.id = :userId " +
           "AND i.createdAt >= :since " +
           "ORDER BY i.createdAt DESC")
    List<Interaction> findRecentByUser(
        @Param("userId") Long userId,
        @Param("since") LocalDateTime since
    );
    
    @Query("SELECT COUNT(i) FROM Interaction i " +
           "WHERE i.customer.id = :customerId " +
           "AND i.createdAt >= :since")
    long countRecentByCustomer(
        @Param("customerId") Long customerId,
        @Param("since") LocalDateTime since
    );
}