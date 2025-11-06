package com.cloume.ecnu.authserver.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

@Configuration
public class MyPermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        boolean accessable = false;
        String ANONYMOUS_USER = "anonymousUser";
        if(authentication.getPrincipal().toString().compareToIgnoreCase(ANONYMOUS_USER) != 0){
            String privilege = targetDomainObject + "-" + permission;

            for(GrantedAuthority authority : authentication.getAuthorities()){
                if(privilege.equalsIgnoreCase(authority.getAuthority())){
                        accessable = true;
                        break;
                }
            }

            return accessable;
        }

        return accessable;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }
}
