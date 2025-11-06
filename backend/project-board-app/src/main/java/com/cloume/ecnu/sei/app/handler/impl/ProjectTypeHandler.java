package com.cloume.ecnu.sei.app.handler.impl;

import com.cloume.commons.rest.response.RestResponse;
import com.cloume.ecnu.sei.app.config.JsonFileConfig;
import com.cloume.ecnu.sei.app.enums.FileTypeEnum;
import com.cloume.ecnu.sei.app.handler.FileTypeHandler;
import com.cloume.ecnu.sei.app.utils.ExportUtil;
import com.cloume.ecnu.sei.app.utils.JsonArrayReaderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * @Author Felix Chen
 * @Date 2024/8/8
 */
@Component
@Slf4j
public class ProjectTypeHandler implements FileTypeHandler {
    @Autowired
    JsonFileConfig jsonFileConfig;

    public static final String MAP_NAME = "busReservation";

    @Override
    public FileTypeEnum getHandlerType() {
        return FileTypeEnum.BUS_RESERVATION;
    }

    @Override
    public void decode(List<Object> rowList, String titleKey) {

    }

    @Override
    public void sort(List<Map<String, Object>> jsonTitles) {
        List<String> sortList = ExportUtil.mapToList(JsonArrayReaderUtils.getInstance(jsonFileConfig.getCodeMapper()).getJsonMapByName(MAP_NAME));
        jsonTitles.sort(Comparator.comparingInt(map -> ExportUtil.indexOfSortList(map, sortList)));
    }

    @Override
    public List<Object> getTemplate() {
        List<String> rowList = ExportUtil.mapToList(JsonArrayReaderUtils.getInstance(jsonFileConfig.getCodeMapper()).getJsonMapByName(MAP_NAME));
        return new ArrayList<>(rowList);
    }

    @Override
    public Map<String, Object> importFromTemplate(MultipartFile file, String httpMethod, Map<String, String> paramMap) throws Exception {
        return null;
    }

    @Override
    public void setColumnWidth(XSSFSheet sheet) {

    }

}
