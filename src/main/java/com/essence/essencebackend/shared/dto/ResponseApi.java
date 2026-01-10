package com.essence.essencebackend.shared.dto;

public record ResponseApi<T>(
        String message,
        T data
) {
}
