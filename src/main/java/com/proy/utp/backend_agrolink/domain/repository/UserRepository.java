package com.proy.utp.backend_agrolink.domain.repository;

import com.proy.utp.backend_agrolink.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findAll();
}
