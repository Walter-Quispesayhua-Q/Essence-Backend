package com.essence.essencebackend.shared.error;

import com.essence.essencebackend.autentication.register.exception.DuplicateEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class ApiErrors {
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ProblemDetail> duplicateEmail(DuplicateEmailException ex) {

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Email ya registrado");
        pd.setDetail("Ya existe un usuario con ese correo.");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }
}
