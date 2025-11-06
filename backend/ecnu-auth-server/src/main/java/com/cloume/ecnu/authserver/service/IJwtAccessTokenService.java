package com.cloume.ecnu.authserver.service;

import com.cloume.ecnu.authserver.model.JwtAccessToken;

/**
 * @author yxiao
 * @date 2020-05-21
 * @description
 */
public interface IJwtAccessTokenService extends IBaseService<JwtAccessToken> {

    JwtAccessToken findByChallengeCode(String challengeCode);

    void deleteToken(String challengeCode);
}
