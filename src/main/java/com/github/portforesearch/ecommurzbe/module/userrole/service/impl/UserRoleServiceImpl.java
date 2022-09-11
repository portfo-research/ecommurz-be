package com.github.portforesearch.ecommurzbe.module.userrole.service.impl;


import com.github.portforesearch.ecommurzbe.module.role.exception.RoleNotFoundException;
import com.github.portforesearch.ecommurzbe.module.role.model.Role;
import com.github.portforesearch.ecommurzbe.module.user.model.User;
import com.github.portforesearch.ecommurzbe.module.role.repo.RoleRepo;
import com.github.portforesearch.ecommurzbe.module.user.repo.UserRepo;
import com.github.portforesearch.ecommurzbe.module.userrole.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;

import static com.github.portforesearch.ecommurzbe.constant.RowStatusConstant.ACTIVE;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserRoleServiceImpl implements UserRoleService, UserDetailsService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =
                userRepo.findByUsernameAndRecordStatusId(username, ACTIVE)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found in database"));

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                authorities);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        User user =
                userRepo.findByUsernameAndRecordStatusId(username, ACTIVE)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found in database"));

        Role role = roleRepo.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        user.getRoles().add(role);
    }
}
