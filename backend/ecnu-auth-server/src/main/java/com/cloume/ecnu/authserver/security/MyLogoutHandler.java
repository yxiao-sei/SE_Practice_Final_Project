package com.cloume.ecnu.authserver.security;

import com.cloume.ecnu.authserver.model.JwtAccessToken;
import com.cloume.ecnu.authserver.repository.JwtAccessTokenRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yxiao
 */
@Log4j2
@Configuration
public class MyLogoutHandler implements LogoutHandler {

    @Value("${client.login-url:http://127.0.0.1:8080}")
    private String loginUrl;

    @Autowired
    private JwtAccessTokenRepository jwtAccessTokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            if (request.getSession().getAttribute("sessionId") != null) {
                String code = request.getSession().getAttribute("sessionId").toString();
                log.info("logout code: {}, user: {}", code, authentication != null ? authentication.getName() : "invalid user");
                request.getSession().removeAttribute("sessionId");
                JwtAccessToken jwtAccessToken = jwtAccessTokenRepository.findTopByChallengeCodeAndIsRemoved(code, false);
                if (jwtAccessToken != null) {
                    //已使用的Token不再持久化存储
                    jwtAccessTokenRepository.delete(jwtAccessToken);
                }
            }
            new SecurityContextLogoutHandler().logout(request, null, null);
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            log.error("logout error: {}", e);
        }
        try {
            response.sendRedirect(loginUrl);
        } catch (IOException e) {
            log.error("redirect error: {}", e);
        }
    }

}
