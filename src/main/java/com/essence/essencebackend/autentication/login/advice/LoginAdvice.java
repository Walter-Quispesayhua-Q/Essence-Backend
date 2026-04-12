package com.essence.essencebackend.autentication.login.advice;

import com.essence.essencebackend.autentication.login.controller.LoginController;
import com.essence.essencebackend.autentication.login.exception.UserNotFound;
import com.essence.essencebackend.security.ratelimit.RateLimitExceededException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = LoginController.class)
public class LoginAdvice {

    @ExceptionHandler({UserNotFound.class, AuthenticationException.class})
    public ProblemDetail loginFailed(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setTitle("Credenciales invalidas");
        pd.setDetail("Correo o contrasena incorrectos.");
        return pd;
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ProblemDetail> rateLimitExceeded(RateLimitExceededException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.TOO_MANY_REQUESTS);
        pd.setTitle("Demasiados intentos");
        pd.setDetail("Intente nuevamente en unos minutos.");

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, String.valueOf(ex.getRetryAfterSeconds()))
                .body(pd);
    }
}
