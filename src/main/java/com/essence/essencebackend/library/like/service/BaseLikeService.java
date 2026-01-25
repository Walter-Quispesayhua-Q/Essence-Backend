package com.essence.essencebackend.library.like.service;

import com.essence.essencebackend.autentication.shared.model.User;
import com.essence.essencebackend.autentication.shared.repository.UserRepository;
import com.essence.essencebackend.library.playlist.exception.UserNotFoundForUsernameException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

@RequiredArgsConstructor
@Slf4j
public abstract class BaseLikeService<E1, E2, E3, ID1, ID2> {

    private final UserRepository userRepository;

    protected abstract JpaRepository<E1, ID1> getRepository1();

    protected abstract JpaRepository<E2, ID2> getRepository2();

    protected abstract RuntimeException createNotFoundException(ID1 id);

    protected abstract E2 newObject(E1 entityId, User userId);

    @Transactional
    protected boolean addLikeTo(ID1 id, String username) {
        log.info("Agregándole me gusta con el: {} , por el usuario: {}", id, username);

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundForUsernameException(username)
        );

        E1 entity = getRepository1().findById(id).orElseThrow(
                () ->  createNotFoundException(id)
        );

        E2 e2 = newObject(entity, user);
    }
}
