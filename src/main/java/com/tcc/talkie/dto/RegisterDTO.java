package com.tcc.talkie.dto;

public record RegisterDTO(
    String name,
    String email,
    String password,
    String cpf
) {}