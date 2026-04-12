package com.essence.essencebackend.shared.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ValidationAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleBodyValidation(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Datos de entrada inválidos");
        pd.setDetail("Uno o más campos del cuerpo de la solicitud no son válidos.");

        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        pd.setProperty("errors", errors);
        return pd;
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ProblemDetail handleParameterValidation(HandlerMethodValidationException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Parámetros inválidos");
        pd.setDetail("Uno o más parámetros de la solicitud no cumplen los requisitos.");

        Map<String, String> errors = new LinkedHashMap<>();

        ex.getValueResults().forEach(result ->
                result.getResolvableErrors().forEach(error -> {
                    String paramName = result.getMethodParameter().getParameterName();
                    String message   = error.getDefaultMessage();
                    if (paramName != null && message != null) {
                        errors.put(paramName, message);
                    }
                })
        );

        ex.getBeanResults().forEach(result ->
                result.getFieldErrors().forEach(fe ->
                        errors.put(fe.getField(), fe.getDefaultMessage())
                )
        );

        if (!errors.isEmpty()) {
            pd.setProperty("errors", errors);
        }

        return pd;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Solicitud invalida");
        pd.setDetail(ex.getMessage());
        return pd;
    }
}
