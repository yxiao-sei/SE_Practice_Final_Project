package com.cloume.ecnu.sei.app.security;

import com.cloume.ecnu.sei.app.model.SimplePermission;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yxiao
 */
@Configuration
@Log4j2
public class JwtResourceServerTokenServices implements ResourceServerTokenServices {

    @Autowired
    private MyJwtAccessTokenConverter jwtTokenEnhancer;

    //jwtToken中有额外的用户信息，通过设置auth的details将用户信息解析出来给securitycontext使用
    @Override
    public OAuth2Authentication loadAuthentication(String accessToken)
            throws AuthenticationException, InvalidTokenException {
        Map<String, Object> map = jwtTokenEnhancer.decode(accessToken);

        List<String> roles = (List<String>) map.get("roles");

        List<String> authorities = getAuthorities(accessToken, String.join(",", roles));
        List<SimpleGrantedAuthority> grantedAuthorityList = new ArrayList<>();
        authorities.stream().forEach(t -> {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(t);
            grantedAuthorityList.add(simpleGrantedAuthority);
        });
        map.replace("authorities", grantedAuthorityList);

        if (map.getOrDefault("entityDepartmentCode", "") == null || StringUtils.isEmpty(map.getOrDefault("entityDepartmentCode", "").toString())) {
            try {
//                DepartmentMember departmentMember = departmentMemberService.findTopByUserName(map.get("user_name").toString());
//                if (departmentMember != null) {
//                    map.replace("entityDepartmentCode", departmentMember.getEntityDepartmentCode());
//                    map.replace("entityDepartmentName", departmentMember.getEntityDepartmentName());
//                    map.replace("departmentType", departmentMember.getDepartmentType());
//                }
            } catch (Exception e) {
                log.error("updateStateAndScope, set host error: {}", e);
            }
        }
        OAuth2Authentication auth = jwtTokenEnhancer.extractAuthentication(map);
        auth.setDetails(map);

        return auth;
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessTokenValue) {
        Map<String, Object> map = jwtTokenEnhancer.decode(accessTokenValue);
        map.put("tokenValue", accessTokenValue);
        return jwtTokenEnhancer.extractAccessToken(accessTokenValue, map);
    }

    @Autowired JdbcTemplate sqlJdbcTemplate;

    private static Map<String, List<String>> rolePermissionMaps = new HashMap<>();

    private List<String> getAuthorities(String token, String roleNames) {
        if (rolePermissionMaps.containsKey(roleNames)) {
            return rolePermissionMaps.get(roleNames);
        }
        String roles = roleNames.replace(",", "','");
        List<String> authorities = new ArrayList<>();
        List<String> authorityString = new ArrayList<>();
        String sql = "SELECT * FROM `role_permission_view` where is_removed = false and name in ('" + roles + "')";
        List<SimplePermission> permissions = sqlJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SimplePermission.class));
        if (permissions != null && !permissions.isEmpty()) {
            permissions.stream().forEach(permission -> {
                if (!authorityString.contains(String.format("%s-%s", permission.getResourceId(), permission.getPrivilege()))) {
                    authorities.add(String.format("%s-%s", permission.getResourceId(), permission.getPrivilege()));
                }
            });
        }
        synchronized (this) {
            rolePermissionMaps.put(roleNames, authorities);
        }
        return authorities;
    }
}	
