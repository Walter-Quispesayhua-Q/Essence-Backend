package com.essence.essencebackend.library.like.exception;

public class AddLikeFailedException extends RuntimeException {
    public AddLikeFailedException(Throwable causa) {
        super("No se pudo registrar tu me gusta en este momento: ", causa);
    }
}
