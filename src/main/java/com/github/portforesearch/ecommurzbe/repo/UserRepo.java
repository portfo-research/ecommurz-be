package com.github.portforesearch.ecommurzbe.repo;

import com.github.portforesearch.ecommurzbe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, String> {
    User findByUsernameAndRecordStatusId(String username, Integer recordStatusId);
    User findByEmailAndRecordStatusId(String email, Integer recordStatusId);
}
