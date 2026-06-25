package com.tcc.talkie.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubcategoryCreateDTO(
    @NotBlank(message = "Nome da subcategoria é obrigatório")
    String name,

    @NotNull(message = "Categoria é obrigatória")
    Long categoryId
) {}
