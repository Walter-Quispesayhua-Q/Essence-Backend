package com.essence.essencebackend.library.like.service.impl;

import org.springframework.data.jpa.repository.JpaRepository;

public abstract class BaseLikeService<T, ID> {

    protected abstract JpaRepository<T, ID> getRepository();

    protected abstract RuntimeException createNotFoundException(ID id);

    protected T findByIdOrThrow(ID id) {
        return getRepository().findById(id).orElseThrow(
                () -> createNotFoundException(id)
        );
    }
}
