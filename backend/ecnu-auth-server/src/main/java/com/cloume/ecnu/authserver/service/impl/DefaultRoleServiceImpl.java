package com.cloume.ecnu.authserver.service.impl;

import com.cloume.ecnu.authserver.model.Role;
import com.cloume.ecnu.authserver.repository.DefaultRoleRepository;
import com.cloume.ecnu.authserver.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * @author yxiao
 */
@Service
public class DefaultRoleServiceImpl implements IRoleService {

    @Autowired
    private DefaultRoleRepository roleRepository;

    @Override
    public Role create() {
        return new Role();
    }

    @Override
    public JpaRepository<Role, String> getRepository() {
        return roleRepository;
    }

    @Override
    public Role findByName(String roleName) {
        return roleRepository.findByNameAndIsRemoved(roleName, false);
    }
}
