package com.essence.essencebackend.autentication.login.controller;

import com.essence.essencebackend.autentication.login.dto.LoginRequestDTO;
import com.essence.essencebackend.autentication.login.dto.LoginResponseDTO;
import com.essence.essencebackend.autentication.login.dto.LoginTokenDTO;
import com.essence.essencebackend.autentication.login.service.LoginService;
import com.essence.essencebackend.shared.dto.ResponseApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/login")
public class LoginController {
    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<ResponseApi<LoginTokenDTO>> login(@RequestBody @Valid LoginRequestDTO data) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseApi<>("Inicio de sesión exitoso!", loginService.login(data))
        );
    }
}
