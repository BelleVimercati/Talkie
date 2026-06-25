package com.tcc.talkie.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateDTO(
    @NotBlank(message = "Nome da categoria é obrigatório")
    String name,

    String icon
) {}
