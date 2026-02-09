package com.essence.essencebackend.user.controller;

import com.essence.essencebackend.autentication.login.dto.LoginResponseDTO;
import com.essence.essencebackend.shared.dto.ResponseApi;
import com.essence.essencebackend.user.dto.UserDetailDTO;
import com.essence.essencebackend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ResponseApi<LoginResponseDTO>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseApi<>("Usuario encontrado exitosamente!",
                        userService.getCurrentUser(userDetails.getUsername()))
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<ResponseApi<UserDetailDTO>> getUserProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseApi<>("Perfil obtenido exitosamente!",
                        userService.getUserProfile(userDetails.getUsername()))
        );
    }
}
