package com.github.portforesearch.ecommurzbe.service;

import com.github.portforesearch.ecommurzbe.model.User;
import com.github.portforesearch.ecommurzbe.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;


class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this); // Moving this below the next line fixed it...
    }


    @Test
    void loadUserByUsername() {
    }


    @Test
    void saveUser() {
        User user = new User();
        user.setUsername("fascal");
        user.setPassword("fascal123");

        userService.saveUser(user);
    }

    @Test
    void saveRole() {
    }

    @Test
    void addRoleToUser() {
    }

    @Test
    void getUser() {
    }

    @Test
    void getUsers() {
    }
}