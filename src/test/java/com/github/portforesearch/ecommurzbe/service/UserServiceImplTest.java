package com.github.portforesearch.ecommurzbe.service;

import com.github.portforesearch.ecommurzbe.constant.RowStatusConstant;
import com.github.portforesearch.ecommurzbe.model.Role;
import com.github.portforesearch.ecommurzbe.model.User;
import com.github.portforesearch.ecommurzbe.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        Role role = new Role();
        role.setId(UUID.randomUUID().toString());


        user.setRoles(Collections.singletonList(role));
        return user;
    }

    @Test
    void testActionModel() {
        User user = generateUser();
        assertNotNull(user.getRoles().get(0).getId());
    }

    @Test
    void saveUserWithId() {
        String userId = UUID.randomUUID().toString();
        User user = generateUser();
        user.setId(userId);

        when(userRepo.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn(user.getPassword());

        userService.save(user);

        verify(userRepo).save(userCaptor.capture());
        User userCaptorValue = userCaptor.getValue();

        assertNotNull(userCaptorValue.getUpdatedDate());
        assertEquals(userCaptorValue.getPassword(), user.getPassword());
        assertEquals(userCaptorValue.getUsername(), user.getUsername());
        assertEquals(userCaptorValue.getEmail(), user.getEmail());
        assertEquals(userCaptorValue.getId(), user.getId());
        assertEquals(RowStatusConstant.ACTIVE, userCaptorValue.getRecordStatusId());
    }

    @Test
    void saveUserWithoutId() {
        User user = generateUser();

        when(userRepo.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn(user.getPassword());

        userService.save(user);

        verify(userRepo).save(userCaptor.capture());
        User userCaptorValue = userCaptor.getValue();

        assertNotNull(userCaptorValue.getUpdatedDate());
        assertNotNull(userCaptorValue.getCreatedDate());
        assertEquals(userCaptorValue.getPassword(), user.getPassword());
        assertEquals(userCaptorValue.getUsername(), user.getUsername());
        assertEquals(userCaptorValue.getEmail(), user.getEmail());
        assertEquals(RowStatusConstant.ACTIVE, userCaptorValue.getRecordStatusId());
    }

    @Test
    void findByUsername() {
        User user = generateUser();
        when(userRepo.findByUsernameAndRecordStatusId(anyString(), anyInt())).thenReturn(Optional.of(user));
        Optional<User> actual = userService.findByUsername(user.getUsername());
        assertNotNull(actual.get());
    }

    @Test
    void findByEmail() {
        User user = generateUser();
        when(userRepo.findByEmailAndRecordStatusId(anyString(), anyInt())).thenReturn(Optional.of(user));
        Optional<User> actual = userService.findByEmail(user.getEmail());
        assertNotNull(actual.get());
    }
}