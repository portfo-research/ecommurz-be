package com.github.portforesearch.ecommurzbe.repo;

import com.github.portforesearch.ecommurzbe.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends JpaRepository<Role, String> {
    Role findByName(String roleName);
}
