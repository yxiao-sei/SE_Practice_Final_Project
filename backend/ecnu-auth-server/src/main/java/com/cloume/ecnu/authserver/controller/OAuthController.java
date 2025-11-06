package com.cloume.ecnu.authserver.controller;

import com.alibaba.fastjson.JSONArray;
import com.cloume.commons.rest.response.RestResponse;
import com.cloume.commons.utils.MapBuilder;
import com.cloume.commons.verify.Verifier;
import com.cloume.ecnu.authserver.config.WhiteListProperties;
import com.cloume.ecnu.authserver.model.CustomUserDetails;
import com.cloume.ecnu.authserver.model.JwtAccessToken;
import com.cloume.ecnu.authserver.config.WxCpConfiguration;
import com.cloume.ecnu.authserver.service.ICustomUserDetailsService;
import com.cloume.ecnu.authserver.service.IJwtAccessTokenService;
import com.cloume.ecnu.authserver.utils.Constant;
import com.cloume.ecnu.authserver.utils.ECNUAPIUtils;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpOauth2UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 第三方平台OAuth回调处理
 */
@Log4j2
@Controller
@RequestMapping("/oauth")
public class OAuthController {

    @Value("${client.login-url}")
    private String loginUrl;

    @Value("${client.wechat-approval-url}")
    private String wechatLoginUrl;

    @Value("${oauth-token.url}")
    private String oauthTokenUrl;

    @Value("${oauth-token.logout-url}")
    private String logoutUrl;

    @Value("${oauth-token.client-id}")
    private String clientId;

    @Value("${oauth-token.client-seceret}")
    private String clientSeceret;

    @Value("${oauth-token.grant-type}")
    private String grantType;

    @Value("${oauth-token.scope}")
    private String scope;

    @Value("${wechat.cp.xgb-agentId}")
    private Integer agentId;

    @Value("${jwt.signingKey}")
    private String jwtSigningKey;

    @Value("${spring.profiles.active}")
    private String profile;

    @Value("${client.wechat-approval-url}")
    private String wechatApprovalUrl;

    @Value("${cas-auth.client-id}")
    private String casClientId;

    @Value("${cas-auth.base-url}")
    private String casBaseUrl;

    @Value("${cas-auth.authorize-code}")
    private String casAuthorizeCode;

    @Value("${cas-auth.redirect-uri}")
    private String redirectUri;

    @Autowired
    private WhiteListProperties whiteListProperties;

    @Autowired
    private IJwtAccessTokenService jwtAccessTokenService;

    @Autowired
    private ICustomUserDetailsService customUserDetailsService;

    @Autowired
    private ECNUAPIUtils ecnuapiUtils;

