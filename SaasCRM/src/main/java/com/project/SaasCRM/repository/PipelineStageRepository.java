package com.project.SaasCRM.repository;

import com.project.SaasCRM.domain.entity.PipelineStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PipelineStageRepository extends JpaRepository<PipelineStage, Long> {
    List<PipelineStage> findAllByOrderByDisplayOrderAsc();

    Optional<PipelineStage> findByName(String name);
}
