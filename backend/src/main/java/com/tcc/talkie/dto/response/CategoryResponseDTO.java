package com.tcc.talkie.dto.response;

import java.util.UUID;
import java.time.LocalDateTime;
import com.tcc.talkie.domain.category.CategoryPriority;

public record CategoryResponseDTO(
    Long id,
    String name,
    String icon,
    UUID userId,
    CategoryPriority priority,
    String color,
    LocalDateTime createdAt
) {}
