package com.github.portforesearch.ecommurzbe.service;

import com.github.portforesearch.ecommurzbe.model.Role;
import com.github.portforesearch.ecommurzbe.model.User;
import com.github.portforesearch.ecommurzbe.repo.RoleRepo;
import com.github.portforesearch.ecommurzbe.repo.UserRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collections;

import static com.github.portforesearch.ecommurzbe.constant.RoleConstant.MANAGER;

class AuthServiceImplTest {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    @InjectMocks
    AuthServiceImpl authService;
    @Mock
    UserRepo userRepo;
    @Mock
    RoleRepo roleRepo;
    ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> roleNameCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Integer> recordStatusIdCaptor = ArgumentCaptor.forClass(Integer.class);

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private User generateUser() {
        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        Role role = new Role();
        role.setName(MANAGER);
        user.setRoles(Collections.singletonList(role));
        return user;
    }

    @Test
    void loadUserByUsernameFound() {

        Mockito.when(userRepo.findByUsernameAndRecordStatusId(Mockito.anyString(), Mockito.anyInt())).thenReturn(generateUser());
        UserDetails actual = authService.loadUserByUsername(USERNAME);

        Assertions.assertEquals(USERNAME, actual.getUsername());
        Assertions.assertEquals(PASSWORD, actual.getPassword());
        Assertions.assertNotNull(actual.getAuthorities());
    }

    @Test
    void loadUserByUsernameNotFound() {
        Mockito.when(userRepo.findByUsernameAndRecordStatusId(Mockito.anyString(), Mockito.anyInt())).thenReturn(null);
        try {
            authService.loadUserByUsername(USERNAME);

        } catch (UsernameNotFoundException e) {
            Assertions.assertEquals("User not found in database", e.getMessage());
        }
    }

    @Test
    void addRoleToUser() {
        User user = generateUser();
        Role role = user.getRoles().get(0);
        user.setRoles(new ArrayList<>());

        Mockito.when(userRepo.findByUsernameAndRecordStatusId(Mockito.anyString(), Mockito.anyInt())).thenReturn(user);
        Mockito.when(roleRepo.findByNameAndRecordStatusId(Mockito.anyString(), Mockito.anyInt())).thenReturn(role);

        authService.addRoleToUser(USERNAME, role.getName());

        Mockito.verify(userRepo).findByUsernameAndRecordStatusId(usernameCaptor.capture(),
                recordStatusIdCaptor.capture());
        String usernameCaptorValue = usernameCaptor.getValue();

        Mockito.verify(roleRepo).findByNameAndRecordStatusId(roleNameCaptor.capture(), recordStatusIdCaptor.capture());
        String roleNameCaptorValue = roleNameCaptor.getValue();

        Assertions.assertEquals(USERNAME, usernameCaptorValue);
        Assertions.assertEquals(MANAGER, roleNameCaptorValue);
    }
}