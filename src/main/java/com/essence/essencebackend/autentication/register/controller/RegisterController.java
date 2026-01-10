package com.essence.essencebackend.autentication.register.controller;

import com.essence.essencebackend.autentication.register.dto.UserRequestDTO;
import com.essence.essencebackend.autentication.register.dto.UserResponseDTO;
import com.essence.essencebackend.autentication.register.service.RegisterService;
import com.essence.essencebackend.shared.dto.ResponseApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/signup")
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping
    public ResponseEntity<ResponseApi<UserResponseDTO>> createUser(@RequestBody UserRequestDTO data){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseApi<>(
                        "Usuario creado exitosamente!", registerService.createUser(data))
                );
    }
}
