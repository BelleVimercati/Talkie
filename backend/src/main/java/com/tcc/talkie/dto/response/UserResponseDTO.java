package com.tcc.talkie.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import com.tcc.talkie.domain.user.Role;

public record UserResponseDTO(UUID id, String name, String email, String cpf, Role role, LocalDateTime createdAt) {

}
