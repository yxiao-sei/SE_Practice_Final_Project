package com.cloume.ecnu.sei.app.repository;

import com.cloume.ecnu.sei.app.model.CustomUserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author yxiao
 */
public interface DefaultCustomUserDetailsRepository extends JpaSpecificationExecutor<CustomUserDetails>, JpaRepository<CustomUserDetails, Integer> {

    /**
     * 根据用户名查找用户
     *
     * @param username
     * @return
     */
    CustomUserDetails findTopByUsernameAndIsRemovedFalse(String username);

    /**
     * 根据用户名查找用户
     *
     * @param username
     * @param isRemoved
     * @return
     */
    CustomUserDetails findFirstByUsernameAndIsRemoved(String username, boolean isRemoved);

    List<CustomUserDetails> findAllByUserTypeAndIsRemovedFalse(String useType);

    List<CustomUserDetails> findAllByRolesLikeAndIsRemoved(String role, boolean isRemoved);

    List<CustomUserDetails> findAllByIsRemovedFalse();
}
