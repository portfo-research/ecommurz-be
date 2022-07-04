package com.github.portforesearch.ecommurzbe.service;

import com.github.portforesearch.ecommurzbe.model.Role;

import java.util.List;

public interface RoleService {
    Role saveRole(Role role);
    List<Role> findAll();
}
