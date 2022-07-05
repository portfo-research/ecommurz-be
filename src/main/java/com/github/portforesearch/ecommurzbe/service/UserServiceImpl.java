package com.github.portforesearch.ecommurzbe.service;


import com.github.portforesearch.ecommurzbe.model.User;
import com.github.portforesearch.ecommurzbe.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Objects;

import static com.github.portforesearch.ecommurzbe.constant.RowStatusConstant.ACTIVE;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Date currentDate = new Date();
        if (Objects.isNull(user.getId())) {
            user.setCreatedDate(currentDate);
        }
        user.setRecordStatusId(ACTIVE);
        user.setUpdatedDate(currentDate);
        return userRepo.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepo.findByUsernameAndRecordStatusId(username, ACTIVE);
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmailAndRecordStatusId(email, ACTIVE);
    }
}
