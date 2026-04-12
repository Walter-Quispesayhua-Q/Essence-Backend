package com.essence.essencebackend.shared.advice;

import com.essence.essencebackend.extractor.exception.ContentNotFoundException;
import com.essence.essencebackend.extractor.exception.ExtractionServiceUnavailableException;
import com.essence.essencebackend.extractor.exception.ExtractionTimeoutException;
import com.essence.essencebackend.music.album.exception.AlbumNotFoundException;
import com.essence.essencebackend.music.artist.exception.ArtistNotFoundException;
import com.essence.essencebackend.music.song.exception.SongNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalMusicAdvice {

    @ExceptionHandler(ExtractionServiceUnavailableException.class)
    public ProblemDetail extractionUnavailable(ExtractionServiceUnavailableException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
        pd.setTitle("Servicio de extraccion no disponible");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(ExtractionTimeoutException.class)
    public ProblemDetail extractionTimeout(ExtractionTimeoutException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.GATEWAY_TIMEOUT);
        pd.setTitle("Timeout en la extraccion");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(ContentNotFoundException.class)
    public ProblemDetail contentNotFound(ContentNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Contenido no encontrado");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(SongNotFoundException.class)
    public ProblemDetail songNotFound(SongNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Cancion no encontrada");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(AlbumNotFoundException.class)
    public ProblemDetail albumNotFound(AlbumNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Album no encontrado");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(ArtistNotFoundException.class)
    public ProblemDetail artistNotFound(ArtistNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Artista no encontrado");
        pd.setDetail(ex.getMessage());
        return pd;
    }
}
