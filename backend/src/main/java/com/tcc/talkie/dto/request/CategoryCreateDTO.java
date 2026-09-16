package com.tcc.talkie.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.tcc.talkie.domain.category.CategoryPriority;

public record CategoryCreateDTO(
    @NotBlank(message = "Nome da categoria é obrigatório")
    String name,

    String icon,

    @NotNull(message = "Prioridade é obrigatória")
    CategoryPriority priority,

    @NotBlank(message = "Cor é obrigatória")
    String color
) {}
