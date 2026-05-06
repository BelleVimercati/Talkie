package com.tcc.talkie.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcc.talkie.domain.occurrence.Occurrence;
import com.tcc.talkie.domain.user.User;

public interface OccurrenceRepository extends JpaRepository<Occurrence, Long> {
    Optional<Occurrence> findById(Long id);

    List<Occurrence> findByOwnerId(UUID ownerId);
}
