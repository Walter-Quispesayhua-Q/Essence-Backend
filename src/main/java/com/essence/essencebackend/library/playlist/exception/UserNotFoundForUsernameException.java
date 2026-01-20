package com.essence.essencebackend.library.playlist.exception;

public class UserNotFoundForUsernameException extends RuntimeException{
    public UserNotFoundForUsernameException(String username) {
        super("Usuario no encontrado, vuelva a iniciar sesión" + username);
    }
}
