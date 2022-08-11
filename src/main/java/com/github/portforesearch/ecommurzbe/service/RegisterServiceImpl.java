package com.github.portforesearch.ecommurzbe.service;

import com.github.portforesearch.ecommurzbe.dto.UserRequestDto;
import com.github.portforesearch.ecommurzbe.dto.UserResponseDto;
import com.github.portforesearch.ecommurzbe.exception.DuplicateEmailException;
import com.github.portforesearch.ecommurzbe.exception.DuplicateUserException;
import com.github.portforesearch.ecommurzbe.model.User;
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
