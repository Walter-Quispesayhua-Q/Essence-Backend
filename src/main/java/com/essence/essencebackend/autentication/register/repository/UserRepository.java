package com.essence.essencebackend.autentication.register.repository;

import com.essence.essencebackend.autentication.register.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
