package com.github.portforesearch.ecommurzbe.service;


import com.github.portforesearch.ecommurzbe.constant.RowStatusConstant;
import com.github.portforesearch.ecommurzbe.model.Role;
import com.github.portforesearch.ecommurzbe.repo.RoleRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepo roleRepo;

    @Override
    public Role saveRole(Role role) {
        Date currentDate = new Date();
        if (Objects.isNull(role.getId())) {
            role.setCreatedDate(currentDate);
        }
        role.setRecordStatusId(RowStatusConstant.ACTIVE);
        role.setUpdatedDate(currentDate);
        return roleRepo.save(role);
    }

    @Override
    public List<Role> findAll() {
        return roleRepo.findAll();
    }


}
