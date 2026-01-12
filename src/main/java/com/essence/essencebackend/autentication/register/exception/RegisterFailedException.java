package com.essence.essencebackend.autentication.register.exception;

public class RegisterFailedException extends RuntimeException {
    public RegisterFailedException(Throwable cause) {
        super("REGISTRO DE USUARIO FALLIDO", cause);
    }
}

