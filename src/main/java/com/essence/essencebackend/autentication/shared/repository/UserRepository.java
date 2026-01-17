package com.essence.essencebackend.autentication.shared.repository;

import com.essence.essencebackend.autentication.shared.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    //== register
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    //==

    //== login
    Optional<User> findByEmail(String email);
    //==

}
