package com.github.portforesearch.ecommurzbe.service;

import com.github.portforesearch.ecommurzbe.dto.UserRequestDto;
import com.github.portforesearch.ecommurzbe.dto.UserResponseDto;
import com.github.portforesearch.ecommurzbe.exception.DuplicateEmailException;
import com.github.portforesearch.ecommurzbe.exception.DuplicateUserException;
import com.github.portforesearch.ecommurzbe.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class RegisterServiceImpl implements RegisterService {

    private final UserRoleService authService;
    private final UserService userService;

    @Override
    @Transactional
    public UserResponseDto register(UserRequestDto userRequestDto, User user) {
        if (userService.findByUsername(user.getUsername()) != null) {
            throw new DuplicateUserException(String.format("User with username %s already exist", user.getUsername()));
        }
        if (userService.findByEmail(user.getEmail()) != null) {
            throw new DuplicateEmailException(String.format("User with email %s already exist", user.getEmail()));
        }

        userService.save(user);

        userRequestDto.getRole().forEach(roleName -> authService.addRoleToUser(userRequestDto.getUsername(), roleName));

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername(user.getUsername());
        userResponseDto.setMessage(String.format("User with username %s has been created with role %s",
                user.getUsername(), userRequestDto.getRole().toString()));
        return userResponseDto;
    }
}
