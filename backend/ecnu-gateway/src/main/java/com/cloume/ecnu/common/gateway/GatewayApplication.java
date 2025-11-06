package com.cloume.ecnu.common.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
//@EnableOAuth2Sso
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

//    @Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http
//    	.authorizeRequests()
//    	//Allow access to all static resources without authentication
//    	.antMatchers("/").permitAll()
//    	.antMatchers(HttpMethod.GET, "/api/**").access("#oauth2.hasScope('read')")
//        .antMatchers(HttpMethod.OPTIONS, "/api/**").access("#oauth2.hasScope('read')")
//        .antMatchers(HttpMethod.POST, "/api/**").access("#oauth2.hasScope('write')")
//        .antMatchers(HttpMethod.PUT, "/api/**").access("#oauth2.hasScope('write')")
//        .antMatchers(HttpMethod.PATCH, "/api/**").access("#oauth2.hasScope('write')")
//        .antMatchers(HttpMethod.DELETE, "/api/**").access("#oauth2.hasScope('write')")
////        .and().csrf().csrfTokenRepository(this.getCSRFTokenRepository())
////        .and().addFilterAfter(this.createCSRFHeaderFilter(), CsrfFilter.class)
//        ;
//	}
}
