package com.example.spring_practice.repository;

import com.example.spring_practice.model.entities.EmpruntEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmpruntRepository extends JpaRepository<EmpruntEntity, Long> {
    // You can add custom query methods here if needed
    long countByAdherentIdAndDateRetourPrevueAfter(Long adherentId, java.time.LocalDateTime now);
    List<EmpruntEntity> findByAdherentId(Long adherentId);
    List<EmpruntEntity> findByExemplaireId(Long exemplaireId);
} 