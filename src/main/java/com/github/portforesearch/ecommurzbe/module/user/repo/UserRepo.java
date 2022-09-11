package com.github.portforesearch.ecommurzbe.module.user.repo;

import com.github.portforesearch.ecommurzbe.module.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, String> {
    Optional<User> findByUsernameAndRecordStatusId(String username, Integer recordStatusId);
    Optional<User> findByEmailAndRecordStatusId(String email, Integer recordStatusId);
}
