package com.tcc.talkie.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OccurrenceDTO(
    @JsonProperty("title") String title,
    @JsonProperty("description") String description,
    @JsonProperty("location") String location,
    @JsonProperty("categoryId") Long categoryId,
    @JsonProperty("subcategoryId") Long subcategoryId
) {}