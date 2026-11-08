package com.tcc.talkie.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record OccurrenceDTO(
    @JsonProperty("title")
    @NotBlank(message = "Título é obrigatório")
    String title,

    @JsonProperty("description")
    @NotBlank(message = "Descrição é obrigatória")
    String description,

    @JsonProperty("location")
    @NotBlank(message = "Localização é obrigatória")
    String location,

    @JsonProperty("categoryId")
    @NotNull(message = "Categoria é obrigatória")
    Long categoryId,

    @JsonProperty("subcategoryId")
    @NotNull(message = "Subcategoria é obrigatória")
    Long subcategoryId,

    @JsonProperty("occurredAt")
    @PastOrPresent(message = "Data/hora do fato não pode ser no futuro")
    LocalDateTime occurredAt,

    @JsonProperty("latitude")
    @DecimalMin(value = "-90", message = "Latitude deve estar entre -90 e 90")
    @DecimalMax(value = "90", message = "Latitude deve estar entre -90 e 90")
    Double latitude,

    @JsonProperty("longitude")
    @DecimalMin(value = "-180", message = "Longitude deve estar entre -180 e 180")
    @DecimalMax(value = "180", message = "Longitude deve estar entre -180 e 180")
    Double longitude
) {}