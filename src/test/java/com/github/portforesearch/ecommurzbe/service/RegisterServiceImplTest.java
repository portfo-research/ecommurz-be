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

import static com.github.portforesearch.ecommurzbe.constant.RoleConstant.ROLE_CUSTOMER;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerSuccess() {
        User user = new User();
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setRole(Collections.singletonList(ROLE_CUSTOMER));

        when(userService.findByUsername(anyString())).thenReturn(null);
        when(userService.findByEmail(anyString())).thenReturn(null);
        when(userService.save(any(User.class))).thenReturn(user);

        doNothing().when(authService).addRoleToUser(anyString(), anyString());

        String message = format("User with username %s has been created with role %s",
                user.getUsername(), userRequestDto.getRole().toString());

        UserResponseDto userResponseDto = registerService.register(userRequestDto, user);


        assertEquals(user.getUsername(), userResponseDto.getUsername());
        assertEquals(message, userResponseDto.getMessage());

    }

    @Test
    void registerThrowDuplicateUserException() {
        User user = new User();
        user.setUsername("username");
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setRole(Collections.singletonList(ROLE_CUSTOMER));

        when(userService.findByUsername(anyString())).thenReturn(user);
        when(userService.findByEmail(anyString())).thenReturn(null);
        when(userService.save(any(User.class))).thenReturn(user);
        when(roleService.save(any(Role.class))).thenReturn(new Role());

        doNothing().when(authService).addRoleToUser(anyString(), anyString());

        String message = format("User with username %s already exist",
                user.getUsername(), userRequestDto.getRole().toString());

        try {
            registerService.register(userRequestDto, user);
        } catch (DuplicateUserException e) {
            assertEquals(message, e.getMessage());
        }
    }

    @Test
    void registerThrowDuplicateEmailException() {
        User user = new User();
        user.setEmail("email@email.com");
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setRole(Collections.singletonList(ROLE_CUSTOMER));

        when(userService.findByUsername(anyString())).thenReturn(null);
        when(userService.findByEmail(anyString())).thenReturn(user);
        when(userService.save(any(User.class))).thenReturn(user);

        doNothing().when(authService).addRoleToUser(anyString(), anyString());

        String message = format("User with email %s already exist",
                user.getEmail(), userRequestDto.getRole().toString());

        try {
            registerService.register(userRequestDto, user);
        } catch (DuplicateEmailException e) {
            assertEquals(message, e.getMessage());
        }
    }
}