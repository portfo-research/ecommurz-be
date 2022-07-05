package com.github.portforesearch.ecommurzbe.service;

import com.github.portforesearch.ecommurzbe.model.Role;
import com.github.portforesearch.ecommurzbe.model.User;
import com.github.portforesearch.ecommurzbe.repo.UserRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.UUID;

class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;
    @Mock
    UserRepo userRepo;
    @Mock
    PasswordEncoder passwordEncoder;
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private User generateUser() {
        User user = new User();
        String password = "password";
        user.setUsername("username");
        user.setEmail("email@email.com");
        user.setPassword(password);

        user.setRoles(Collections.singletonList(new Role()));
        return user;
    }

    @Test
    void saveUserWithId() {
        String userId = UUID.randomUUID().toString();
        User user = generateUser();
        user.setId(userId);

        Mockito.when(userRepo.save(Mockito.any(User.class))).thenReturn(user);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn(user.getPassword());

        userService.save(user);

        Mockito.verify(userRepo).save(userCaptor.capture());
        User userCaptorValue = userCaptor.getValue();

        Assertions.assertNotNull(userCaptorValue.getUpdatedDate());
        Assertions.assertNotNull(userCaptorValue.getPassword(), user.getPassword());
        Assertions.assertNotNull(userCaptorValue.getUsername(), user.getUsername());
        Assertions.assertNotNull(userCaptorValue.getEmail(), user.getEmail());
        Assertions.assertNotNull(userCaptorValue.getId(), user.getId());
    }

    @Test
    void saveUserWithoutId() {
        User user = generateUser();

        Mockito.when(userRepo.save(Mockito.any(User.class))).thenReturn(user);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn(user.getPassword());

        userService.save(user);

        Mockito.verify(userRepo).save(userCaptor.capture());
        User userCaptorValue = userCaptor.getValue();

        Assertions.assertNotNull(userCaptorValue.getUpdatedDate());
        Assertions.assertNotNull(userCaptorValue.getCreatedDate());
        Assertions.assertNotNull(userCaptorValue.getPassword(), user.getPassword());
        Assertions.assertNotNull(userCaptorValue.getUsername(), user.getUsername());
        Assertions.assertNotNull(userCaptorValue.getEmail(), user.getEmail());
    }

    @Test
    void findByUsername() {
        User user = generateUser();
        Mockito.when(userRepo.findByUsernameAndRecordStatusId(Mockito.anyString(), Mockito.anyInt())).thenReturn(user);
        User actual = userService.findByUsername(user.getUsername());
        Assertions.assertNotNull(actual);
    }

    @Test
    void findByEmail() {
        User user = generateUser();
        Mockito.when(userRepo.findByEmailAndRecordStatusId(Mockito.anyString(), Mockito.anyInt())).thenReturn(user);
        User actual = userService.findByEmail(user.getEmail());
        Assertions.assertNotNull(actual);
    }
}