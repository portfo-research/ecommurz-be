package com.github.portforesearch.ecommurzbe.module.role.repo;

import com.github.portforesearch.ecommurzbe.module.role.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role, String> {
    Optional<Role> findByName(String roleName);
}
