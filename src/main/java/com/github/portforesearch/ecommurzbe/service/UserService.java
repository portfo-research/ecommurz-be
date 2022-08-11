package com.github.portforesearch.ecommurzbe.service;

import com.github.portforesearch.ecommurzbe.model.User;

import java.util.Optional;

/**
 * User Service
 */
public interface UserService {
    /**
     * Create new user
     * @param user
     * @return
     */
    User save(User user);

    /**
     * Find user by username
     * @param username
     * @return
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     * @param email
     * @return
     */
    Optional<User> findByEmail(String email);
}
