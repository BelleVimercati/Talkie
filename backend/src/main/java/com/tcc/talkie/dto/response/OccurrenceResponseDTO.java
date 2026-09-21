package com.tcc.talkie.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tcc.talkie.domain.occurrence.OccurrenceStatus;

public record OccurrenceResponseDTO(
    Long id,
    String title,
    String description,
    String location,
    UUID ownerId,
    String ownerName,
    String categoryName,
    String subcategoryName,
    OccurrenceStatus status,
    LocalDateTime createdAt,
    LocalDateTime resolvedAt
) {}