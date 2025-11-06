package com.cloume.ecnu.sei.app.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cloume.commons.rest.response.RestResponse;
import com.cloume.ecnu.sei.app.config.MQMessageSender;
import com.cloume.ecnu.sei.app.converter.ExportConverter;
import com.cloume.ecnu.sei.app.model.DataSourceSyncTask;
import com.cloume.ecnu.sei.app.model.dto.AllowanceDto;
import com.cloume.ecnu.sei.app.service.DataSourceSyncTaskService;
import com.cloume.ecnu.sei.app.utils.EasyExcelUntil;
import com.cloume.ecnu.sei.app.utils.CommonRestfulApiUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.crab2died.ExcelUtils;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

@Log4j2
@RestController
@RequestMapping("/app/fileExport")
//@Api(tags = "文件导出接口")
public class FileExportController {
    @Resource
    private CommonRestfulApiUtils commonRestfulApiUtils;
    @Value("${url.strapi}")
    private String strapiBaseUrl;

    @Resource
    private DataSourceSyncTaskService dataSourceSyncTaskService;

    @Autowired
    private MQMessageSender mqMessageSender;

    private <T> List<List<String>> getList(List<Field> exportableFields, List<T> queryList, ExportConverter exportConverter) {
        List<List<String>> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(exportableFields)) {
            return list;
        }
        Object fieldValue;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        for (T item : queryList) {
            List<String> tem = new ArrayList<>();
            for (Field field : exportableFields) {
                try {
                    fieldValue = field.get(item);
                    if (fieldValue == null || StringUtils.isEmpty(fieldValue.toString())) {
                        if (field.getType() == Boolean.class) {
                            tem.add("未知");
                        } else {
                            tem.add("");
                        }
                    } else {
                        if (field.getType() == Boolean.class) {
                            if (Boolean.valueOf(fieldValue.toString()) == true) {
                                tem.add("是");
                            } else if (Boolean.valueOf(fieldValue.toString()) == false) {
                                tem.add("否");
                            }
                        } else if (field.getType() == Long.class) {
                            String formattedTime = simpleDateFormat.format(new Date(Long.valueOf(fieldValue.toString())));
                            //导出时，如果时间为19700101则清空
                            if (StringUtils.isNotEmpty(formattedTime) && formattedTime.equals("19700101")) {
                                tem.add("");
                            } else {
                                tem.add(formattedTime);
                            }
                        } else {
                            if (exportConverter != null) {
                                if (field.getType() == String.class) {
                                    fieldValue = exportConverter.convert(field.getName(), fieldValue.toString());
                                }
                            }
                            if (field.getName().equals("memberState")) {
                                if (fieldValue.toString().equals("enrolled")) {
                                    tem.add("已报名");
                                } else if (fieldValue.toString().equals("in_audit")) {
                                    tem.add("待审核");
                                } else {
                                    tem.add("已拒绝");
                                }
                            } else if (field.getName().equals("isRegister")) {
                                if (fieldValue.toString().equals("registered")) {
                                    tem.add("已签到");
                                } else if (fieldValue.toString().equals("unregistered")) {
                                    tem.add("未签到");
                                } else {
                                    tem.add("无需签到");
                                }
                            } else if (field.getName().equals("progress")) {
                                if (!fieldValue.toString().equals("0")) {
                                    fieldValue = Double.valueOf(fieldValue.toString()) * 0.01;
                                }
                                tem.add(fieldValue.toString());
                            } else {
                                tem.add(fieldValue.toString());
                            }
                        }
                    }
                } catch (Exception e) {
                    tem.add("");
                    e.printStackTrace();
                }
            }
            list.add(tem);
        }
        return list;
    }

