package com.tcc.talkie.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tcc.talkie.domain.occurrence.Occurrence;

public interface OccurrenceRepository extends JpaRepository<Occurrence, Long> {
    Optional<Occurrence> findById(Long id);

    List<Occurrence> findByOwnerId(UUID ownerId);

    List<Occurrence> findByCategoryId(Long categoryId);

    @Modifying
    @Query("update Occurrence o set o.owner = null where o.owner.id = :userId")
    int detachOwner(@Param("userId") UUID userId);
}
