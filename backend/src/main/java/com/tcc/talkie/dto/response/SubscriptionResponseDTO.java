package com.tcc.talkie.dto.response;

import java.time.LocalDateTime;

public record SubscriptionResponseDTO(Long id, Long categoryId, String categoryName, LocalDateTime subscribedAt) {}
