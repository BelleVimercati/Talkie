package com.tcc.talkie.dto.response;

public record ApiResponse<T>(
    String message,
    T data
) {}