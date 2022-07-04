package com.github.portforesearch.ecommurzbe.service;

import com.github.portforesearch.ecommurzbe.model.User;

import java.util.List;

public interface UserService {
    User save(User user);
    User get(String username);
    List<User> getAll();
}
