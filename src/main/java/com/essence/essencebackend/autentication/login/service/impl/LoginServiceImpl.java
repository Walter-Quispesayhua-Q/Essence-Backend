package com.essence.essencebackend.autentication.login.service.impl;

import com.essence.essencebackend.autentication.login.dto.LoginRequestDTO;
import com.essence.essencebackend.autentication.login.dto.LoginResponseDTO;
import com.essence.essencebackend.autentication.login.dto.LoginTokenDTO;
import com.essence.essencebackend.autentication.login.exception.UserNotFound;
import com.essence.essencebackend.autentication.login.service.LoginService;
import com.essence.essencebackend.autentication.shared.mapper.UserMapper;
import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.autentication.shared.repository.UserRepository;
import com.essence.essencebackend.security.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserMapper userMapper;
    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;

    @Override
    public LoginTokenDTO login(LoginRequestDTO data) {
        log.info("Iniciando autenticación para: {}", data.email());

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(data.email(), data.password())
        );

        String token = tokenService.tokenGenerator(auth);

        return new LoginTokenDTO(token);
    }

    @Override
    public LoginResponseDTO getUser(String authHeader) {
        log.info("Obteniendo Usuario desde token");

        String token = authHeader.replace("Bearer ", "");

        Jwt jwt = jwtDecoder.decode(token);

        String email = jwt.getSubject();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFound(email));

        return userMapper.toLoginDTO(user);
    }
}
