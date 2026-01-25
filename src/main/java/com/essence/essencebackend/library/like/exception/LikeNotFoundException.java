package com.essence.essencebackend.library.like.exception;

public class LikeNotFoundException extends RuntimeException {
    public LikeNotFoundException(Object entityId, String username) {
        super("Like no encontrado para entityId: " + entityId + ", username: " + username);
    }
}