package com.essence.essencebackend.autentication.register.exception;

public class DuplicateUsernameException extends RuntimeException{
    public DuplicateUsernameException(String username) {
        super("El nombre de usuario ya esta siendo usado: " + username);
    }
}
