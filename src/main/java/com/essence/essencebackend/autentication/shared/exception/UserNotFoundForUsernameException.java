package com.essence.essencebackend.autentication.shared.exception;

public class UserNotFoundForUsernameException extends RuntimeException {
    public UserNotFoundForUsernameException(String username) {
        super("Usuario no encontrado para la sesión actual: " + username);
    }
}
