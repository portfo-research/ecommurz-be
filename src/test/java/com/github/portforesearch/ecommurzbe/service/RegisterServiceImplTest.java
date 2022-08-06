package com.github.portforesearch.ecommurzbe.service;

import com.github.portforesearch.ecommurzbe.dto.UserRequestDto;
import com.github.portforesearch.ecommurzbe.dto.UserResponseDto;
import com.github.portforesearch.ecommurzbe.exception.DuplicateEmailException;
import com.github.portforesearch.ecommurzbe.exception.DuplicateUserException;
import com.github.portforesearch.ecommurzbe.model.Role;
import com.github.portforesearch.ecommurzbe.model.User;
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
    RoleService roleService;

    @Mock
    UserRoleService authService;

    @InjectMocks
    private RegisterServiceImpl registerService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerSuccess() {
        //GIVEN
        User user = new User();
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setRole(Collections.singletonList(ROLE_CUSTOMER));

        when(userService.findByUsername(anyString())).thenReturn(null);
        when(userService.findByEmail(anyString())).thenReturn(null);
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

    @Test
    void registerThrowDuplicateUserException() {
        //GIVEN
        User user = new User();
        user.setUsername("username");
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setRole(Collections.singletonList(ROLE_CUSTOMER));

        when(userService.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userService.findByEmail(anyString())).thenReturn(null);
        when(userService.save(any(User.class))).thenReturn(user);
        when(roleService.save(any(Role.class))).thenReturn(new Role());

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
    void registerThrowDuplicateEmailException() {
        //GIVEN
        User user = new User();
        user.setEmail("email@email.com");
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setRole(Collections.singletonList(ROLE_CUSTOMER));

        when(userService.findByUsername(anyString())).thenReturn(null);
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userService.save(any(User.class))).thenReturn(user);

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