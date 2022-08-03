package com.github.portforesearch.ecommurzbe.service;

import com.github.portforesearch.ecommurzbe.exception.RoleNotFoundException;
import com.github.portforesearch.ecommurzbe.model.Role;
import com.github.portforesearch.ecommurzbe.model.User;
import com.github.portforesearch.ecommurzbe.repo.RoleRepo;
import com.github.portforesearch.ecommurzbe.repo.UserRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collections;

import static com.github.portforesearch.ecommurzbe.constant.RoleConstant.ROLE_CUSTOMER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserRoleImplTest {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    @InjectMocks
    UserRoleServiceImpl userRoleService;
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
        role.setName(ROLE_CUSTOMER);
        user.setRoles(Collections.singletonList(role));
        return user;
    }

    @Test
    void loadUserByUsernameFound() {

        when(userRepo.findByUsernameAndRecordStatusId(anyString(), anyInt())).thenReturn(generateUser());
        UserDetails actual = userRoleService.loadUserByUsername(USERNAME);

        assertEquals(USERNAME, actual.getUsername());
        assertEquals(PASSWORD, actual.getPassword());
        Assertions.assertNotNull(actual.getAuthorities());
    }

    @Test
    void loadUserByUsernameNotFound() {
        when(userRepo.findByUsernameAndRecordStatusId(anyString(), anyInt())).thenReturn(null);
        try {
            userRoleService.loadUserByUsername(USERNAME);
        } catch (UsernameNotFoundException e) {
            assertEquals("User not found in database", e.getMessage());
        }
    }

    @Test
    void addRoleToUser() {
        User user = generateUser();
        Role role = user.getRoles().get(0);
        user.setRoles(new ArrayList<>());

        when(userRepo.findByUsernameAndRecordStatusId(anyString(), anyInt())).thenReturn(user);
        when(roleRepo.findByName(anyString())).thenReturn(role);

        userRoleService.addRoleToUser(USERNAME, role.getName());

        verify(userRepo).findByUsernameAndRecordStatusId(usernameCaptor.capture(),
                recordStatusIdCaptor.capture());
        String usernameCaptorValue = usernameCaptor.getValue();

        verify(roleRepo).findByName(roleNameCaptor.capture());
        String roleNameCaptorValue = roleNameCaptor.getValue();

        assertEquals(USERNAME, usernameCaptorValue);
        assertEquals(ROLE_CUSTOMER, roleNameCaptorValue);
    }

    @Test
    void addRoleToUserRoleNotFound() {
        User user = generateUser();
        Role role = user.getRoles().get(0);
        user.setRoles(new ArrayList<>());

        when(userRepo.findByUsernameAndRecordStatusId(anyString(), anyInt())).thenReturn(user);
        when(roleRepo.findByName(anyString())).thenReturn(null);

        try {
            userRoleService.addRoleToUser(USERNAME, role.getName());
        } catch (RoleNotFoundException e) {
            assertEquals("Role not found", e.getMessage());
        }
    }
}