package com.essence.essencebackend.library.playlist.advice;


import com.essence.essencebackend.library.playlist.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.essence.essencebackend.library.playlist")
public class PlaylistAdvice {

    @ExceptionHandler(PlaylistNotFoundException.class)
    public ProblemDetail playlistNotFound(PlaylistNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Playlist no encontrada");
        pd.setDetail(ex.getMessage());
        return pd;
    }
    @ExceptionHandler(TitleEmptyException.class)
    public ProblemDetail titleEmpty(TitleEmptyException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Datos inválidos");
        pd.setDetail(ex.getMessage());
        return pd;
    }
    @ExceptionHandler(DuplicatePlaylistException.class)
    public ProblemDetail duplicatePlaylist(DuplicatePlaylistException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Playlist duplicada");
        pd.setDetail(ex.getMessage());
        return pd;
    }
    @ExceptionHandler(SongAlreadyInPlaylistException.class)
    public ProblemDetail songAlreadyInPlaylist(SongAlreadyInPlaylistException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Canción ya existe en playlist");
        pd.setDetail(ex.getMessage());
        return pd;
    }
    @ExceptionHandler(FailedToDeletePlaylistException.class)
    public ProblemDetail failedToDelete(FailedToDeletePlaylistException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Error al eliminar");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(SystemPlaylistCannotBeDeletedException.class)
    public ProblemDetail systemPlaylistCannotBeDeleted(SystemPlaylistCannotBeDeletedException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        pd.setTitle("Operación no permitida");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(SystemPlaylistCannotBeModifiedException.class)
    public ProblemDetail systemPlaylistCannotBeModified(SystemPlaylistCannotBeModifiedException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        pd.setDetail(ex.getMessage());
        pd.setTitle("Operación no permitida");
        return pd;
    }

    @ExceptionHandler(PrivatePlaylistLikeException.class)
    public ProblemDetail privatePlaylistLike(PrivatePlaylistLikeException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        pd.setTitle("Operación no permitida");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(SongNotInPlaylistException.class)
    public ProblemDetail songNotInPlaylist(SongNotInPlaylistException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Canción no encontrada en playlist");
        pd.setDetail(ex.getMessage());
        return pd;
    }

}
