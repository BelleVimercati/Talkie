package com.tcc.talkie.dto.response;

import java.util.UUID;

import com.tcc.talkie.domain.occurrence.OccurrenceStatus;

public record OccurrenceResponseDTO(
    String title,
    String description,
    String location,
    UUID ownerId,
    String categoryName,
    String subcategoryName,
    OccurrenceStatus status
) {}