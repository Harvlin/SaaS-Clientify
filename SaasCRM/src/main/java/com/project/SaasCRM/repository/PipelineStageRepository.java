package com.project.SaasCRM.repository;

import com.project.SaasCRM.domain.entity.PipelineStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PipelineStageRepository extends JpaRepository<PipelineStage, Long> {
    Optional<PipelineStage> findByName(String name);
    
    List<PipelineStage> findAllByOrderByDisplayOrderAsc();
    
    @Query("SELECT ps.name, COUNT(d) FROM PipelineStage ps LEFT JOIN Deal d ON d.stage = ps.id GROUP BY ps.name")
    List<Object[]> getDealCountsByStage();
}
