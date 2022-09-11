package com.github.portforesearch.ecommurzbe.module.user.service;

import com.github.portforesearch.ecommurzbe.module.auth.service.impl.RegisterServiceImpl;
import com.github.portforesearch.ecommurzbe.module.user.dto.UserRequestDto;
import com.github.portforesearch.ecommurzbe.module.user.dto.UserResponseDto;
import com.github.portforesearch.ecommurzbe.module.auth.exception.DuplicateEmailException;
import com.github.portforesearch.ecommurzbe.module.auth.exception.DuplicateUserException;
import com.github.portforesearch.ecommurzbe.module.user.model.User;
import com.github.portforesearch.ecommurzbe.module.userrole.service.UserRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static com.github.portforesearch.ecommurzbe.constant.RoleConstant.ROLE_CUSTOMER;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


class RegisterServiceImplTest {

    @Mock
    UserService userService;

    @Mock
    UserRoleService authService;

    @InjectMocks
    private RegisterServiceImpl registerService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_thenReturnSuccess() {
        //GIVEN
        User user = getUser();
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setRole(Collections.singletonList(ROLE_CUSTOMER));

        when(userService.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userService.save(any(User.class))).thenReturn(user);

        doNothing().when(authService).addRoleToUser(anyString(), anyString());

        String message = format("User with username %s has been created with role %s",
                user.getUsername(), userRequestDto.getRole().toString());

        //WHEN
        UserResponseDto userResponseDto = registerService.register(userRequestDto, user);

        //THEN
        assertEquals(user.getUsername(), userResponseDto.getUsername());
        assertEquals(message, userResponseDto.getMessage());

    }

    private User getUser() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("email");
        return user;
    }

    @Test
    void register_whenDuplicateUser_thenThrowDuplicateUserException() {
        //GIVEN
        User user = getUser();
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setRole(Collections.singletonList(ROLE_CUSTOMER));

        //Set exist user when findByUsername then throw DuplicateUserException
        when(userService.findByUsername(anyString())).thenReturn(Optional.of(user));

        doNothing().when(authService).addRoleToUser(anyString(), anyString());

        String message = format("User with username %s already exist",
                user.getUsername(), userRequestDto.getRole().toString());
        //WHEN
        DuplicateUserException duplicateUserException = assertThrows(DuplicateUserException.class,
                () -> registerService.register(userRequestDto, user));
        //THEN
        assertEquals(message, duplicateUserException.getMessage());
    }

    @Test
    void register_whenDuplicateEmail_thenThrowDuplicateEmailException() {
        //GIVEN
        User user = getUser();
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setRole(Collections.singletonList(ROLE_CUSTOMER));


        when(userService.findByUsername(anyString())).thenReturn(Optional.empty());
        //Set exist user when findByEmail then throw DuplicateEmailException
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));

        doNothing().when(authService).addRoleToUser(anyString(), anyString());

        String message = format("User with email %s already exist",
                user.getEmail(), userRequestDto.getRole().toString());

        //WHEN
        DuplicateEmailException duplicateUserException = assertThrows(DuplicateEmailException.class,
                () -> registerService.register(userRequestDto, user));

        //THEN
        assertEquals(message, duplicateUserException.getMessage());
    }
}