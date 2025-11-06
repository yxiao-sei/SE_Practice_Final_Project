package com.cloume.ecnu.authserver.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author yjc
 * @Description 获取token并保存，超时刷新
 * @date 2021/7/13-10:41
 */
@Component
@Log4j2
public class ECNUAPIUtils {

    @Value("${interface.baseUrl}")
    private String baseUrl;

    @Value("${interface.token}")
    private String token;

    @Value("${interface.info}")
    private String info;

    @Value("${interface.principal}")
    private String principalURL;

    @Value("${interface.photo}")
    private String photo;

    @Value("${interface.clientId}")
    private String clientId;

    @Value("${interface.clientSecret}")
    private String clientSecret;

    @Value("${interface.grantType}")
    private String grantType;

    @Value("${spring.profiles.active}")
    private String profiles;

    @Value("${interface.refresh}")
    private String refresh;

    @Value("${interface.grantTypeNew}")
    private String grantTypeNew;

    @Value("${cas-auth.redirect-uri}")
    private String redirectUrI;

    private Map<String, String> userToken = null;
    private long grantTime = 0;

    /**
     * 初始化
     */
    @PostConstruct
    void init() {
        if (userToken == null) {
            userToken = getTokenFromThird();
        }
    }


    public Map<String, String> getClientToken() {
        if (userToken == null || userToken.isEmpty()) {
            return getTokenFromThird();
        }
        return userToken;
    }

    public Map<String, String> getNewClientToken() {
        userToken = getTokenFromThird();
        return userToken;
    }

