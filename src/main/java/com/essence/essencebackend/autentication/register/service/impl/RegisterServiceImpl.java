package com.essence.essencebackend.autentication.register.service.impl;

import com.essence.essencebackend.autentication.register.dto.UserRequestDTO;
import com.essence.essencebackend.autentication.register.dto.UserResponseDTO;
import com.essence.essencebackend.autentication.register.exception.DuplicateEmailException;
import com.essence.essencebackend.autentication.register.mapper.UserMapper;
import com.essence.essencebackend.autentication.register.model.User;
import com.essence.essencebackend.autentication.register.repository.UserRepository;
import com.essence.essencebackend.autentication.register.service.RegisterService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO data) {
        log.info("Iniciando la creación de un usuario: {}", data);
        if (userRepository.existsByEmail(data.email())) {
            throw new DuplicateEmailException(data.email());
        }
        User user = userMapper.toEntity(data);
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
