package com.essence.essencebackend.autentication.login.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(

        @NotBlank(message = "El email no puede estar vacío")
        @Email
        String email,

        @NotBlank(message = "El password no puede estar vacío")
        String password
) {
}
