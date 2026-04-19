package com.essence.essencebackend.autentication.register.advice;

import com.essence.essencebackend.autentication.register.controller.RegisterController;
import com.essence.essencebackend.autentication.register.exception.DuplicateEmailException;
import com.essence.essencebackend.autentication.register.exception.DuplicateUsernameException;
import com.essence.essencebackend.autentication.register.exception.RegisterFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = RegisterController.class)
public class RegisterAdvice {

    @ExceptionHandler(DuplicateEmailException.class)
    public ProblemDetail duplicateEmail(DuplicateEmailException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Email en uso");
        pd.setDetail("El correo electrónico ya está registrado.");
        pd.setProperty("field", "email");
        return pd;
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ProblemDetail duplicateUsername(DuplicateUsernameException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Username en uso");
        pd.setDetail("El nombre de usuario ya está siendo utilizado.");
        pd.setProperty("field", "username");
        return pd;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail integrityViolation(DataIntegrityViolationException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Datos en uso");
        pd.setDetail("El nombre de usuario o correo ya está siendo utilizado.");
        return pd;
    }

    @ExceptionHandler(RegisterFailedException.class)
    public ProblemDetail registerFailed(RegisterFailedException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("No se pudo crear el usuario");
        pd.setDetail("Ocurrió un error inesperado. Intente nuevamente.");
        return pd;
    }
}
