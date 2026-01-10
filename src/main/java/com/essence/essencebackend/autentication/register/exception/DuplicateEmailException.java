package com.essence.essencebackend.autentication.register.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("Email ya registrado: " + email);
    }
}
