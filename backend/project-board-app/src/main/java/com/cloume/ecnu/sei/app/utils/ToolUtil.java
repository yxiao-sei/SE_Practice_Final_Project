package com.cloume.ecnu.sei.app.utils;



import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ToolUtil {
    public static String mapToString(Map<Integer, String> map) {
        StringBuilder result = new StringBuilder();
        if (map == null || map.size() == 0) {
            return result.toString();
        }
        for (Map.Entry<Integer, String> tem : map.entrySet()) {
            result.append(String.format("不符合的条件%d：%s", tem.getKey(), tem.getValue()));
        }
        return result.toString();
    }

    /**
     * 解析Excel文件格式是否正确
     *
     * @param fileName
     * @return
     */
    public static boolean isXls(String fileName) {
        // (?i)忽略大小写
        if (fileName.matches("^.+\\.(?i)(xls)$")) {
            return true;
        } else if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
            return false;
        } else {
            throw new RuntimeException("格式不对");
        }
    }
}
