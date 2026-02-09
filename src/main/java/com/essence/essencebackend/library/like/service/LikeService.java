package com.essence.essencebackend.library.like.service;

public interface LikeService<ID> {

//    espera id de canción, album, artista y usuario.
    void addLike(ID id, String username);
    void deleteLike(ID id, String username);
}
