package com.essence.essencebackend.autentication.register.controller;

import com.essence.essencebackend.autentication.register.dto.RegisterRequestDTO;
import com.essence.essencebackend.autentication.register.dto.RegisterResponseDTO;
import com.essence.essencebackend.autentication.register.service.RegisterService;
import com.essence.essencebackend.shared.dto.ResponseApi;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/register")
public class RegisterController {

    private final RegisterService registerService;

    @GetMapping("/username")
    public ResponseEntity<ResponseApi<Boolean>> getAvailableUsername(
            @RequestParam @NotBlank(message = "El nombre de usuario no puede estar vacio") @Size(min = 3, max = 30, message = "El nombre de usuario debe tener entre 3 y 30 caracteres") @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "El nombre de usuario solo puede contener letras, numeros, guiones, puntos y guiones bajos") String username) {

        boolean available = registerService.getAvailableUsername(username);
        String message = available ? "Nombre de usuario disponible" : "Nombre de usuario no disponible";
        return ResponseEntity.ok(new ResponseApi<>(message, available));
    }

    @PostMapping
    public ResponseEntity<ResponseApi<RegisterResponseDTO>> createUser(@RequestBody @Valid RegisterRequestDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseApi<>(
                        "Usuario creado exitosamente!", registerService.createUser(data)));
    }
}
