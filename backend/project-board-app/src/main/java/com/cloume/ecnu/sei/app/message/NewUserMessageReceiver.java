package com.cloume.ecnu.sei.app.message;

import com.alibaba.fastjson.JSONObject;
import com.cloume.ecnu.sei.app.model.CustomUserDetails;
import com.cloume.ecnu.sei.app.service.ICustomUserDetailsService;
import com.cloume.ecnu.sei.app.utils.Constant;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yxiao
 * @date 2020-05-11
 * @description 接收处理同步任务消息
 */
@Log4j2
@Component
public class NewUserMessageReceiver {

    @Value("${security.oauth2.resource.jwt.key-value}")
    private String jwtSigningKey;

    @Autowired
    private ICustomUserDetailsService customUserDetailsService;

    /**
     * 异步创建系统用户登录账户信息
     *
     * @param msg
     * @return
     */
    @RabbitListener(queues = "topic.new_users")
    public void processMessage(String msg) {
        log.info(Thread.currentThread().getName() + " 接收到来自topic.new_users队列的消息：" + msg);
        try {
            JSONObject jsonTask = JSONObject.parseObject(msg);
            String username = jsonTask.getString("username");
            String name = jsonTask.getString("name");
            String roleString = jsonTask.getString("roles");
            Boolean roleCover = jsonTask.getBoolean("roleCover");
            List<String> newRoles = Arrays.asList(roleString.split(","));
            CustomUserDetails customUserDetails = customUserDetailsService.findByUsername(username);
            if (customUserDetails == null) {
                customUserDetails = new CustomUserDetails();
                customUserDetails.setUsername(username);
                customUserDetails.setRealName(name);
                customUserDetails.setSubDepartmentCode(jsonTask.getString("subDepartmentCode"));
                customUserDetails.setDepartmentId(jsonTask.getString("departmentId"));
                customUserDetails.setDepartmentName(jsonTask.getString("departmentName"));
                customUserDetails.setSubDepartmentName(jsonTask.getString("subDepartmentName"));
            }
            List<String> roles = new ArrayList<>();
            if (StringUtils.isNotEmpty(customUserDetails.getRoles()) && !roleCover) {
                roles = new ArrayList<>(Arrays.asList(customUserDetails.getRoles().split(",")));
                roles.addAll(newRoles);
            } else if (StringUtils.isEmpty(customUserDetails.getRoles()) || roleCover) {
                roles = newRoles;
            }

            //院系管理员默认只能通过ECNU统一身份认证或者企业微信扫码登录
            if (jsonTask.containsKey("authenticationMethods")) {
                customUserDetails.setAuthenticationMethods(jsonTask.getString("authenticationMethods"));
            } else {
                customUserDetails.setAuthenticationMethods(String.format("%s,%s", Constant.AuthenticationType.CAS_REDIRECT, Constant.AuthenticationType.CORP_WECHAT));
            }

            if (Constant.AuthenticationType.USERNAME_PASSWORD.equalsIgnoreCase(customUserDetails.getAuthenticationMethods())
                    && jsonTask.containsKey("password") && !StringUtils.isEmpty(jsonTask.getString("password"))) {
                customUserDetails.setPassword(CustomUserDetails.PASSWORD_ENCODER.encode(jsonTask.getString("password")));
            } else if (customUserDetails.getAuthenticationMethods().contains(Constant.AuthenticationType.CAS_REDIRECT)
                    || customUserDetails.getAuthenticationMethods().contains(Constant.AuthenticationType.CORP_WECHAT)) {
                customUserDetails.setPassword(CustomUserDetails.PASSWORD_ENCODER.encode(DigestUtils.md5DigestAsHex(String.format("%s%s", jwtSigningKey, username).getBytes())));
            }
            //log.info(CustomUserDetails.PASSWORD_ENCODER.encode(jsonTask.getString("password")));
            log.info(CustomUserDetails.PASSWORD_ENCODER.encode(DigestUtils.md5DigestAsHex(String.format("%s%s", jwtSigningKey, username).getBytes())));
            if (jsonTask.containsKey("creator")) {
                if (StringUtils.isEmpty(customUserDetails.getCreator())) {
                    customUserDetails.setCreator(jsonTask.getString("creator"));
                }
                customUserDetails.setLastUpdater(jsonTask.getString("creator"));
            }

            roles = roles.stream().distinct().collect(Collectors.toList());//去重
            customUserDetails.setRoles(String.join(",", roles));
            customUserDetailsService.save(customUserDetails);
            log.info(String.format("user.new, 成功添加用户：%s, roles: %s", customUserDetails.getUsername(), customUserDetails.getRoles()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
