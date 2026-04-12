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
import com.essence.essencebackend.security.ratelimit.RateLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.Locale;


@RequiredArgsConstructor
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserMapper userMapper;
    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;
    private final RateLimitService rateLimitService;

    @Override
    public LoginTokenDTO login(LoginRequestDTO data) {
        String email = data.email().trim().toLowerCase(Locale.ROOT);
        log.info("Iniciando autenticación para: {}", email);

        rateLimitService.assertLoginIdentityAllowed(email);

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, data.password())
            );

            rateLimitService.clearLoginFailures(email);

            String token = tokenService.tokenGenerator(auth);

            return new LoginTokenDTO(token);
        } catch (AuthenticationException ex) {
            rateLimitService.recordLoginFailure(email);
            throw ex;
        }
    }

    @Override
    public LoginResponseDTO getUser(String authHeader) {
        log.info("Obteniendo usuario desde token");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UserNotFound();
        }
        String token = authHeader.substring(7);

        Jwt jwt = jwtDecoder.decode(token);

        String username = jwt.getSubject();

        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFound::new);

        return userMapper.toLoginDTO(user);
    }
}
