package com.essence.essencebackend.autentication.register.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(

        @NotBlank(message = "el username no puede estar vacío")
        String username,

        @NotBlank(message = "el email no puede estar vacío")
        @Email
        String email,

        @NotBlank(message = "el password no puede estar vacío")
        String password
) {
}
