package com.cloume.ecnu.authserver.security;

import com.cloume.ecnu.authserver.model.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsUtils;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //enable option requests.
        http.authorizeRequests().requestMatchers(CorsUtils::isPreFlightRequest).permitAll();

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/oauth/result/wechat/**", "/oauth/result/cas/**",
                        "/oauth/challenge/**", "/token/**", "/oauth/login/**", "/oauth/logout/**", "/logout/**", "/user/**", "/login/**").permitAll()
                .anyRequest().authenticated();

        //不创建session，使用jwt来管理用户的登录状态
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
        http.logout().logoutUrl("/logout").addLogoutHandler(logoutHandler());
//        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

//        http.addFilterAt(wxAppletAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//        http.(wxAppletAuthenticationFilter());
    }

    @Bean
    public LogoutHandler logoutHandler() {
        return new MyLogoutHandler();
    }

//    @Autowired
//    private CorpWxAuthenticationManager wxAppletAuthenticationManager;

//    @Bean
//    public FilterRegistrationBean AuthenticationFilterBean() {
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
//
//        filterRegistrationBean.setFilter(wxAppletAuthenticationFilter());
//        filterRegistrationBean.setEnabled(true);
//        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        filterRegistrationBean.addUrlPatterns("/*");
//
//        return filterRegistrationBean;
//    }
//
//    @Bean
//    public CorpWxAuthenticationFilter wxAppletAuthenticationFilter(){
//        CorpWxAuthenticationFilter wxAppletAuthenticationFilter = new CorpWxAuthenticationFilter("/login");
//        wxAppletAuthenticationFilter.setAuthenticationManager(wxAppletAuthenticationManager);
//        wxAppletAuthenticationFilter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler());
//
//        return wxAppletAuthenticationFilter;
//    }

    @Bean
    public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler(){
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public UserDetailsService customUserDetailsService() {
        return new MyUserDetailsServiceImpl();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(customUserDetailsService()).passwordEncoder(CustomUserDetails.PASSWORD_ENCODER);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new PasswordEncoder() {
//            @Override
//            public String encode(CharSequence charSequence) {
//                return charSequence.toString();
//            }
//
//            @Override
//            public boolean matches(CharSequence charSequence, String s) {
//                return Objects.equals(charSequence.toString(),s);
//            }
//        };
//    }
}