//    @PostMapping("/export")
//    public void export(HttpServletResponse response, @RequestBody Map<String, Object> map) throws IOException {
//        //文件名称
//        String fileName = URLEncoder.encode(map.getOrDefault("fileName", "").toString(), "UTF-8");
//        //请求方法
//        String request = map.getOrDefault("request", "GET").toString();
//        //路由地址
//        StringBuilder url = new StringBuilder();
//        url.append(strapiBaseUrl);
//        url.append(map.getOrDefault("url", "/api/staffs").toString());
//        String params = map.getOrDefault("params", "").toString();
//        if (StringUtils.isNotEmpty(params)) {
//            url.append("?");
//            url.append(params);
//        }
//        //token
//        String token = map.getOrDefault("token", "").toString();
//        if (StringUtils.isEmpty(url)) {
//            return;
//        }
//        //表头
//        Set<String> titlesKey = new LinkedHashSet<>();
//        List<Object> titles = new ArrayList<>();
//        List<Map<String, Object>> jsonTitles = (List<Map<String, Object>>) map.getOrDefault("titles", "");
//        Map<String, Object> objectMap = new HashMap<>();
//        jsonTitles.forEach(item -> {
//            for (String key : item.keySet()) {
//                titlesKey.add(key);
//                objectMap.put(key, item.getOrDefault(key, ""));
//            }
//        });
//        titlesKey.forEach(itemKey -> {
//            titles.add(objectMap.getOrDefault(itemKey, ""));
//        });
//
//        //导出的数据
//        Map<String, Object> res = commonRestfulApiUtils.callInterface(request, url.toString(), new HashMap<>(), token);
//        String jsonString = res.getOrDefault("data", "").toString();
//        ObjectMapper mapper = new ObjectMapper();
//        //搜索到收到数据
//        List<Map<String, Object>> dataList = mapper.readValue(jsonString, new TypeReference<List<Map<String, Object>>>() {
//        });
//        //excel数据
//        List<List<Object>> data = new ArrayList<>();
//        data.add(titles);
//        //遍历所有数据
//        for (Map<String, Object> item : dataList) {
//            List<Object> row = new ArrayList<>();
//            Map<String, Object> information = (Map<String, Object>) item.getOrDefault("attributes", "");
//            titlesKey.forEach(itemKey -> {
//                Object cell = information.getOrDefault(itemKey, "");
//                //处理bool数据
//                if (cell instanceof Boolean) {
//                    cell = (Boolean) cell ? "是" : "否";
//                }
//                row.add(cell);
//            });
//            data.add(row);
//        }
//
//        response.setCharacterEncoding("utf-8");
//        response.setContentType("application/vnd.ms-excel");
//
//        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
//        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
//
//        EasyExcelUntil.writeExcel(data, fileName, response);
//    }

    /**
     * 导出
     *
     * @param principal
     * @param map
     * @return
     * @throws IOException
     */
    @PostMapping("/export")
    public RestResponse<?> export(Principal principal, @RequestBody Map<String, Object> map) throws IOException {

        //获取权限
        OAuth2Authentication authentication = (OAuth2Authentication) principal;
        OAuth2AuthenticationDetails oauth2Details = (OAuth2AuthenticationDetails) authentication.getDetails();
        Map<String, Object> decodedDetails = (Map<String, Object>) oauth2Details.getDecodedDetails();
        String realName = decodedDetails.get("realName").toString();

        DataSourceSyncTask dataSourceSyncTask = new DataSourceSyncTask();
        dataSourceSyncTask.setCreator(principal.getName());
        dataSourceSyncTask.setCreatorName(realName);
        dataSourceSyncTask.setFileName(map.get("fileName").toString());
        dataSourceSyncTask = dataSourceSyncTaskService.save(dataSourceSyncTask);

        JSONObject jsonTask = new JSONObject();
        jsonTask.put("taskId", dataSourceSyncTask.getId());
        jsonTask.put("dataSourceTypeName", dataSourceSyncTask.getFileName());
        jsonTask.put("userName", principal.getName());
        jsonTask.put("realName", realName);
        jsonTask.putAll(map);
        mqMessageSender.send(jsonTask.toJSONString(), MQMessageSender.TOPIC_FILE_EXPORT, MQMessageSender.TOPIC_KEY_FILE_EXPORT);

        return RestResponse.good("ok");
    }
    private static Map<String, Object> getStringObjectMap(String attributes) {
        String[] pairs = attributes.split(", ");
        Map<String, Object> information = new HashMap<>();
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            information.put(keyValue[0], keyValue.length > 1 ? keyValue[1] : "");
        }
        return information;
    }

    @GetMapping("/staff-allowance-grant-records/template")
    public void exportInventory(HttpServletResponse httpServletResponse) throws UnsupportedEncodingException {
        String fileName = URLEncoder.encode("补贴导入模板", "UTF-8");
        List<List<Object>> data = new ArrayList<>();
        List<Object> row = new ArrayList<>();
        row.add("学院名称");
        row.add("性别");
        row.add("姓名");
        row.add("新退休编号");
        row.add("单位编号");
        row.add("单位名称");
        data.add(row);
        httpServletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        httpServletResponse.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xlsx");
        EasyExcelUntil.writeExcel(data, "template", httpServletResponse);

    }

    @PutMapping(value = "/staff-allowance-grant-records/upload")
    public RestResponse<?> importAllowance(@RequestParam(value = "file") MultipartFile file,
                                           @RequestParam(value = "allowanceGrantRecordId") String allowanceGrantRecordId,
                                           @RequestParam(value = "allowanceGrantRecordTopic") String allowanceGrantRecordTopic,
                                           @RequestParam(value = "allowanceGrantRecordType") String allowanceGrantRecordType,
                                           @RequestParam(value = "allowanceProjectName") String allowanceProjectName,
                                           @RequestParam(value = "allowanceProjectNumber") String allowanceProjectNumber,
                                           @RequestParam(value = "amount") String amount,
                                           @RequestParam(value = "grantMethod") String grantMethod,
                                           @RequestParam(value = "grantTime") String grantTime) {
        try {
            if (StringUtils.isEmpty(allowanceGrantRecordId)) {
                return RestResponse.bad(-1, "allowanceGrantRecordId is null");
            }
            if (StringUtils.isEmpty(allowanceGrantRecordTopic)) {
                return RestResponse.bad(-1, "allowanceGrantRecordTopic is null");
            }
            if (StringUtils.isEmpty(allowanceGrantRecordType)) {
                return RestResponse.bad(-1, "allowanceGrantRecordType is null");
            }
            if (StringUtils.isEmpty(allowanceProjectName)) {
                return RestResponse.bad(-1, "allowanceProjectName is null");
            }
            if (StringUtils.isEmpty(allowanceProjectNumber)) {
                return RestResponse.bad(-1, "allowanceProjectNumber is null");
            }

            if (StringUtils.isEmpty(amount)) {
                return RestResponse.bad(-1, "amount is null");
            }

            if (StringUtils.isEmpty(grantMethod)) {
                return RestResponse.bad(-1, "grantMethod is null");
            }
            if (StringUtils.isEmpty(grantTime)) {
                return RestResponse.bad(-1, "grantTime is null");
            }
            String url = strapiBaseUrl + "/node-red/strapi/add/staff-allowance-grant-records";
            String request = "POST";
            String token = "";
            List<AllowanceDto> allowanceDtoList = ExcelUtils.getInstance().readExcel2Objects(file.getInputStream(), AllowanceDto.class, 0);
            for (AllowanceDto allowance : allowanceDtoList) {
                allowance.setAllowanceGrantRecordId(allowanceGrantRecordId);
                allowance.setAllowanceGrantRecordTopic(allowanceGrantRecordTopic);
                allowance.setAllowanceGrantRecordType(allowanceGrantRecordType);
                allowance.setAllowanceProjectNumber(allowanceProjectNumber);
                allowance.setAllowanceProjectName(allowanceProjectName);
                allowance.setAmount(amount);
                allowance.setGrantMethod(grantMethod);
                allowance.setGrantTime(grantTime);

                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(allowance);
                JSONObject param = JSON.parseObject(json);
                Map<String, Object> res = commonRestfulApiUtils.callInterface(request, url, param, token);
                log.info("url:{},res:{} requestBody:{}", url, res, param);
            }
            return RestResponse.good("成功更新" + allowanceDtoList.size() + "条数据");

        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.bad(-1, e.getMessage());
        }
    }

}
