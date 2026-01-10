package com.essence.essencebackend.autentication.register.service;

import com.essence.essencebackend.autentication.register.dto.UserRequestDTO;
import com.essence.essencebackend.autentication.register.dto.UserResponseDTO;

public interface RegisterService {
    UserResponseDTO createUser(UserRequestDTO data);
}
