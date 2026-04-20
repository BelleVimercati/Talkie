package com.tcc.talkie.dto.request;

import java.util.UUID;

public record CategoryCreateDTO(String name, String icon, UUID userId) {

}
