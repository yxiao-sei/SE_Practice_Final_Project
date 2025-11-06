package com.cloume.ecnu.sei.app.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yjc
 * @Description FITS接口调用工具类
 * @date 2021/7/13-10:41
 */
@Log4j2
@Component
public class CommonRestfulApiUtils {

    private Map<String, Object> mapsToken = null;

    public static CloseableHttpClient getHttpClient() {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            //不进行主机名验证
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build(),
                    NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory())
                    .register("https", sslConnectionSocketFactory)
                    .build();

            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(200);
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslConnectionSocketFactory)
                    .setDefaultCookieStore(new BasicCookieStore())
                    .setConnectionManager(cm).build();
            return httpclient;
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }

    public Map<String, Object> callInterface(String method, String url, Map<String, Object> body, String token) {
        CloseableHttpClient httpclient = getHttpClient();
        Map<String, Object> result = new HashMap<>();
        int code = 0;
        //调用auth的token接口使用
        if (method.equalsIgnoreCase("POST")) {
            HttpPost httpPost = new HttpPost(url);
            try {
                StringEntity se = new StringEntity(JSONObject.toJSONString(body), "utf-8");
                httpPost.setEntity(se);
                httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
                if (StringUtils.isNotEmpty(token)) {
                    httpPost.setHeader("Authorization", "Bearer " + token);
                }
                CloseableHttpResponse response = httpclient.execute(httpPost);
                code = response.getStatusLine().getStatusCode();
                if (code == 200) {
                    result = JSONObject.parseObject(EntityUtils.toString(response.getEntity(), "utf-8"));
                } else {
                    log.error("callInterface:{}, {}, {}, {}", method, url, body, response);
                }
            } catch (Exception e) {
                log.error("callInterface:{}, {}", e.getMessage(), e.getCause());
            }
        }
        //调用MAPS的接口使用
        else if (method.equalsIgnoreCase("GET")) {
            HttpGet httpGet = new HttpGet(url);
            try {
                httpGet.setHeader("Content-Type", "application/json; charset=utf-8");
                if (StringUtils.isNotEmpty(token)) {
                    httpGet.setHeader("Authorization", "Bearer " + token);
                }
                CloseableHttpResponse response = httpclient.execute(httpGet);
                code = response.getStatusLine().getStatusCode();
                if (code == 200) {
                    result = JSONObject.parseObject(EntityUtils.toString(response.getEntity(), "utf-8"));
                } else {
                    log.error("callInterface:{}, {}, {}, {}", method, url, body, response);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else if (method.equalsIgnoreCase(Constant.InterfaceType.PUT)) {
            HttpPut httpPut = new HttpPut(url);
            try {
                StringEntity se = new StringEntity(JSONObject.toJSONString(body), "utf-8");
                httpPut.setEntity(se);
                httpPut.setHeader("Content-Type", "application/json; charset=utf-8");
                if (StringUtils.isNotEmpty(token)) {
                    httpPut.setHeader("Authorization", "Bearer " + token);
                }
                CloseableHttpResponse response = httpclient.execute(httpPut);
                code = response.getStatusLine().getStatusCode();
                if (code == 200) {
                    result = JSONObject.parseObject(EntityUtils.toString(response.getEntity(), "utf-8"));
                } else {
                    log.error("callInterface:{}, {}, {}, {}", method, url, body, response);
                }
            } catch (Exception e) {
                log.error("callInterface:{}, {}", e.getMessage(), e.getCause());
            }
        } else if (method.equalsIgnoreCase(Constant.InterfaceType.DELETE)) {
            HttpDelete httpDelete = new HttpDelete(url);
            try {
                httpDelete.setHeader("Content-Type", "application/json; charset=utf-8");
                if (StringUtils.isNotEmpty(token)) {
                    httpDelete.setHeader("Authorization", "Bearer " + token);
                }
                CloseableHttpResponse response = httpclient.execute(httpDelete);
                code = response.getStatusLine().getStatusCode();
                if (code == 200) {
                    result = JSONObject.parseObject(EntityUtils.toString(response.getEntity(), "utf-8"));
                } else {
                    log.error("callInterface:{}, {}, {}, {}", method, url, body, response);
                }
            } catch (Exception e) {
                log.error("callInterface:{}, {}", e.getMessage(), e.getCause());
            }
        }
        return result;
    }


}
