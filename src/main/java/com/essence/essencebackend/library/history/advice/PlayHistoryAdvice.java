package com.essence.essencebackend.library.history.advice;

import com.essence.essencebackend.library.history.controller.PlayHistoryController;
import com.essence.essencebackend.library.history.exception.AddToHistoryFailedException;
import com.essence.essencebackend.library.playlist.exception.PlaylistNotFoundException;
import com.essence.essencebackend.music.album.exception.AlbumNotFoundException;
import com.essence.essencebackend.music.song.exception.SongNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = PlayHistoryController.class)
public class PlayHistoryAdvice {

    @ExceptionHandler(AddToHistoryFailedException.class)
    public ProblemDetail addToHistoryFailed(AddToHistoryFailedException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("No se pudo agregar la canción al historial");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(SongNotFoundException.class)
    public ProblemDetail songNotFound(SongNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Canción no encontrada");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(AlbumNotFoundException.class)
    public ProblemDetail albumNotFound(AlbumNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Álbum no encontrado");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(PlaylistNotFoundException.class)
    public ProblemDetail playlistNotFound(PlaylistNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Playlist no encontrada");
        pd.setDetail(ex.getMessage());
        return pd;
    }
}
