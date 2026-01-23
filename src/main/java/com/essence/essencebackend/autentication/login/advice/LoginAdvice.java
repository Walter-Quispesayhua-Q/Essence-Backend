package com.essence.essencebackend.autentication.login.advice;

import com.essence.essencebackend.autentication.login.controller.LoginController;
import com.essence.essencebackend.autentication.login.exception.UserNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = LoginController.class)
public class LoginAdvice {

    @ExceptionHandler(UserNotFound.class)
    public ProblemDetail userNotFound(UserNotFound ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Usuario no encontrado");
        pd.setDetail("El usuario no esta registrado, regístrese y vuelva a intentar");
        return pd;
    }
}
