package com.cloume.ecnu.sei.app.handler;

import com.cloume.ecnu.sei.app.enums.FileTypeEnum;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileTypeHandler {
    FileTypeEnum getHandlerType();

    /**
     * 对不同导出的字段进行解析
     * @param rowList 解析的每行数据
     * @param titleKey 需要解析的表头
     */
    void decode(List<Object> rowList, String titleKey);

    /**
     * 对不同导出类型的表头进行排序
     * @param jsonTitles 需要排序的表头
     */
    void sort(List<Map<String, Object>> jsonTitles);

    /**
     * 根据不同导出类型生成对应导出模版
     *
     * @return 对应类型的表头
     */
    List<Object> getTemplate();

    /**
     * 根据不同类型从Excel导入
     * @param file excel文件
     * @param httpMethod http方法
     * @throws Exception excel解析异常
     * @return 成功导入的条数
     */
    Map<String, Object> importFromTemplate (MultipartFile file, String httpMethod, Map<String, String> paramMap) throws Exception;

    /**
     * 设置excel列宽
     * @param sheet 表格
     */
    void setColumnWidth(XSSFSheet sheet);
}
