package com.project.SaasCRM.repository;

import com.project.SaasCRM.domain.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
    
    Optional<EmailTemplate> findByName(String name);
    
    List<EmailTemplate> findByActive(boolean active);
    
    @Query("SELECT t FROM EmailTemplate t WHERE t.active = true AND t.lastUsedAt < :date")
    List<EmailTemplate> findUnusedActiveTemplates(@Param("date") LocalDateTime date);
    
    @Modifying
    @Query("UPDATE EmailTemplate t SET t.usageCount = t.usageCount + 1, t.lastUsedAt = CURRENT_TIMESTAMP WHERE t.id = :id")
    void incrementUsageCount(@Param("id") Long id);
    
    @Query("SELECT t FROM EmailTemplate t WHERE t.active = true ORDER BY t.usageCount DESC")
    List<EmailTemplate> findMostUsedTemplates();
    
    boolean existsByName(String name);

    List<EmailTemplate> findByType(String type);

    List<EmailTemplate> findByCreatedBy(Long userId);
}
