package com.cloume.ecnu.authserver.repository;

import com.cloume.ecnu.authserver.model.JwtAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author yxiao
 * @date 2020-05-21
 * @description
 */
public interface JwtAccessTokenRepository extends JpaSpecificationExecutor<JwtAccessToken>, JpaRepository<JwtAccessToken, String> {

    JwtAccessToken findTopByChallengeCodeAndIsRemoved(String challengeCode, Boolean isRemoved);

    void deleteByChallengeCode(String challengeCode);
}
