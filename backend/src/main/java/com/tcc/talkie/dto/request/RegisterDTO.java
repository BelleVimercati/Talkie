package com.tcc.talkie.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import com.tcc.talkie.domain.user.Role;

public record RegisterDTO(
    @NotBlank(message = "Nome é obrigatório")
    String name,

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    String email,

    @NotBlank(message = "Senha é obrigatória")
    String password,

    @NotBlank(message = "CPF é obrigatório")
    String cpf,

    Role role
) {}