package com.tcc.talkie.dto.response;

import java.util.UUID;

public record UserResponseDTO(UUID id, String name, String email) {

}