    /**
     * 接口获取token
     *
     * @return
     */
    private Map<String, String> getTokenFromThird() {
        Date time1 = new Date();
        log.info("ECNUAPIUtils.getTokenFromThird时间1: " + time1);
        //接口获取token
        Map<String, String> resultMap = new HashMap<>();
        try {
            //外部接口获取token
            String urlGetToken = baseUrl + token + "?grant_type=" + grantType + "&client_id=" + clientId + "&client_secret=" + clientSecret;
            URL url = new URL(urlGetToken);
            Date time2 = new Date();
            log.info("ECNUAPIUtils.getTokenFromThird时间2: " + time2);
            long diffInSeconds = (time2.getTime() - time1.getTime()) / 1000; // 计算秒数差
            log.info("ECNUAPIUtils.getTokenFromThird2-1两个时间相差的秒数：" + diffInSeconds);
            //post参数
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("geomInfo", "");
            params.put("f", "json");
            Date time3 = new Date();
            log.info("ECNUAPIUtils.getTokenFromThird时间3: " + time3);
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
//            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
//            conn.getOutputStream().write(postDataBytes);

            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (int c; (c = in.read()) >= 0; ) {
                sb.append((char) c);
            }
            String response = sb.toString();
            JSONObject result = JSON.parseObject(response);
            resultMap.put("tokenType", result.getString("token_type"));
            resultMap.put("accessToken", result.getString("access_token"));
            grantTime = System.currentTimeMillis() + result.getLong("expires_in") * 1800;//todo:token过期时间调整
            resultMap.put("grantTime", String.valueOf(grantTime));
            Date time4 = new Date();
            log.info("ECNUAPIUtils.getTokenFromThird时间4: " + time4);
            diffInSeconds = (time4.getTime() - time3.getTime()) / 1000; // 计算秒数差
            log.info("ECNUAPIUtils.getTokenFromThird4-3两个时间相差的秒数：" + diffInSeconds);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    /**
     * 接口获取token
     *
     * @return
     */
    public Map<String, String> getTokenFromThirdRefresh(Map<String, String> oldToken) {
        //接口获取token
        Map<String, String> resultMap = new HashMap<>();
        try {
            //外部接口获取token
            String urlGetToken = baseUrl + token + "?grant_type=" + refresh + "&client_id=" + clientId + "&client_secret=" + clientSecret + "&refresh_token=" + oldToken.get("refreshToken");
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
            for (int c; (c = in.read()) >= 0; ) {
                sb.append((char) c);
            }
            String response = sb.toString();
            JSONObject result = JSON.parseObject(response);
            resultMap.put("tokenType", result.getString("token_type"));
            resultMap.put("accessToken", result.getString("access_token"));
            grantTime = System.currentTimeMillis() + (result.getLong("expires_in") * 1000);//todo:token过期时间调整
            resultMap.put("grantTime", String.valueOf(grantTime));
            resultMap.put("refreshToken", result.getString("refresh_token"));
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    /**
     * 接口获取token
     *
     * @return
     */
    private Map<String, String> getTokenFromThirdNew(String code) {
        //接口获取token
        Map<String, String> resultMap = new HashMap<>();
        try {
            //外部接口获取token
            String urlGetToken = baseUrl + token + "?grant_type=" + grantTypeNew + "&client_id=" + clientId + "&client_secret=" + clientSecret + "&scope=ECNU-Basic&redirect_uri=" + redirectUrI + "&code=" + code;
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
            for (int c; (c = in.read()) >= 0; ) {
                sb.append((char) c);
            }
            String response = sb.toString();
            JSONObject result = JSON.parseObject(response);
            resultMap.put("tokenType", result.getString("token_type"));
            resultMap.put("accessToken", result.getString("access_token"));
            grantTime = System.currentTimeMillis() + (result.getLong("expires_in") * 1800);//todo:token过期时间调整
            resultMap.put("grantTime", String.valueOf(grantTime));
            resultMap.put("refreshToken", result.getString("refresh_token"));
            return resultMap;
        } catch (Exception e) {
            log.error("get token error: {}", e);
        }
        return resultMap;
    }

    /**
     * 解析为JSONObject返回
     *
     * @param interfaceType
     * @param interfaceParam
     * @return
     */
    public JSONObject getSingleObject(String interfaceType, String interfaceParam, Map<String, String> useToken) {
        JSONObject result = new JSONObject();
        try {
            String apiData = getUrlData(interfaceType, interfaceParam, useToken);
            result = JSON.parseObject(apiData).getJSONObject("data");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 解析为JSONArray返回
     *
     * @param interfaceType
     * @param interfaceParam
     * @return
     */
    public JSONArray getList(String interfaceType, String interfaceParam, Map<String, String> useToken) {
        JSONArray result = new JSONArray();
        try {
            String apiData = getUrlData(interfaceType, interfaceParam, useToken);
            result = JSON.parseObject(apiData).getJSONArray("data");
        } catch (Exception e) {
            log.error("getList error: {}, {}, {}, {}", interfaceType, interfaceParam, useToken, e);
        }
        if (result == null) {
            return new JSONArray();
        }
        return result;
    }

    /**
     * 解析为字节流返回
     *
     * @param interfaceType
     * @param interfaceParam
     * @return
     */
    public byte[] getRawData(String interfaceType, String interfaceParam, Map<String, String> useToken) {
        byte[] result = new byte[0];
        try {
            //建立链接
            HttpURLConnection conn = getConnection(interfaceType, interfaceParam, useToken);
            //通过输入流获取图片数据
            InputStream inStream = conn.getInputStream();
            //得到图片的二进制数据，以二进制封装得到数据
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            //创建一个Buffer字符串
            byte[] buffer = new byte[1024];
            //每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
            //使用一个输入流从buffer里把数据读取出来
            while ((len = inStream.read(buffer)) != -1) {
                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
            //关闭输入流
            inStream.close();
            byte[] data = outStream.toByteArray();
            result = data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据接口类型和参数进行调用，返回String类型数据
     *
     * @param interfaceType
     * @param interfaceParam
     * @return
     */
    private String getUrlData(String interfaceType, String interfaceParam, Map<String, String> useToken) {
        String result = "";
        try {
            //建立链接
            HttpURLConnection conn = getConnection(interfaceType, interfaceParam, useToken);
            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int c; (c = in.read()) >= 0; ) {
                sb.append((char) c);
            }
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param interfaceType
     * @param interfaceParam
     * @return
     */
    private HttpURLConnection getConnection(String interfaceType, String interfaceParam, Map<String, String> useToken) {
        HttpURLConnection result = null;
        try {
            String interfaceUrl = "";
            switch (interfaceType) {
                case Constant.InterfaceType.PRINCIPAL:
                    interfaceUrl = principalURL;
                    break;
                case Constant.InterfaceType.BASE_INFO:
                    interfaceUrl = info;
                    break;
                case Constant.InterfaceType.PHOTO:
                    interfaceUrl = photo;
                    break;
            }
            long time1 = System.currentTimeMillis();
            log.info("ECNUAPIUtils.getConnection时间1: " + time1);
            String urlGetOrganization = baseUrl + interfaceUrl + interfaceParam;
            long time2 = System.currentTimeMillis();
            log.info("ECNUAPIUtils.getConnection时间2: " + time2);
            long diffInSeconds = (time2 - time1) / 1000; // 计算秒数差
            log.info("ECNUAPIUtils.getConnection 1-2两个时间相差的秒数：" + diffInSeconds);
            URL url = new URL(urlGetOrganization);
            //get参数
            result = (HttpURLConnection) url.openConnection();
            result.setRequestMethod("GET");
            result.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            result.setRequestProperty("Authorization", useToken.get("tokenType") + " " + useToken.get("accessToken"));
            int responseCode = result.getResponseCode();
            long time3= System.currentTimeMillis();
            log.info("ECNUAPIUtils.getConnection时间3: " + time3);
            diffInSeconds = (time3 - time2) / 1000; // 计算秒数差
            log.info("ECNUAPIUtils.getConnection 3-2两个时间相差的秒数：" + diffInSeconds);
            if (responseCode == 401) {
                //token失效时重新获取
                if (profiles.equals("dev") || profiles.equals("uat")) {
                    if (StringUtils.isNotEmpty(useToken.get("refresh_token"))) {
                        useToken = getTokenFromThirdRefresh(useToken);
                        return getConnection(interfaceType, interfaceParam, useToken);
                    } else {
                        useToken = getTokenFromThird();
                        userToken = useToken;
                        return getConnection(interfaceType, interfaceParam, useToken);
                    }
                } else {
                    if (StringUtils.isNotEmpty(useToken.get("refresh_token"))) {
                        useToken = getTokenFromThirdRefresh(useToken);
                        return getConnection(interfaceType, interfaceParam, useToken);
                    }
                }
            }
            long time4= System.currentTimeMillis();
            log.info("ECNUAPIUtils.getConnection时间4: " + time4);
            diffInSeconds = (time4 - time3) / 1000; // 计算秒数差
            log.info("ECNUAPIUtils.getConnection 4-3两个时间相差的秒数：" + diffInSeconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<String, String> getToken(String code) {
        Map<String, String> token = this.getTokenFromThirdNew(code);
        return token;
    }
}
