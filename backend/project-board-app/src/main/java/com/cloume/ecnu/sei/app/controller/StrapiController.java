package com.cloume.ecnu.sei.app.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cloume.commons.rest.response.RestResponse;
import com.cloume.ecnu.sei.app.utils.CommonRestfulApiUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.security.Principal;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/strapi")
public class StrapiController {

    @Value("${url.strapi}")
    private String strapiBaseUrl;

    @Resource
    private CommonRestfulApiUtils commonRestfulApiUtils;

    @RequestMapping("/**")
    public RestResponse<?> strapiAgent(HttpServletRequest request, Principal principal) {
        log.info(request.getQueryString());
        String realName = principal.getName();
        if (principal instanceof OAuth2Authentication) {
            OAuth2Authentication authentication = (OAuth2Authentication) principal;
            OAuth2AuthenticationDetails oauth2Details = (OAuth2AuthenticationDetails) authentication.getDetails();
            Map<String, Object> decodedDetails = (Map<String, Object>) oauth2Details.getDecodedDetails();
            realName = decodedDetails.get("realName").toString();
        }
            String token = null;
        String endUrl;
        if (StringUtils.isEmpty(request.getQueryString())) {
            endUrl = strapiBaseUrl + request.getRequestURI();
        } else {
            endUrl = strapiBaseUrl + request.getRequestURI() + "?" + request.getQueryString();
        }
        StringBuffer sb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            log.error(e);
        }
        endUrl = endUrl.replaceAll("/board/strapi", "");
        String jsonString = sb.toString().replaceAll("\\s", "").replaceAll("\n", "");
        JSONObject requestBody = JSON.parseObject(jsonString);
        if (requestBody != null) {
            requestBody.getJSONObject("data").put("lastUpdaterNumber", principal.getName());
            requestBody.getJSONObject("data").put("lastUpdaterName", realName);
        }
        Map<String, Object> res = commonRestfulApiUtils.callInterface(request.getMethod(), endUrl, requestBody, token);
        if (res == null || res.isEmpty()) {
            return RestResponse.bad(-1, "请求失败");
        }
        log.info("url:{}, res:{}", endUrl, res);
        return RestResponse.good(res);
    }

}
