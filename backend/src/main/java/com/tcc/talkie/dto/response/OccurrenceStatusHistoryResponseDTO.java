package com.tcc.talkie.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tcc.talkie.domain.occurrence.OccurrenceStatus;

/* changedById/changedByName são nulos quando o autor do evento excluiu a conta. */
public record OccurrenceStatusHistoryResponseDTO(
    Long id,
    OccurrenceStatus previousStatus,
    OccurrenceStatus newStatus,
    String reason,
    LocalDateTime changedAt,
    UUID changedById,
    String changedByName
) {}
