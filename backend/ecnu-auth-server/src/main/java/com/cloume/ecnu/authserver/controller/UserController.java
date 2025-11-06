package com.cloume.ecnu.authserver.controller;

import com.cloume.commons.beanutils.Updater;
import com.cloume.commons.rest.response.RestResponse;
import com.cloume.commons.verify.Verifier;
import com.cloume.ecnu.authserver.model.CustomUserDetails;
import com.cloume.ecnu.authserver.service.ICustomUserDetailsService;
import com.cloume.ecnu.authserver.utils.Constant;
import com.cloume.ecnu.authserver.utils.ECNUAPIUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.AuthorizationServerProperties;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * @author yxiao
 * @date 2020-05-13
 * @description
 */
@RequestMapping("/user")
@RestController
@Log4j2
public class UserController {

    @Value("${jwt.signingKey}")
    private String jwtSigningKey;

    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired
    private ECNUAPIUtils ecnuapiUtils;

    @Autowired
    private ICustomUserDetailsService customUserDetailsService;

    @GetMapping("/me")
    public RestResponse<?> getPrincipal(@AuthenticationPrincipal AuthorizationServerProperties.Jwt principal) {
        return RestResponse.good(principal);
    }


    /**
     * 根据学号得到该教师的照片访问路径
     * 访问路径算法：其中outid为需要查看照片的学工号，pid为口令参数，关于口令参数的算法说明如下：
     * pid=md5(str1+outid+day)
     * 其中：
     * pid为32位MD5加密后小写的密文串。
     * str1=31AD111DE2210E5CDC95663B
     * outid=学工号
     * day=当前日期中的日（day）-1
     *
     * @return
     */
    @GetMapping("/photo")
    public RestResponse<?> getPhoto(Principal principal, HttpServletRequest request, @RequestParam(defaultValue = "") String code) {
        log.info("photo code:{}, principal: {}", code, principal.getName());
        Map<String, String> token = (Map<String, String>) request.getSession().getAttribute("token");
        String employeeCode;
        if (StringUtils.isNotEmpty(code) && code != "null" && token.get("userType") != null && token.get("userType").equalsIgnoreCase(Constant.UserRole.ADMINISTRATOR)) {
            employeeCode = code;
            token = (Map<String, String>) request.getSession().getAttribute("clientToken");
        } else {
            employeeCode = request.getSession().getAttribute("employeeCode").toString();
        }
        byte[] data = new byte[0];
        try {
            data = ecnuapiUtils.getRawData(Constant.InterfaceType.PHOTO, employeeCode, token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RestResponse.good(data);
    }

    @PutMapping
    public RestResponse<?> addNewUser(@RequestBody Map<String, Object> body) {
        if (!new Verifier()
                .rule("username", true, check -> StringUtils.isNotEmpty(body.get("username").toString()))
                .rule("password", true, check -> StringUtils.isNotEmpty(body.get("password").toString()))
                .result((r, s) -> {
                    log.debug(String.format("request body validate result: %s %s", r, s));
                }).verify(body)
        ) {
            return RestResponse.bad(-1, "invalid request body");
        }
        String USERNAME = "username";
        if (body.containsKey(USERNAME)) {
            if (customUserDetailsService.findByUsername(body.get(USERNAME).toString()) != null) {
                return RestResponse.bad(-2, "用户名已存在！");
            }
        }
        String PASSWORD = "password";
        if (body.containsKey(PASSWORD)) {
            body.put(PASSWORD, CustomUserDetails.PASSWORD_ENCODER.encode(body.get(PASSWORD).toString()));
        }

        CustomUserDetails user = Updater.wrap(new CustomUserDetails()).update(body);

        user = customUserDetailsService.save(user);

        return RestResponse.good(user);
    }


    @GetMapping("/search")
    public RestResponse<?> searchByRealName(@RequestParam("q") String keyword,
                                            @RequestParam(value = "limit", required = false) Integer limit) {
        if (StringUtils.isBlank(keyword)) {
            return RestResponse.good(java.util.Collections.emptyList());
        }
        List<CustomUserDetails> users = customUserDetailsService.searchByRealName(keyword, limit);
        List<Map<String, String>> result = users.stream().map(u -> {
            Map<String, String> m = new HashMap<>();
            m.put("realName", u.getRealName());
            m.put("username", u.getUsername());
            return m;
        }).collect(Collectors.toList());
        return RestResponse.good(result);
    }


    /**
     * 批量创建学工部管理员登录账号
     * {"users": [
     * {"username":"xxx", "realName":"xxx", "roles":"xxx,xxx"},
     * {"username":"xxx", "realName":"xxx", "roles":"xxx,xxx"}
     * ]}
     *
     * @return
     */
    @PutMapping("/llbxt-admin-account-prd")
    public RestResponse<?> batchAddAdminAccountPrd(@RequestBody Map<String, Object> body) {
        log.info("llbxt-account-prd, 开始批量创建管理员账号: {}", body);
        try {
            List<Map<String, String>> users = (List<Map<String, String>>) body.get("users");
            users.parallelStream().forEach(user -> {
                CustomUserDetails customUserDetails = customUserDetailsService.findByUsername(user.get("username"));
                if (customUserDetails == null) {
                    customUserDetails = new CustomUserDetails();
                }
                customUserDetails.setPassword(CustomUserDetails.PASSWORD_ENCODER.encode(DigestUtils.md5DigestAsHex(String.format("%s%s", jwtSigningKey, customUserDetails.getUsername()).getBytes())));

                customUserDetailsService.save(customUserDetails);
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return RestResponse.good("ok");
    }
}
