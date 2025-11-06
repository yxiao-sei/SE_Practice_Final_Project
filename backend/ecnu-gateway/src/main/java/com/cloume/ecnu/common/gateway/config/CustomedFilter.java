package com.cloume.ecnu.common.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author yxiao
 * @date 2020-05-13
 * @description
 */
@Configuration
public class CustomedFilter {

    @Value("${cors.allowedUrl:http://127.0.0.1:8080}")
    private String allowedUrl;

    class SimpleCORSFilter implements Filter {

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {}

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            HttpServletRequest request = (HttpServletRequest) servletRequest;

            if(request.getMethod().equalsIgnoreCase("options")){
                response.setHeader("Access-Control-Allow-Methods", "PUT, POST, GET, OPTIONS, DELETE");
                response.setHeader("Access-Control-Allow-Headers", "x-requested-with, origin, accept, content-type, Authorization");
            }


            // 设置允许多个域名请求
            String[] allowDomains = {allowedUrl,"http://192.168.1.180:8080","http://192.168.5.24:8080"};
            Set allowOrigins = new HashSet(Arrays.asList(allowDomains));
            // if (Arrays.asList(Constants.ALLOW_DOMAIN).contains(originHeader)) {
            String originHeads = request.getHeader("Origin");
            if(allowOrigins.contains(originHeads)){
                //设置允许跨域的配置
                // 这里填写你允许进行跨域的主机ip（正式上线时可以动态配置具体允许的域名和IP）
                response.setHeader("Access-Control-Allow-Origin", originHeads);
            }
            response.setHeader("Access-Control-Allow-Credentials", "true");
            filterChain.doFilter(servletRequest, servletResponse);
        }

        @Override
        public void destroy() {}
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean () {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();

        filterRegistrationBean.setFilter(new SimpleCORSFilter());
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/*");

        return filterRegistrationBean;
    }
}
