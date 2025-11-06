package com.cloume.ecnu.authserver.service;

import com.cloume.ecnu.authserver.model.Role;

/**
 * @author yxiao
 * @date 2020-06-26
 */
public interface IRoleService extends IBaseService<Role> {
    Role findByName(String roleName);
}
