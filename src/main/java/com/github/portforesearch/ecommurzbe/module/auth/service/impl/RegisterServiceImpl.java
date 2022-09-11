package com.github.portforesearch.ecommurzbe.module.auth.service.impl;

import com.github.portforesearch.ecommurzbe.module.auth.service.RegisterService;
import com.github.portforesearch.ecommurzbe.module.user.dto.UserRequestDto;
import com.github.portforesearch.ecommurzbe.module.user.dto.UserResponseDto;
import com.github.portforesearch.ecommurzbe.module.auth.exception.DuplicateEmailException;
import com.github.portforesearch.ecommurzbe.module.auth.exception.DuplicateUserException;
import com.github.portforesearch.ecommurzbe.module.user.model.User;
import com.github.portforesearch.ecommurzbe.module.userrole.service.UserRoleService;
import com.github.portforesearch.ecommurzbe.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class RegisterServiceImpl implements RegisterService {

    private final UserRoleService authService;
    private final UserService userService;

    @Override
    @Transactional
    public UserResponseDto register(UserRequestDto userRequestDto, User user) {

        String username = Objects.requireNonNull(user.getUsername());
        String email = Objects.requireNonNull(user.getEmail());

        userService.findByUsername(user.getUsername()).ifPresent(usr -> {
            throw new DuplicateUserException(String.format("User with username %s already exist", username));
        });

        userService.findByEmail(user.getEmail()).ifPresent(eml -> {
            throw new DuplicateEmailException(String.format("User with email %s already exist", email));
        });

        userService.save(user);

        userRequestDto.getRole().forEach(roleName -> authService.addRoleToUser(userRequestDto.getUsername(), roleName));

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername(user.getUsername());
        userResponseDto.setMessage(String.format("User with username %s has been created with role %s",
                user.getUsername(), userRequestDto.getRole().toString()));
        return userResponseDto;
    }
}
