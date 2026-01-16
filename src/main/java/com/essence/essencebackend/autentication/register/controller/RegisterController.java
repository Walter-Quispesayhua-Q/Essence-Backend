package com.essence.essencebackend.autentication.register.controller;

import com.essence.essencebackend.autentication.register.dto.RegisterRequestDTO;
import com.essence.essencebackend.autentication.register.dto.RegisterResponseDTO;
import com.essence.essencebackend.autentication.register.service.RegisterService;
import com.essence.essencebackend.shared.dto.ResponseApi;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/register")
public class RegisterController {

    private final RegisterService registerService;

    @GetMapping
    public ResponseEntity<ResponseApi<Boolean>> getAvailableUsername(@RequestParam @NotBlank String username) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseApi<>("Usuario disponible", registerService.getAvailableUsername(username))
                );
    }

    @PostMapping
    public ResponseEntity<ResponseApi<RegisterResponseDTO>> createUser(@RequestBody RegisterRequestDTO data){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseApi<>(
                        "Usuario creado exitosamente!", registerService.createUser(data))
                );
    }


}
