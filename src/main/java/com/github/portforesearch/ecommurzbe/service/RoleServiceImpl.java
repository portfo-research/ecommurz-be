package com.github.portforesearch.ecommurzbe.service;

import com.github.portforesearch.ecommurzbe.model.Role;
import com.github.portforesearch.ecommurzbe.repo.RoleRepo;
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
