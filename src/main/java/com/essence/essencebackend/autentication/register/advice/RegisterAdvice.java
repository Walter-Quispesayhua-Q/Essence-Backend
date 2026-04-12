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

    private static final String CONFLICT_TITLE  = "Datos en uso";
    private static final String CONFLICT_DETAIL = "El nombre de usuario o correo ya esta siendo utilizado.";

    @ExceptionHandler({DuplicateEmailException.class, DuplicateUsernameException.class})
    public ProblemDetail duplicateCredential(RuntimeException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle(CONFLICT_TITLE);
        pd.setDetail(CONFLICT_DETAIL);
        return pd;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail integrityViolation(DataIntegrityViolationException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle(CONFLICT_TITLE);
        pd.setDetail(CONFLICT_DETAIL);
        return pd;
    }

    @ExceptionHandler(RegisterFailedException.class)
    public ProblemDetail registerFailed(RegisterFailedException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("No se pudo crear el usuario");
        pd.setDetail("Ocurrio un error inesperado. Intente nuevamente.");
        return pd;
    }
}
