package com.essence.essencebackend.autentication.register.dto;

public record UserResponseDTO(
        Long id,
        String username,
        String email
) {
}
