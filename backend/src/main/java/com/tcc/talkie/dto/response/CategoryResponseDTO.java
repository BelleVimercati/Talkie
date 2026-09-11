package com.tcc.talkie.dto.response;

import java.util.UUID;

public record CategoryResponseDTO(Long id, String name, String icon, UUID userId) {

}
