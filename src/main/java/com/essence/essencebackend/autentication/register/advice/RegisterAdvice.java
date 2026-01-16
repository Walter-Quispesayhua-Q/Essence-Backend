package com.essence.essencebackend.autentication.register.advice;

import com.essence.essencebackend.autentication.register.controller.RegisterController;
import com.essence.essencebackend.autentication.register.exception.DuplicateEmailException;
import com.essence.essencebackend.autentication.register.exception.DuplicateUsernameException;
import com.essence.essencebackend.autentication.register.exception.RegisterFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = RegisterController.class)
public class RegisterAdvice {

    @ExceptionHandler(DuplicateEmailException.class)
    public ProblemDetail duplicateEmail(DuplicateEmailException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Email ya registrado");
        pd.setDetail("Ya existe un usuario con ese correo.");
        return pd;
    }

    @ExceptionHandler(RegisterFailedException.class)
    public ProblemDetail registerFailed(RegisterFailedException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("No se pudo crear el usuario");
        pd.setDetail("Ocurrió un error inesperado. Intente nuevamente.");
        return pd;
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ProblemDetail duplicateUsername(DuplicateUsernameException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Nombre de usuario ya esta siendo usado");
        pd.setDetail("El nombre de usuario esta siendo usado por otro, pruebe otro usuario");
        return pd;
    }
}
