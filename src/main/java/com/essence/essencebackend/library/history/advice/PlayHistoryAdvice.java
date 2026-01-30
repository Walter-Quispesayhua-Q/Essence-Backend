package com.essence.essencebackend.library.history.advice;

import com.essence.essencebackend.library.history.controller.PlayHistoryController;
import com.essence.essencebackend.library.history.exception.AddToHistoryFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = PlayHistoryController.class)
public class PlayHistoryAdvice {
    @ExceptionHandler(AddToHistoryFailedException.class)
    public ProblemDetail AddToHistoryFailed(AddToHistoryFailedException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("No se pudo agregar la canción al historial");
        pd.setDetail(ex.getMessage());
        return pd;
    }
}
