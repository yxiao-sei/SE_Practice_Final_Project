package com.cloume.ecnu.sei.app.config;

import com.cloume.ecnu.sei.app.utils.JsonMapReaderUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yxiao
 * @date
 * @description
 */
@Data
@Component
@ConfigurationProperties(prefix = "config")
public class JsonFileConfig {
    private Map<String, String> codeMapper;

    /**
     * list形式的json文件列表
     */
    private Map<String, String> listCodeMapper;

    /**
     * 错误码
     */
    private static Map<String, Object> errorCode;

    /**
     * 模板文件名
     */
    private static Map<String, Object> templateFileName;

    public Map<String, Object> getErrorCode() {
        if (errorCode == null) {
            errorCode = JsonMapReaderUtils.getInstance(codeMapper).getJsonMapByName("errorCode");
        }

        return errorCode;
    }

    public Map<String, Object> getTemplateFileName() {
        if (templateFileName == null) {
            templateFileName = JsonMapReaderUtils.getInstance(codeMapper).getJsonMapByName("templateFileName");
        }

        return templateFileName;
    }
}
