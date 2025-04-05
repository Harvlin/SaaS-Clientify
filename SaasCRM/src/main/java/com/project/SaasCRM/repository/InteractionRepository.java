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
}