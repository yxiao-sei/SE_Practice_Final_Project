package com.cloume.ecnu.sei.app.message;

import com.alibaba.fastjson.JSONObject;
import com.cloume.ecnu.sei.app.config.JsonFileConfig;
import com.cloume.ecnu.sei.app.config.MQMessageSender;
import com.cloume.ecnu.sei.app.model.DataSourceSyncRecord;
import com.cloume.ecnu.sei.app.model.DataSourceSyncTask;
import com.cloume.ecnu.sei.app.service.DataSourceSyncTaskService;
import com.cloume.ecnu.sei.app.service.FileService;
import com.cloume.ecnu.sei.app.service.IDataSourceSyncRecordService;
import com.cloume.ecnu.sei.app.utils.CommonRestfulApiUtils;
import com.cloume.ecnu.sei.app.utils.Constant;
import com.cloume.ecnu.sei.app.utils.ToolUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 导出
 */
@Log4j2
@Component
public class FileExportReceiver {
    @Resource
    private CommonRestfulApiUtils commonRestfulApiUtils;

    @Autowired
    private DataSourceSyncTaskService dataSourceSyncTaskService;

    @Autowired
    private FileService fileService;

    @Value("${url.strapi}")
    private String strapiBaseUrl;

    @Autowired
    private MQMessageSender mqMessageSender;

    @Autowired
    private IDataSourceSyncRecordService dataSourceSyncRecordService;

    @Resource
    private JsonFileConfig jsonFileConfig;

