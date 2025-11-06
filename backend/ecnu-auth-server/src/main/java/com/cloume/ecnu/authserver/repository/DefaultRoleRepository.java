package com.cloume.ecnu.authserver.repository;

import com.cloume.ecnu.authserver.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author yxiao
 */
public interface DefaultRoleRepository extends JpaSpecificationExecutor<Role>, JpaRepository<Role, String> {

    /** 根据角色名查找角色
     * @param name
     * @param isRemoved
     * @return
     */
    Role findByNameAndIsRemoved(String name, boolean isRemoved);
}
