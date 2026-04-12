package com.essence.essencebackend.shared.advice;

import com.essence.essencebackend.autentication.shared.exception.UserNotFoundForUsernameException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalAuthAdvice {

    @ExceptionHandler(UserNotFoundForUsernameException.class)
    public ProblemDetail userMissingForToken(UserNotFoundForUsernameException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setTitle("Sesion invalida");
        pd.setDetail(ex.getMessage());
        return pd;
    }
}

