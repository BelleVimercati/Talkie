package com.tcc.talkie.dto.response;

import java.util.UUID;

public record OccurrenceResponseDTO(
    String title,
    String description,
    String location,
    UUID ownerId,
    String categoryName,
    String subcategoryName
) {}