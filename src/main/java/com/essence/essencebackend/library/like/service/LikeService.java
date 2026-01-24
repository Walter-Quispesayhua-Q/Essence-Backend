package com.essence.essencebackend.library.like.service;

public interface LikeService {

    boolean addLikeToSong(Long id, String username);
    boolean deleteLikeToSong(Long id, String username);
}
