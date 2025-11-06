package com.cloume.ecnu.authserver.repository;

import com.cloume.ecnu.authserver.model.SimplePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author yxiao
 */
public interface DefaultSimplePermissionRepository extends JpaSpecificationExecutor<SimplePermission>, JpaRepository<SimplePermission, String> {
}
