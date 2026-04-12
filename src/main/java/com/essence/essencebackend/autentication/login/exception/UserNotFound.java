package com.essence.essencebackend.autentication.login.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserNotFound extends UsernameNotFoundException {
    public UserNotFound() {
        super("Credenciales invalidas");
    }
}
