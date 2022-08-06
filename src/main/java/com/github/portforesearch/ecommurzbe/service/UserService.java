package com.github.portforesearch.ecommurzbe.service;

import com.github.portforesearch.ecommurzbe.model.User;

import java.util.Optional;

public interface UserService {
    User save(User user);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
