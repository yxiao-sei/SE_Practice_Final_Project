package com.cloume.ecnu.authserver.service.impl;

import com.cloume.ecnu.authserver.model.JwtAccessToken;
import com.cloume.ecnu.authserver.repository.JwtAccessTokenRepository;
import com.cloume.ecnu.authserver.service.IJwtAccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * @author yxiao
 * @date 2020-05-21
 * @description
 */
@Service
public class JwtAccessTokenServiceImpl implements IJwtAccessTokenService {

    @Autowired
    private JwtAccessTokenRepository jwtAccessTokenRepository;

    @Override
    public JwtAccessToken create() {
        return new JwtAccessToken();
    }

    @Override
    public JpaRepository<JwtAccessToken, String> getRepository() {
        return jwtAccessTokenRepository;
    }

    @Override
    public JwtAccessToken findByChallengeCode(String challengeCode) {
        return jwtAccessTokenRepository.findTopByChallengeCodeAndIsRemoved(challengeCode, false);
    }

    @Override
    public void deleteToken(String challengeCode) {
        jwtAccessTokenRepository.deleteByChallengeCode(challengeCode);
    }
}
