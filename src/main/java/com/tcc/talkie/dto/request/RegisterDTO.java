package com.tcc.talkie.dto.request;

import com.tcc.talkie.domain.user.Role;

public record RegisterDTO(
    String name,
    String email,
    String password,
    String cpf,
    Role role
) {}