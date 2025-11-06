package com.cloume.ecnu.authserver.security;

import com.cloume.ecnu.authserver.model.CustomUserDetails;
import com.cloume.ecnu.authserver.model.Role;
import com.cloume.ecnu.authserver.model.SimplePermission;
import com.cloume.ecnu.authserver.service.ICustomUserDetailsService;
import com.cloume.ecnu.authserver.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MyUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ICustomUserDetailsService userService;

    @Autowired
    private IRoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUserDetails user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username: %s", username));
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        List<String> authorityString = new ArrayList<>();
        String roles = user.getRoles();
        List<String> roleList = Arrays.asList(roles.split(","));
        for (String roleName : roleList) {
            Role role = roleService.findByName(roleName);
            if (role == null) {
                continue;
            }
            for (SimplePermission permission : role.getPermissions()) {
                if (!authorityString.contains(String.format("%s-%s", permission.getResourceId(), permission.getPrivilege()))) {
                    authorities.add(new SimpleGrantedAuthority(String.format("%s-%s", permission.getResourceId(), permission.getPrivilege())));
                }
            }
        }
        user.setAuthorities(authorities);

        return user;
    }
}

