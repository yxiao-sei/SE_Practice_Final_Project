package com.cloume.ecnu.authserver.repository;

import com.cloume.ecnu.authserver.model.CustomUserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author yxiao
 */
public interface DefaultCustomUserDetailsRepository extends JpaSpecificationExecutor<CustomUserDetails>, JpaRepository<CustomUserDetails, String> {

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

    List<CustomUserDetails> findTop10ByRealNameContainingAndIsRemovedFalse(String realName);

    List<CustomUserDetails> findByRealNameContainingAndIsRemovedFalse(String realName, Pageable pageable);
}
