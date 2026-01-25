package com.essence.essencebackend.library.like.service;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.autentication.shared.repository.UserRepository;
import com.essence.essencebackend.library.like.exception.AddLikeFailedException;
import com.essence.essencebackend.library.like.exception.DeleteLikeFailedException;
import com.essence.essencebackend.library.like.exception.LikeNotFoundException;
import com.essence.essencebackend.library.playlist.exception.UserNotFoundForUsernameException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
public abstract class BaseLikeService<E, ID, LIKE, LIKE_ID> implements LikeService<ID>{

    private final UserRepository userRepository;

    protected abstract JpaRepository<E, ID> entityRepository();
    protected abstract JpaRepository<LIKE, LIKE_ID> likeRepository();

    protected abstract RuntimeException entityNotFound(ID id);

    protected abstract LIKE_ID buildLikeId(E entity, User user);
    protected abstract LIKE buildLikeEntity(LIKE_ID id, E entity, User user);

    @Override
    @Transactional
    public void addLike(ID id, String username) {
        log.info("Agregando me gusta a la entityId: {}, por el username: {}", id, username);

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );
        E entity = entityRepository().findById(id).orElseThrow(
                () -> entityNotFound(id)
        );

        LIKE_ID likeId = buildLikeId(entity, user);

        if (likeRepository().existsById(likeId)) {
            return;
        }

        try {
            LIKE like = buildLikeEntity(likeId, entity, user);
            likeRepository().save(like);
        } catch (RuntimeException e) {
            throw new AddLikeFailedException(e);
        }
    }

    @Override
    @Transactional
    public void deleteLike(ID id, String username) {
        log.info("Eliminando me gusta a la entityId: {}, por el username: {}", id, username);

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );
        E entity = entityRepository().findById(id).orElseThrow(
                () -> entityNotFound(id)
        );

        LIKE_ID likeId = buildLikeId(entity, user);

        if (!likeRepository().existsById(likeId)) {
            throw new LikeNotFoundException(id, username);
        }
        try {
            likeRepository().deleteById(likeId);
        } catch (RuntimeException e) {
            throw new DeleteLikeFailedException(e);
        }

    }
}