    /**
     * 企业微信扫码登录回调
     *
     * @param request
     * @param response
     * @param code
     * @param state
     * @throws IOException
     */
    @GetMapping("/result/wechat")
    public void wechatOAuthCallback(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "code", required = false) String code,
                                    @RequestParam(value = "state", defaultValue = "") String state,
                                    @RequestParam(value = "device", defaultValue = "pc") String device,
                                    @RequestParam(value = "sourceUrl", defaultValue = "") String sourceUrl,
                                    @RequestParam(value = "uat", defaultValue = "true") Boolean uat) throws IOException {
        log.info("callback: {},{},{},{}", code, state, device, sourceUrl);
        if (request.getSession().getAttribute("_CORP_USER") != null) {
            response.sendRedirect(loginUrl);
        } else {
            log.info("sessionId(code): {},{}", request.getSession().getAttribute("sessionId"), state);
            if (request.getSession().getAttribute("sessionId") != null) {
                state = request.getSession().getAttribute("sessionId").toString();
            }
            JwtAccessToken jwtAccessToken = jwtAccessTokenService.findByChallengeCode(state);
            if (jwtAccessToken == null) {
                response.sendRedirect(loginUrl);
            } else {
                WxCpService wxCpService = WxCpConfiguration.getCpService(agentId);
                try {
                    WxCpOauth2UserInfo wxCpOauth2UserInfo = wxCpService.getOauth2Service().getUserInfo(code);
                    log.info("wechat login: {}", wxCpOauth2UserInfo);
                    if (StringUtils.isEmpty(wxCpOauth2UserInfo.getUserId())) {
                        //非企业内部员工
                        response.sendRedirect(String.format("%s?error=%s", loginUrl, URLEncoder.encode("非本企业微信用户，请尝试其他登录方式", "utf-8")));
                    } else {
                        String username = wxCpOauth2UserInfo.getUserId();
                        if ("dev".equalsIgnoreCase(profile)) {
                            if ("qy01569f946ac509f59ca3dce545".equalsIgnoreCase(wxCpOauth2UserInfo.getUserId())) {
                                username = "10175102217";
                            } else if ("TangBoYuan".equalsIgnoreCase(wxCpOauth2UserInfo.getUserId())) {
                                username = "10180110131";
                            } else if ("ZhouXiangLin".equalsIgnoreCase(wxCpOauth2UserInfo.getUserId())) {
                                username = "10180710302";
                            } else if ("JiangZhiMaLaTang".equalsIgnoreCase(wxCpOauth2UserInfo.getUserId())) {
                                username = "10180710303";
                            } else if ("WuZhiQing".equalsIgnoreCase(wxCpOauth2UserInfo.getUserId())) {
                                username = "20200124";
                            }
                        }
                        CustomUserDetails userDetails = customUserDetailsService.findByUsername(username);
                        if (userDetails == null) {
                            response.sendRedirect(String.format("%s?error=%s", loginUrl, URLEncoder.encode("非本系统账户，请勿登录", "utf-8")));
                        } else {
                            //FIXME:超级不规范的用法！！！！
                            if ("dev".equalsIgnoreCase(profile)) {
                                jwtAccessToken = getOAuth2AccessToken(jwtAccessToken, userDetails.getUsername(), DigestUtils.md5DigestAsHex("123456".getBytes()));
                            } else {
                                jwtAccessToken = getOAuth2AccessToken(jwtAccessToken, userDetails.getUsername(), DigestUtils.md5DigestAsHex(String.format("%s%s", jwtSigningKey, userDetails.getUsername()).getBytes()));
                            }
                            jwtAccessTokenService.save(jwtAccessToken);
                            if (!userDetails.getActivated()) {
                                userDetails.setActivateTime(System.currentTimeMillis());
                                userDetails.setActivated(true);
                                customUserDetailsService.save(userDetails);
                            }
                            String redirectUrl = loginUrl;
                            if (StringUtils.isNotEmpty(sourceUrl)) {
                                log.info("source url: {}", sourceUrl);
                                redirectUrl = String.format("%s?challengeCode=%s", wechatApprovalUrl, URLEncoder.encode(state, "utf-8"));
                            } else {
                                redirectUrl = String.format("%s?challengeCode=%s", wechatApprovalUrl, URLEncoder.encode(state, "utf-8"));
                            }
                            log.info("wechat login success: {}, redirect url: {}", wxCpOauth2UserInfo.getUserId(), redirectUrl);
                            response.sendRedirect(redirectUrl);
                        }
                    }
                } catch (Exception e) {
                    log.error("wechat login error: {}", e);
                    response.sendRedirect(String.format("%s?error=%s", loginUrl, URLEncoder.encode("登录异常，请尝试其他登录方式", "utf-8")));
                }
            }
        }
    }

    /**
     * ECNU CAS认证回调地址
     *
     * @param request
     * @param response
     * @throws IOException
     *
     * base info: [{departmentId=014103, name=毛霈宁, active=1, userType=研究生, department=国际与比较教育研究所, userId=51274103015}]
     * cas user: [{departmentId=014103, name=毛霈宁, active=1, userType=研究生, department=国际与比较教育研究所, userId=51274103015}]
     */
    @GetMapping("/result/cas")
    public void casCallback(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(value = "client", defaultValue = "") String client,
                            @RequestParam(value = "state", defaultValue = "") String state,
                            @RequestParam(defaultValue = "") String code) throws IOException {
        log.info(request.getRequestURI(), request.getRemoteHost(), request.getRemoteAddr());
        try {
            log.info("cas code: {}", code);
            Map<String, String> token = ecnuapiUtils.getToken(code);
            log.info("cas token: {}", token, "client: ", client, "state: ", state);
            if (client.isEmpty()) {
                client = state;
            }
            JSONArray sourceInfo = ecnuapiUtils.getList(Constant.InterfaceType.PRINCIPAL, "", token);
            String username = "";
            String name = "";
            String departmentName = "";
            if (!CollectionUtils.isEmpty(sourceInfo)) {
                JSONObject jsonObject = (JSONObject) sourceInfo.get(0);
                log.info("findByCode, base info: {}", sourceInfo);
                username = jsonObject.get("userId").toString();
                name = jsonObject.get("name").toString();
                departmentName = jsonObject.get("department").toString();
            }
            log.info("cas user: {}", sourceInfo);
            CustomUserDetails userDetails = customUserDetailsService.findByUsername(username);
            if (userDetails == null) {
                // 检查是否启用院系白名单自动创建用户功能
                if (whiteListProperties.isEnabled() && isDepartmentAllowed(departmentName)) {
                    // 自动创建用户
                    userDetails = createUserFromCasInfo(username, name, departmentName, sourceInfo);
                    if (userDetails != null) {
                        log.info("Auto-created user for department: {}, username: {}", departmentName, username);
                    } else {
                        log.error("Failed to create user for department: {}, username: {}", departmentName, username);
                        response.sendRedirect(String.format("%s?error=%s", loginUrl, URLEncoder.encode("用户创建失败，请勿登录", "utf-8")));
                        return;
                    }
                } else {
                    response.sendRedirect(String.format("%s?error=%s", loginUrl, URLEncoder.encode("非本系统账户，请勿登录", "utf-8")));
                    return;
                }
            }
            
            if (userDetails != null) {
                JwtAccessToken jwtAccessToken = new JwtAccessToken();
                jwtAccessToken.setUsername(userDetails.getUsername());
                jwtAccessToken.setChallengeCode(request.getSession().getId());
                request.getSession().setAttribute("sessionId", jwtAccessToken.getChallengeCode());
                jwtAccessToken = jwtAccessTokenService.save(jwtAccessToken);
                jwtAccessToken = getOAuth2AccessToken(jwtAccessToken, userDetails.getUsername(), DigestUtils.md5DigestAsHex(String.format("%s%s", jwtSigningKey, userDetails.getUsername()).getBytes()));
                jwtAccessTokenService.save(jwtAccessToken);
                if (!userDetails.getActivated()) {
                    userDetails.setActivateTime(System.currentTimeMillis());
                    userDetails.setActivated(true);
                    userDetails.setDepartmentName(departmentName);
                    userDetails.setRealName(name);
                    customUserDetailsService.save(userDetails);
                }
                log.info("cas login success: {}, redirect: {}, user-agent: {}", userDetails.getUsername(), client, request.getHeader("User-Agent"));
                if ("project-board-ms".equalsIgnoreCase(client)) {
                    response.sendRedirect("https://seioa.ecnu.edu.cn/project-board-ms/#/layout");
                } else if ("project-board-client".equalsIgnoreCase(client)) {
                    response.sendRedirect("https://seioa.ecnu.edu.cn/project-board-client/#/");
                } else if (!userDetails.getRoles().contains("ROLE_SEI_SYSTEM_ADMIN") && !userDetails.getRoles().contains("ROLE_SEI_ADMIN")
                        && !userDetails.getRoles().contains("ROLE_SEI_APPROVAL_ADMIN")) {
                    //非管理员用户默认跳转到微信客户端页面
                    //TODO:以后多客户端怎么处理？
                    response.sendRedirect(wechatLoginUrl);
                } else if (request.getHeader("User-Agent").contains("WindowsWechat") || request.getHeader("User-Agent").contains("Wechat")
                        || request.getHeader("User-Agent").contains("wxwork") || request.getHeader("User-Agent").contains("MicroMessenger")) {
                    response.sendRedirect(wechatLoginUrl);
                } else {
                    response.sendRedirect(loginUrl);
                }
            }
        } catch (Exception e) {
            log.error("cas login error: {}", e);
            response.sendRedirect(String.format("%s?error=%s", loginUrl, URLEncoder.encode("登录异常，请尝试其他登录方式", "utf-8")));
        }
    }

    /**
     * 获取挑战码
     *
     * @param request
     * @throws IOException
     */
    @PostMapping("/challenge")
    @ResponseBody
    @Deprecated
    public RestResponse<?> getChallengeCode(HttpServletRequest request, @RequestBody Map<String, String> body) throws IOException {
        //TODO： 使用body参数secret签名的方式进行获取挑战码接口的安全校验
        //TODO: 生成挑战码并保存到数据库或者redis中，如何保证并发？
//        if(!new Verifier()
//                .rule("dataSource")
//                .rule("type")
//                .verify(body)){
//            return RestResponse.bad(-1,"invalid input");
//        }
        JwtAccessToken jwtAccessToken = new JwtAccessToken();
        jwtAccessToken.setChallengeCode(request.getSession().getId());
        request.getSession().setAttribute("sessionId", jwtAccessToken.getChallengeCode());
        jwtAccessTokenService.save(jwtAccessToken);

        return RestResponse.good("ok");
    }

    /**
     * 使用挑战码获取token
     *
     * @param request
     * @throws IOException
     */
    @GetMapping("/challenge")
    @ResponseBody
    public RestResponse<?> getTokenWithChallengeCode(HttpServletRequest request) {
        try {
            //TODO: 根据挑战码从数据库/redis中获取token信息
            log.info("sessionId(code): {}", request.getSession().getAttribute("sessionId"));
            String code = request.getSession().getAttribute("sessionId").toString();
            JwtAccessToken jwtAccessToken = jwtAccessTokenService.findByChallengeCode(code);
            if (jwtAccessToken == null) {
                log.error("invalid challenge code: {}", code);
                return RestResponse.bad(-1, "invalid challenge code!");
            } else if (StringUtils.isEmpty(jwtAccessToken.getAccessToken())) {
                log.error("invalid challenge code: {}", code);
                return RestResponse.bad(-2, "token not acquired!");
            }
            log.info("challenge success: {}", code);
            Map<String, String> tokenMap = MapBuilder.begin("access_token", jwtAccessToken.getAccessToken())
                    .and("refresh_token", jwtAccessToken.getRefreshToken())
                    .and("token_type", jwtAccessToken.getTokenType())
                    .and("expires_in", String.valueOf(jwtAccessToken.getExpiresIn()))
                    .build();
            return RestResponse.good(tokenMap);
        } catch (Exception e) {
            log.error("challenge error: {}", e);
            return RestResponse.bad(-403, "challenge error!");
        }
    }

    /**
     * CAS登录系统
     *
     * @param request
     * @throws IOException
     */
    @PostMapping("/login/cas")
    @ResponseBody
    public RestResponse casLogin(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) Map<String, String> body) throws IOException {
        String client = "";
        String redirectClient = redirectUri;
        if (body != null && body.containsKey("client")) {
            client = body.get("client");
            redirectClient = redirectUri + URLEncoder.encode("?client=" + client, "utf-8");
        }
        String authorizeUrl = casBaseUrl + casAuthorizeCode + "&client_id=" + casClientId + "&state=" + client + "&redirect_uri=" + redirectUri ;
        log.info("cas redirect: {}", authorizeUrl);
        return RestResponse.good(authorizeUrl);
    }

    /**
     * 使用用户名密码登录系统
     *
     * @param request
     * @throws IOException
     */
    @PutMapping("/login")
    @ResponseBody
    public RestResponse<?> loginWithUsernamePassword(HttpServletRequest request, @RequestBody Map<String, String> body) {
        if (!new Verifier()
                .rule("username")
                .rule("password")
                .verify(body)) {
            return RestResponse.bad(-1, "invalid input");
        }
        log.info("username-password login: {}", body);
        String username = body.get("username");
        String password = body.get("password");
        CustomUserDetails user = customUserDetailsService.findByUsername(username);
        if (user == null) {
            return RestResponse.bad(-3, "该用户名不存在或已被禁用，请核实后再登录!");
        }
        try {
            JwtAccessToken jwtAccessToken = new JwtAccessToken();
            jwtAccessToken.setUsername(username);
            jwtAccessToken.setChallengeCode(request.getSession().getId());
            request.getSession().setAttribute("sessionId", jwtAccessToken.getChallengeCode());
            jwtAccessToken = jwtAccessTokenService.save(jwtAccessToken);
            jwtAccessToken = getOAuth2AccessToken(jwtAccessToken, username, password);
            jwtAccessTokenService.save(jwtAccessToken);
            if (!user.getActivated()) {
                user.setActivateTime(System.currentTimeMillis());
                user.setActivated(true);
                customUserDetailsService.save(user);
            }
            log.info("username-password login success: {}", user.getUsername());
            return RestResponse.good(jwtAccessToken);
        } catch (Exception e) {
            log.error("username-password login error: {}", e);
            return RestResponse.bad(-2, "用户名或密码错误，请尝试使用企业微信扫码登录或者公共数据库统一认证登录!");
        }
    }

    @Autowired
    private ObjectMapper objectMapper;

    public JwtAccessToken getOAuth2AccessToken(JwtAccessToken jwtAccessToken, String username, String password) throws IOException {

        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(clientId, clientSeceret));

        CloseableHttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        URI uri = URI.create(String.format("%s?grant_type=password", oauthTokenUrl));

