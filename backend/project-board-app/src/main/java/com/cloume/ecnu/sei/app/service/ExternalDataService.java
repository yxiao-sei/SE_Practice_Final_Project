package com.cloume.ecnu.sei.app.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ExternalDataService {
    private String tokenType;
    private String accessToken;

    @Value("${spring.profiles.active}")
    private String active;

    @Value("${interface.url}")
    private String urlHead;

    @Value("${interface.token}")
    private String token;

    @Value("${interface.clientId}")
    private String clientId;

    @Value("${interface.clientSecret}")
    private String clientSecret;

    @Value("${interface.grantType}")
    private String grantType;


    /**
     * 从信息办获取数据
     *
     * @param url 接口地址
     * @return
     */
    public Object getDataList(String url) {
        try {
            if (StringUtils.isEmpty(accessToken)) {
                //判断本地是否存在token
                Map<String, String> tokenResult = new HashMap<>();//获取token
                tokenResult = getToken();//获取token
                if (tokenResult.isEmpty()) {
                    return null;
                }
                //成功设置本地token
                accessToken = tokenResult.get("accessToken");
                tokenType = tokenResult.get("tokenType");
            }

            //获取数目成功拉取全部列表
            Object source = getSource(accessToken, tokenType, url);

            return source;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 接口获取数据列表
     *
     * @param accessToken
     * @param tokenType
     * @return
     */
    private Object getSource(String accessToken, String tokenType, String urlGet) {
        try {
            URL url = new URL(urlGet);
            //get参数
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Authorization", tokenType + " " + accessToken);

            int responseCode = conn.getResponseCode();
            if (responseCode == 401) {
                //token失效时重新获取
                Map<String, String> token = getToken();
                if (token.isEmpty()) {
                    return null;
                }
                this.accessToken = token.get("accessToken");
                this.tokenType = token.get("tokenType");
                return getSource(this.accessToken, this.tokenType, urlGet);
            }

            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (int c; (c = in.read()) >= 0; ) {
                sb.append((char) c);
            }
            String response = sb.toString();
            JSONObject jsonObject = JSON.parseObject(response);
            if (jsonObject != null) {
                return jsonObject.get("data");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 接口获取token
     *
     * @return
     */
    private Map<String, String> getToken() {
        //接口获取token
        Map<String, String> resultMap = new HashMap<>();
        try {
            //外部接口获取token
            String urlGetToken = urlHead + token + "?grant_type=" + grantType + "&client_id=" + clientId + "&client_secret=" + clientSecret;
            URL url = new URL(urlGetToken);
            //post参数
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("geomInfo", "");
            params.put("f", "json");

            //开始访问
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);


            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (int c; (c = in.read()) >= 0; )
                sb.append((char) c);
            String response = sb.toString();
            JSONObject result = JSON.parseObject(response);
            resultMap.put("tokenType", result.getString("token_type"));
            resultMap.put("accessToken", result.getString("access_token"));
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }
}
