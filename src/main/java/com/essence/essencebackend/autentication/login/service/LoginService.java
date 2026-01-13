package com.essence.essencebackend.autentication.login.service;

import com.essence.essencebackend.autentication.login.dto.LoginRequestDTO;
import com.essence.essencebackend.autentication.login.dto.LoginResponseDTO;
import com.essence.essencebackend.autentication.login.dto.LoginTokenDTO;

public interface LoginService {
    LoginTokenDTO login(LoginRequestDTO data);
    LoginResponseDTO getUser(String authHeader);
}
