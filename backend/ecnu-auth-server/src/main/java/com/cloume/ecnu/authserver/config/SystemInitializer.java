package com.cloume.ecnu.authserver.config;

import com.cloume.ecnu.authserver.model.CustomUserDetails;
import com.cloume.ecnu.authserver.model.Role;
import com.cloume.ecnu.authserver.repository.DefaultCustomUserDetailsRepository;
import com.cloume.ecnu.authserver.repository.DefaultRoleRepository;
import com.cloume.ecnu.authserver.repository.DefaultSimplePermissionRepository;
import com.google.gson.reflect.TypeToken;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 系统初始化配置类，主要用于加载内置数据到目标数据库上
 *
 * @author yxiao
 */
@Log4j2
@Component
public class SystemInitializer {

    @Value("${initialzation.file.users}")
    private String userFileName;

    @Value("${initialzation.file.roles}")
    private String roleFileNames;

    @Autowired
    private DefaultCustomUserDetailsRepository userRepository;

    @Autowired
    private DefaultRoleRepository roleRepository;

    @Autowired
    private DefaultSimplePermissionRepository simplePermissionRepository;

    @PostConstruct
    public boolean initialize() throws Exception {
        try {
            //导入初始的系统设置
            InputStream userInputStream = getClass().getClassLoader().getResourceAsStream(userFileName);
            if (userInputStream == null) {
                throw new Exception("initialzation user file not found: " + userFileName);
            }

            List<String> roleFileList = Arrays.asList(roleFileNames.split(","));
            roleFileList.stream().forEach(roleFileName -> {
                InputStream roleInputStream = getClass().getClassLoader().getResourceAsStream(roleFileName);
                if (roleInputStream != null) {
                    //导入初始的系统管理员角色
                    Type roleTokenType = new TypeToken<ArrayList<Role>>() {}.getType();
                    ArrayList<Role> roles = CommonGsonBuilder.create().fromJson(new InputStreamReader(roleInputStream, StandardCharsets.UTF_8), roleTokenType);
                    for (Role role : roles) {
                        if (roleRepository.findByNameAndIsRemoved(role.getName(), false) == null) {
                            role.getPermissions().forEach(simplePermission -> {
                                simplePermissionRepository.save(simplePermission);
                            });
                            roleRepository.save(role);
                            log.info("导入初始角色数据：{}", role.getName());
                        }
                    }
                }
            });

            //导入初始的系统管理员用户
            Type teacherTokenType = new TypeToken<ArrayList<CustomUserDetails>>() {
            }.getType();
            ArrayList<CustomUserDetails> users = CommonGsonBuilder.create().fromJson(new InputStreamReader(userInputStream, StandardCharsets.UTF_8), teacherTokenType);
            for (CustomUserDetails user : users) {
                if (userRepository.findFirstByUsernameAndIsRemoved(user.getUsername(), false) == null) {
                    userRepository.save(user);
                    log.info("导入初始系统用户数据：{}", user.getUsername());
                }
            }
        } catch (Exception e) {
            log.error("初始角色或用户信息导入失败：{}", e.getStackTrace());
        }
        return true;
    }
}