    @RabbitListener(queues = "topic.file_export")
    public void fileExportMessage(String msg) {
        log.info(Thread.currentThread().getName() + " 接收到来自topic.file_export队列的消息：" + msg);
        String convert = "[\n" +
                "  {\n" +
                "    \"key\":\"publishTime\",\n" +
                "    \"value\":\"yyyy-MM-dd HH:mm:ss\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\":\"checkDate\",\n" +
                "    \"value\":\"yyyy-MM-dd HH:mm:ss\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\":\"paperSubmissionTime\",\n" +
                "    \"value\":\"yyyy-MM-dd HH:mm:ss\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\":\"degreeAwardTime\",\n" +
                "    \"value\":\"yyyy-MM-dd HH:mm:ss\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\":\"degreeDate\",\n" +
                "    \"value\":\"yyyy-MM-dd HH:mm:ss\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\":\"applicationTime\",\n" +
                "    \"value\":\"yyyy-MM-dd\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\":\"departureTime\",\n" +
                "    \"value\":\"yyyy-MM-dd\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\":\"returnTime\",\n" +
                "    \"value\":\"yyyy-MM-dd\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\":\"BYRQ\",\n" +
                "    \"value\":\"yyyy-MM-dd\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\":\"registerDate\",\n" +
                "    \"value\":\"yyyy-MM-dd\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\":\"checkoutTime\",\n" +
                "    \"value\":\"yyyy-MM-dd\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\":\"applicationDate\",\n" +
                "    \"value\":\"yyyy-MM-dd\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\":\"approvalDate\",\n" +
                "    \"value\":\"yyyy-MM-dd\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\":\"awardTime\",\n" +
                "    \"value\":\"yyyy-MM-dd HH:mm:ss\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\":\"awardDate\",\n" +
                "    \"value\":\"yyyy-MM\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\":\"expectedGraduationDate\",\n" +
                "    \"value\":\"yyyy-MM-dd\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\":\"operationTime\",\n" +
                "    \"value\":\"yyyy-MM-dd\"\n" +
                "  }\n" +
                "]";
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String userName = jsonObject.getString("userName");
        String realName = jsonObject.getString("realName");
        String fileName = jsonObject.get("fileName").toString() + System.currentTimeMillis();
        String fileNameNew = fileName + ".xls";
        // 需要进行转换时间的字段
        //List<Map<String, Object>> timeConvert = JsonArrayReaderUtils.getInstance(jsonFileConfig.getCodeMapper()).getJsonMapByName("timeConvert");
        List<Map<String,Object>> timeConvert = JSONObject.parseObject(convert,  ArrayList.class);

        DataSourceSyncTask dataSourceSyncTask = dataSourceSyncTaskService.find(jsonObject.getInteger("taskId"));
        try {
            DataSourceSyncRecord dataSourceSyncRecord = new DataSourceSyncRecord(dataSourceSyncTask);
            dataSourceSyncRecord.setCreator(userName);
            dataSourceSyncRecord.setCreatorName(realName);
            dataSourceSyncRecord.setTotalRecordNumber(0L);
            dataSourceSyncRecord.setOuterDataSourceFileName(fileNameNew);
            dataSourceSyncRecord.setDataSourceTypeName(jsonObject.getString("dataSourceTypeName"));
            dataSourceSyncRecord.setBeginTime(System.currentTimeMillis());
            dataSourceSyncRecord = dataSourceSyncRecordService.save(dataSourceSyncRecord);
            //请求方法
            String request = jsonObject.getOrDefault("request", "GET").toString();
            //路由地址
            StringBuilder url = new StringBuilder();
            url.append(strapiBaseUrl);
            url.append(jsonObject.getOrDefault("url", "/api/staffs").toString());
            String params = jsonObject.getOrDefault("params", "").toString();
            if (StringUtils.isNotEmpty(params)) {
                url.append("?");
                url.append(params);
            }
            //token
            String token = jsonObject.getOrDefault("token", "").toString();
            if (StringUtils.isEmpty(url)) {
                return;
            }

            //导出的数据
            Map<String, Object> res = commonRestfulApiUtils.callInterface(request, url.toString(), new HashMap<>(), token);
            String jsonString = res.getOrDefault("data", "").toString();
            ObjectMapper mapper = new ObjectMapper();
            //搜索到收到数据
            List<Map<String, Object>> dataList = mapper.readValue(jsonString, new TypeReference<List<Map<String, Object>>>() {
            });
            //excel数据
            //表头
            List<Map<String, Object>> jsonTitles = (List<Map<String, Object>>) jsonObject.getOrDefault("titles", "");
            JSONObject titleJson = new JSONObject(true);
            jsonTitles.forEach(item -> {
                for (String key : item.keySet()) {
                    titleJson.put(key, item.getOrDefault(key, ""));
                }
            });

            // 创建一个工作薄
            XSSFWorkbook workbook = new XSSFWorkbook();
            // 生成一个表格
            XSSFSheet sheet = workbook.createSheet(fileName);
            //表头
            XSSFRow rowTitle = sheet.createRow(0);
            int i = 0;
            for (String key : titleJson.keySet()) {
                XSSFCell cellTitle = rowTitle.createCell(i);
                XSSFRichTextString textTitle = new XSSFRichTextString(titleJson.get(key).toString());//标题名
                cellTitle.setCellValue(textTitle);
                i++;
            }

            //数据
            int rowIndex = 1;//行
            dataSourceSyncRecord.setTotalRecordNumber(Long.valueOf(dataList.size()));
            dataSourceSyncRecord.setResult(Constant.SyncRecordConstResult.PROGRESS);
            dataSourceSyncRecord = dataSourceSyncRecordService.save(dataSourceSyncRecord);
            //返回的错误信息
            Map<Integer, String> errorMessage = new HashMap<>();
            for (Map<String, Object> item : dataList) {
                dataSourceSyncTask = dataSourceSyncTaskService.find(jsonObject.getInteger("taskId"));
                if (dataSourceSyncTask == null) {
                    throw new Exception();
                }
                if (Constant.SyncTaskConstStatus.ABORTED.equalsIgnoreCase(dataSourceSyncTask.getStatus())) {
                    break;
                }
                Map<String, Object> informationMap = (Map<String, Object>) item.getOrDefault("attributes", "");
                XSSFRow cells = sheet.createRow(rowIndex);
                int cellIndex = 0;//单元格下标
                for (String key : titleJson.keySet()) {

                    XSSFCell cellTitle = cells.createCell(cellIndex);
                    String value = informationMap.getOrDefault(key, "") == null ? "" : informationMap.getOrDefault(key, "").toString();
                    if (StringUtils.isNotEmpty(value)){
                        List<Map<String, Object>> timeFormat = timeConvert.stream().filter(time -> time.get("key").equals(key)).collect(Collectors.toList());
                        if (!timeFormat.isEmpty()) {
                            Map<String, Object> timeFormatMap = timeFormat.get(0);
                            value = timeConvert(timeFormatMap.get("value").toString(), Long.parseLong(value));
                        }
                    }
                    if (StringUtils.isNotEmpty(value) &&
                            (key.equalsIgnoreCase("createdAt") || key.equalsIgnoreCase("updatedAt") || key.equalsIgnoreCase("publishedAt"))) {
                        value = value.substring(0, 18);
                    }

                    XSSFRichTextString textTitle = new XSSFRichTextString(value);
                    cellTitle.setCellValue(textTitle);
                    cellIndex++;
                }
                dataSourceSyncRecord.setResult(Constant.SyncRecordConstResult.PROGRESS);
                dataSourceSyncRecord.setProgressedRecordNumber(Long.valueOf(rowIndex));
                dataSourceSyncRecord = dataSourceSyncRecordService.save(dataSourceSyncRecord);
                rowIndex++;
            }
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                workbook.write(bos);
                byte[] brray = bos.toByteArray();
                InputStream inputStream = new ByteArrayInputStream(brray);
                //上传minio
                fileService.minioUpload(inputStream, fileNameNew);

                workbook.close();// XSSFWorkbook关闭
                bos.flush();// 刷新流
                bos.close();// 关闭流
            } catch (IOException ioException) {
                ioException.printStackTrace();
                log.error("上传minio异常：{}", ioException);
                errorMessage.put(errorMessage.size() + 1, "程序运行错误");
            }
            if (dataSourceSyncRecord.getTotalRecordNumber().equals(dataSourceSyncRecord.getProgressedRecordNumber())) {
                dataSourceSyncRecord.setResult(Constant.SyncRecordConstResult.COMPLETED);
                dataSourceSyncTask.setStatus(Constant.SyncTaskConstStatus.SUCCESS);
            } else {
                dataSourceSyncRecord.setResult(Constant.SyncRecordConstResult.ABORTED);
                dataSourceSyncTask.setStatus(Constant.SyncTaskConstStatus.ABORTED);
            }
            dataSourceSyncRecord.setEndTime(System.currentTimeMillis());
            if (errorMessage.size() != 0) {
                dataSourceSyncRecord.setComment(ToolUtil.mapToString(errorMessage));
            }
            dataSourceSyncRecordService.save(dataSourceSyncRecord);
            dataSourceSyncTaskService.save(dataSourceSyncTask);
        } catch (Exception e) {
            log.error("topic.file_export error: {},{}, {}", e.getCause(), e.getMessage(), e.getStackTrace());
        }
    }


    public  String timeConvert(String pattern, long timeStamp) {
        String time = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            time = sdf.format(new Date(timeStamp));
            System.out.println("Time Stamp: " + timeStamp);
            System.out.println("Converted Time: " + time);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
        return time;
    }
}
