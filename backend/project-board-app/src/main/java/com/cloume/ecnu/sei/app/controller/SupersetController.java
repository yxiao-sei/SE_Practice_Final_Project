package com.cloume.ecnu.sei.app.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cloume.commons.rest.response.RestResponse;
import com.cloume.ecnu.sei.app.utils.CommonRestfulApiUtils;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Log4j2
@RestController
@RequestMapping("/app/superset")
public class SupersetController {
    @Value("${url.superset}")
    private String supersetBaseUrl;

    @Resource
    private CommonRestfulApiUtils commonRestfulApiUtils;

    @RequestMapping("/**")
    public RestResponse<?> getSupersetCharts(HttpServletRequest request) {
        val method = request.getMethod();
        val url = supersetBaseUrl + request.getRequestURI().replaceAll("/superset", "") + "?" + request.getQueryString();
        JSONObject body = null;
        try (val reader = request.getReader()) {
            val builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            val bodyStr = builder.toString();
            body = JSON.parseObject(bodyStr);
        } catch (Exception e) {
            log.error(e);
        }

        val res = commonRestfulApiUtils.callInterface(method, url, body, "");
//        try {
//            val result = (JSONObject) res.get("result");
//            val query = result.remove("query");
//        } catch (Exception e) {
//            log.error(e);
//        }
        return RestResponse.good(res);
    }
}
