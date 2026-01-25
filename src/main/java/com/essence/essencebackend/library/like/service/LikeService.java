package com.essence.essencebackend.library.like.service;

public interface LikeService<ID> {
    void addLike(ID id, String username);
    void deleteLike(ID id, String username);
}
