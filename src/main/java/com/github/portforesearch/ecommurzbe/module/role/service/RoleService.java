package com.github.portforesearch.ecommurzbe.module.role.service;

import com.github.portforesearch.ecommurzbe.module.role.model.Role;

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
