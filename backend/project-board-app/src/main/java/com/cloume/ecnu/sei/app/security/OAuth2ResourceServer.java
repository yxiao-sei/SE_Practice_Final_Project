package com.cloume.ecnu.sei.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class OAuth2ResourceServer extends ResourceServerConfigurerAdapter {

    @Autowired
    private JwtResourceServerTokenServices jwtTokenServices;

    @Autowired
    private MyJwtAccessTokenConverter myJwtAccessTokenConverter;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/audit-record/**", "/swagger**/**", "/webjars/**", "/v3/**", "/doc.html",  "/v2/**", "/api/**").permitAll()
                .anyRequest().authenticated();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("basic").tokenStore(tokenStore()).tokenServices(jwtTokenServices);
//        if(!isStateless) resources.stateless(false);	///only for test
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(myJwtAccessTokenConverter);
    }

}
