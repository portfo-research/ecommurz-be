package com.github.portforesearch.ecommurzbe.module.role.service.impl;

import com.github.portforesearch.ecommurzbe.module.role.model.Role;
import com.github.portforesearch.ecommurzbe.module.role.repo.RoleRepo;
import com.github.portforesearch.ecommurzbe.module.role.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepo roleRepo;

    @Override
    public Role save(Role role) {
        return roleRepo.save(role);
    }

}
