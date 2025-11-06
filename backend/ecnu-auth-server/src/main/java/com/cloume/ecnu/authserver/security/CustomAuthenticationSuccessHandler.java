package com.cloume.ecnu.authserver.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户认证通过的处理handler
 */
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//
//    @Autowired
//    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private JwtTokenStore tokenStore;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        // 使用jwt管理，所以封装用户信息生成jwt响应给前端
//        String token = jwtTokenUtils.generateToken(((WxAppletAuthenticationToken)authentication).getOpenid());
//        Map<String, Object> result = Maps.newHashMap();
//        result.put(ConstantEnum.AUTHORIZATION.getValue(), token);
//        httpServletResponse.setContentType(ContentType.JSON.toString());
//        httpServletResponse.getWriter().write(JSON.toJSONString(result));
        OAuth2Authentication auth2AccessToken = (OAuth2Authentication) authentication;
        OAuth2AccessToken accessToken = tokenStore.getAccessToken(auth2AccessToken);
    }
}
