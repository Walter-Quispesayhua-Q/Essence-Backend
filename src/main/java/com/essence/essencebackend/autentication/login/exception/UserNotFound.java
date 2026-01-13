package com.essence.essencebackend.autentication.login.exception;

public class UserNotFound extends RuntimeException{
    public UserNotFound(String email) {
        super("Usuario no existe: "+ email);
    }
}
