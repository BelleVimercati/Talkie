package com.tcc.talkie.dto.response;

import java.util.UUID;

import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.category.Subcategory;

public record OccurrenceResponseDTO(
    String title,
    String description,
    String location,
    UUID ownerId,
    Long category,
    Long subcategory
) {}