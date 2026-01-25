package com.essence.essencebackend.library.like.exception;

public class DeleteLikeFailedException extends RuntimeException {
    public DeleteLikeFailedException(Throwable causa) {
        super("No se pudo procesar tu solicitud para quitar el me gusta: ", causa);
    }
}
