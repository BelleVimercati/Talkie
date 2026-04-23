package com.tcc.talkie.dto.request;

public record OccurrenceDTO(
    String title,
    String description,
    String location,
    Long categoryId,
    Long subcategoryId
) {

}
