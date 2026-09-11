package com.tcc.talkie.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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
    Long subcategoryId
) {}