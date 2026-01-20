package com.essence.essencebackend.library.playlist.exception;

public class TitleEmptyException extends RuntimeException{
    public TitleEmptyException() {
        super("el titulo no puede estar vació");
    }
}
