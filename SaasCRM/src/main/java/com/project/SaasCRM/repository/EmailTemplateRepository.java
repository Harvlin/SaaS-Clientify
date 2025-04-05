package com.project.SaasCRM.repository;

import com.project.SaasCRM.domain.entity.EmailTemplate;
import com.project.SaasCRM.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
    List<EmailTemplate> findByTemplateType(String templateType);

    List<EmailTemplate> findByCreatedBy(User user);

    Optional<EmailTemplate> findByName(String name);
}
