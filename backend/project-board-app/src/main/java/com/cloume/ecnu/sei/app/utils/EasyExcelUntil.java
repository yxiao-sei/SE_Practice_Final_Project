package com.cloume.ecnu.sei.app.utils;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.cloume.ecnu.sei.app.factory.FileHandlerFactory;
import com.cloume.ecnu.sei.app.handler.FileTypeHandler;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author: liu yue
 * @date: 2023/11/23 14:24
 * @Descirption:
 * @version:1.0
 */
@Component
public class EasyExcelUntil {
    @Resource
    private FileHandlerFactory fileHandlerFactory;


    public static List<List<String>> readExcel(MultipartFile file) {
        FileInputStream inputStream = null;
        try {
            inputStream = (FileInputStream) file.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringExcelListener listener = new StringExcelListener();
        ExcelReader excelReader = EasyExcelFactory.read(inputStream, null, listener).headRowNumber(0).build();
        excelReader.read();
        List<List<String>> datas = listener.getDatas();
        excelReader.finish();
        return datas;

    }

    /**
     * 根据标题和数据动态生成excel
     *
     * @param
     * @param data
     * @return
     */
    public static void writeExcel(List<List<Object>> data, String sheetName, HttpServletResponse response) {
        XSSFWorkbook workBook = new XSSFWorkbook();
        // 创建sheet对象
        XSSFSheet sheet = workBook.createSheet(sheetName);
        XSSFCellStyle cellStyle = workBook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        Integer max = 0;
        CellStyle titleStyle = workBook.createCellStyle();
        XSSFFont font = workBook.createFont();
        font.setFontHeightInPoints((short) 20); //字体高度
        font.setColor(HSSFFont.COLOR_RED); //字体颜色
        font.setFontName("黑体");
        font.setBold(true); //
        titleStyle.setFont(font);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        for (int i = 0; i < data.size(); i++) { //遍历数据外层集合 数据条数
            XSSFRow row = sheet.createRow(i);  //行数
            for (int j = 0; j < data.get(i).size(); j++) { //遍历数据内层集合 数据
                String value = String.valueOf(data.get(i).get(j)); //每个数据
                row.createCell(j).setCellValue(value); //将数据添加到这行的第几个单元格中
                max = j > max ? j : max;
            }

            sheet.getRow(i).setRowStyle(cellStyle);
            if (i == 0) { //获取所有标题
                sheet.getRow(i).setRowStyle(titleStyle);
            }
        }
        //设置自动列宽
        for (int i = 0; i < max; i++) {
            sheet.autoSizeColumn(i);
        }
        try {
            workBook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<List<Object>> merge(List<String> titles, List<List<Object>> data) {
        List<List<Object>> finalData = new ArrayList<>();
        List<Object> temps = new ArrayList<>();
        for (int i = 0; i < titles.size(); i++) {
            temps.add(titles.get(i));
        }
        finalData.add(0, temps);
        finalData.addAll(data);
        return finalData;
    }


    public static void writeExcel(HashMap<String,List<List<Object>>> dataMap, HttpServletResponse response) {
        XSSFWorkbook workBook = new XSSFWorkbook();

        for (String sheetName : dataMap.keySet()) {
            List<List<Object>> data = dataMap.get(sheetName);
            if (data==null){
                continue;
            }
            // 创建sheet对象
            XSSFSheet sheet = workBook.createSheet(sheetName);
            XSSFCellStyle cellStyle = workBook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            Integer max = 0;
            CellStyle titleStyle = workBook.createCellStyle();
            XSSFFont font = workBook.createFont();
            font.setFontHeightInPoints((short) 20); //字体高度
            font.setColor(HSSFFont.COLOR_RED); //字体颜色
            font.setFontName("黑体");
            font.setBold(true); //
            titleStyle.setFont(font);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            for (int i = 0; i < data.size(); i++) {
                XSSFRow row = sheet.createRow(i);
                for (int j = 0; j < data.get(i).size(); j++) {
                    String value = String.valueOf(data.get(i).get(j));
                    row.createCell(j).setCellValue(value);
                    max = j > max ? j : max;
                }

                sheet.getRow(i).setRowStyle(cellStyle);
                if (i == 0) {
                    sheet.getRow(i).setRowStyle(titleStyle);
                }
            }
            //设置自动列宽
            for (int i = 0; i < max; i++) {
                sheet.autoSizeColumn(i);
            }
        }

        try {
            workBook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static HashMap<String, List<List<String>>> readExcelInBatch(MultipartFile file) {
        HashMap<String, List<List<String>>> result = new HashMap<>();
        try {
            FileInputStream inputStream = null;
            inputStream = (FileInputStream) file.getInputStream();
            ZipSecureFile.setMinInflateRatio(-1.0d);
            XSSFWorkbook sheets = new XSSFWorkbook(inputStream);
            for (int k = 0; k < sheets.getNumberOfSheets(); k++) {
                XSSFSheet workSheet = sheets.getSheetAt(k);
                boolean hidden = sheets.isSheetHidden(k);
                if (hidden) {
                    System.out.println(workSheet.getSheetName() + "is hidden! program will be not read it ! ");
                    continue;
                }
                List<List<String>> sheet = new ArrayList<>();
                for (int i = 0; i <= workSheet.getLastRowNum(); i++) {
                    List<String> list = new ArrayList<>();
                    for (int j = 0; workSheet.getRow(i) != null && j < workSheet.getRow(i).getLastCellNum(); j++) {
                        XSSFCell cell = workSheet.getRow(i).getCell(j);
                        String value =  getCellValue(cell);
                        list.add(value);
                    }
                    sheet.add(list);
                }
                result.put(workSheet.getSheetName(), sheet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    private static String getCellValue(XSSFCell cell ) {
        if (cell == null) {
            return "";
        }

        if (cell.getCellTypeEnum().equals(CellType.FORMULA)) {
            return String.valueOf(cell.getNumericCellValue());
        }

        if (cell.getCellTypeEnum().equals(CellType.STRING)) {
            return cell.getStringCellValue();
        }
        if (cell.getCellTypeEnum().equals(CellType.NUMERIC)) {
            return cell.getRawValue();
        }
//
//        if (cell.getCellTypeEnum().equals(CellType.BOOLEAN)) {
//            return cell.getBooleanCellValue()? "TRUE" : "FALSE";
//        }
        return cell.toString();
    }

    /**
     * 根据标题和数据动态生成excel
     *
     * @param
     * @param data
     * @return
     */
    public void writeExcel(List<List<Object>> data, String sheetName, HttpServletResponse response, Integer exportType) {
        FileTypeHandler handler = fileHandlerFactory.getHandler(exportType);

        XSSFWorkbook workBook = new XSSFWorkbook();
        // 创建sheet对象
        XSSFSheet sheet = workBook.createSheet(sheetName);
        XSSFCellStyle cellStyle = workBook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        Integer max = 0;
        CellStyle titleStyle = workBook.createCellStyle();
        XSSFFont font = workBook.createFont();
        font.setFontHeightInPoints((short) 20); //字体高度
        font.setColor(HSSFFont.COLOR_RED); //字体颜色
        font.setFontName("黑体");
        font.setBold(true); //
        titleStyle.setFont(font);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        for (int i = 0; i < data.size(); i++) { //遍历数据外层集合 数据条数
            XSSFRow row = sheet.createRow(i); //行数
            for (int j = 0; j < data.get(i).size(); j++) { //遍历数据内层集合 数据
                String value = String.valueOf(data.get(i).get(j)); //每个数据
                row.createCell(j).setCellValue(value); //将数据添加到这行的第几个单元格中
                max = j > max ? j : max;
            }

            sheet.getRow(i).setRowStyle(cellStyle);
            if (i == 0) { //获取所有标题
                sheet.getRow(i).setRowStyle(titleStyle);
            }
        }
        // 优先自动列宽，针对不同导入导出优化
        for (int i = 0; i < max; i++) {
            sheet.autoSizeColumn(i);
        }
        handler.setColumnWidth(sheet);
        try {
            workBook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Double getDoubleByString(String data) {
        try {
            if ("".equals(data)){
                return  null;
            }
            Double result = Double.valueOf(data);
            return result;
        } catch (Exception e) {
            return null;
        }

    }

}
