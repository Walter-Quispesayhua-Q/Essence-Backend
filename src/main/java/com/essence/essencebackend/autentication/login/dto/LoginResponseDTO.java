package com.essence.essencebackend.autentication.login.dto;

import java.time.Instant;

public record LoginResponseDTO(
        Long id,
        String username,
        String email,
        Instant createdAt,
        Instant updatedAt
) {
}
