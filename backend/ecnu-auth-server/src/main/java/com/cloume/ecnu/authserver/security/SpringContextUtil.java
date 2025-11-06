package com.cloume.ecnu.authserver.security;

import com.cloume.ecnu.authserver.model.CustomUserDetails;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 * 获取spring容器，以访问容器中定义的其他bean
 * @author xiaoyu
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    /**
     * Spring应用上下文环境
     */
    private static ApplicationContext applicationContext;

    /**
     * 实现ApplicationContextAware接口的回调方法，设置上下文环境
     *
     * @param applicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取对象
     * 这里重写了bean方法，起主要作用
     * @param name
     * @return Object 一个以所给名字注册的bean的实例
     * @throws BeansException
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    /**
     * 手动实现登录，用于结合微信小程序登录和Spring Security登录
     * @param user
     * @param password
     */
    public static Authentication injectPrincipalToSecurityContext(CustomUserDetails user, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), password);
        token.setDetails(user);
        final Map<String, UserDetailsService> userDetailsServices = SpringContextUtil.getApplicationContext().getBeansOfType(UserDetailsService.class);
        UserDetailsService userDetailsService = (UserDetailsService) userDetailsServices.values().toArray()[0];

        DaoAuthenticationProvider authenticator = new DaoAuthenticationProvider();
        authenticator.setUserDetailsService(userDetailsService);
        authenticator.setPasswordEncoder(CustomUserDetails.PASSWORD_ENCODER);
        Authentication authentication = authenticator.authenticate(token);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        return authentication;
    }
}
