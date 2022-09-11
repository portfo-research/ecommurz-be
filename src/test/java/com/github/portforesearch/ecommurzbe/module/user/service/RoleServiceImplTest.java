package com.github.portforesearch.ecommurzbe.module.user.service;

import com.github.portforesearch.ecommurzbe.module.role.model.Role;
import com.github.portforesearch.ecommurzbe.module.role.repo.RoleRepo;
import com.github.portforesearch.ecommurzbe.module.role.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static com.github.portforesearch.ecommurzbe.constant.RoleConstant.ROLE_CUSTOMER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoleServiceImplTest {
    @Mock
    RoleRepo roleRepo;
    ArgumentCaptor<Role> roleArgumentCaptor = ArgumentCaptor.forClass(Role.class);
    @InjectMocks
    private RoleServiceImpl roleService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveSuccess() {
        //GIVEN
        Role role = new Role();
        role.setId(UUID.randomUUID().toString());
        role.setName(ROLE_CUSTOMER);

        when(roleRepo.save(any(Role.class))).thenReturn(role);

        //WHEN
        roleService.save(role);

        //THEN
        verify(roleRepo).save(roleArgumentCaptor.capture());
        Role roleArgumentCaptorValue = roleArgumentCaptor.getValue();

        assertEquals(ROLE_CUSTOMER, roleArgumentCaptorValue.getName());
    }

}