//        HttpUriRequest request = new HttpPost(uri);
        HttpPost request = new HttpPost(uri);
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("grant_type", grantType));
        pairs.add(new BasicNameValuePair("username", username));
        pairs.add(new BasicNameValuePair("password", password));
        pairs.add(new BasicNameValuePair("scope", scope));
        request.setEntity(new UrlEncodedFormEntity(pairs));

        CloseableHttpResponse response = client.execute(request);

        String body = EntityUtils.toString(response.getEntity());
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        Map<String, Object> bodyMap = new ObjectMapper().readValue(body, Map.class);
        //TODO: 可以将属性大小写/驼峰命名法/下划线命名法规则设置放到commons的Updater里面去
        jwtAccessToken.setAccessToken(bodyMap.get("access_token").toString());
        jwtAccessToken.setRefreshToken(bodyMap.get("refresh_token").toString());
        jwtAccessToken.setTokenType(bodyMap.get("token_type").toString());
        jwtAccessToken.setExpiresIn(Long.valueOf((Integer) bodyMap.get("expires_in")));
        jwtAccessToken.setJti(bodyMap.get("jti").toString());

        return jwtAccessToken;
    }

    /**
     * 注销
     *
     * @throws IOException
     */
    @PostMapping("/logout")
    @ResponseBody
    public RestResponse logout(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> body) throws IOException, ServletException {
        try {
            log.info("logout: {}", request.getSession().getAttribute("sessionId"));
            request.getSession().removeAttribute("sessionId");
            request.getSession().invalidate();
//            request.logout();
        } catch (Exception e) {
            log.error("logout error: {}", e);
        }
        return RestResponse.good(logoutUrl);
//        return RestResponse.good("https://portal1.ecnu.edu.cn/cas/logout?service=https%3A%2F%2Fportal2020.ecnu.edu.cn%2Fapi%2Fv1%2Fidc%2Fslo");
    }

    /**
     * 检查院系是否在白名单中
     * @param departmentName 院系名称
     * @return 是否允许自动创建用户
     */
    private boolean isDepartmentAllowed(String departmentName) {
        List<String> departments = whiteListProperties.getDepartments();
        
        // 添加调试日志
        log.info("Checking department whitelist - enabled: {}, departments: {}, checking: {}", 
                whiteListProperties.isEnabled(), departments, departmentName);
        
        if (departments == null || departments.isEmpty()) {
            log.warn("Whitelist departments is null or empty");
            return false;
        }
        
        // 如果配置了 "*"，则允许所有院系
        if (departments.contains("*")) {
            log.info("Department '*' found in whitelist, allowing all departments");
            return true;
        }
        
        // 检查具体院系名称
        boolean allowed = departments.contains(departmentName);
        log.info("Department '{}' {} in whitelist", departmentName, allowed ? "found" : "not found");
        return allowed;
    }

    /**
     * 根据CAS信息创建用户
     * @param username 用户名
     * @param name 真实姓名
     * @param departmentName 院系名称
     * @param sourceInfo CAS返回的完整信息
     * @return 创建的用户对象，失败时返回null
     */
    private CustomUserDetails createUserFromCasInfo(String username, String name, String departmentName, JSONArray sourceInfo) {
        try {
            CustomUserDetails userDetails = new CustomUserDetails();
            userDetails.setUsername(username);
            userDetails.setRealName(name);
            userDetails.setDepartmentName(departmentName);
            userDetails.setActivated(true);
            userDetails.setActivateTime(System.currentTimeMillis());
            
            // 根据用户名长度设置默认角色
            // 8位数字的是教师工号，其他数字位默认为学生
            if (username.matches("\\d{8}")) {
                userDetails.setRoles("ROLE_SEI_TEACHER");
            } else if (username.matches("\\d+")) {
                userDetails.setRoles("ROLE_SEI_STUDENT");
            } else {
                userDetails.setRoles("ROLE_USER");
            }
            
            // 设置默认认证方式
            userDetails.setAuthenticationMethods("CAS_REDIRECT,CORP_WECHAT");
            userDetails.setPassword(CustomUserDetails.PASSWORD_ENCODER.encode(DigestUtils.md5DigestAsHex(String.format("%s%s", jwtSigningKey, username).getBytes())));

            // 从sourceInfo中提取更多信息
            if (!CollectionUtils.isEmpty(sourceInfo)) {
                JSONObject jsonObject = (JSONObject) sourceInfo.get(0);
                if (jsonObject.containsKey("userType")) {
                    userDetails.setUserType(jsonObject.get("userType").toString());
                }
                if (jsonObject.containsKey("departmentId")) {
                    userDetails.setDepartmentId(jsonObject.get("departmentId").toString());
                }
            }
            
            // 保存用户
            return customUserDetailsService.save(userDetails);
        } catch (Exception e) {
            log.error("Error creating user from CAS info: {}", e);
            return null;
        }
    }
}
