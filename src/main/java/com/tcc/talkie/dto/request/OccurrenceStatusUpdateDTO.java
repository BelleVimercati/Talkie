package com.tcc.talkie.dto.request;

import com.tcc.talkie.domain.occurrence.OccurrenceStatus;

import jakarta.validation.constraints.NotNull;

public record OccurrenceStatusUpdateDTO(
    @NotNull(message = "Status é obrigatório")
    OccurrenceStatus status
) {}
