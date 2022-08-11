package com.github.portforesearch.ecommurzbe.service;

import com.github.portforesearch.ecommurzbe.model.Role;

/**
 * Role Service
 */
public interface RoleService {
    /**
     * Add new role
     * @param role
     * @return
     */
    Role save(Role role);
}
