package com.cloume.ecnu.authserver.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author yxiao
 * @date 2020-05-21
 * @description 保存AccessToken信息
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "jwt_access_token")
public class JwtAccessToken extends BaseResource {

    /**
     * access token
     */
    @Column(columnDefinition="text")
    private String accessToken;

    /**
     * refresh access token
     */
    @Column(columnDefinition="text")
    private String refreshToken = "";

    /**
     * token type: bearer
     */
    private String tokenType;

    /**
     * 失效时间，秒
     */
    private Long expiresIn;

    /**
     * scope: read, write
     */
    private String scope;

    /**
     * TODO:
     */
    private String jti;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录的客户端id
     */
    private String clientId;

    /**
     * oauth2认证返回的ticket或者code
     */
    private String authenticationKey;

    /**
     * 挑战码
     */
    private String challengeCode;

    /**
     * 通过认证的时间/生成token的时间
     */
    private Long authenticatedTime;
}
