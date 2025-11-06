package com.cloume.ecnu.sei.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.cloume.commons.rest.response.RestResponse;
import com.cloume.commons.utils.MapBuilder;
import com.cloume.ecnu.sei.app.utils.JsonMapReaderUtils;
import com.cloume.ecnu.sei.app.service.BasisInfoCenterService;
import com.cloume.ecnu.sei.app.config.JsonFileConfig;
import com.cloume.ecnu.sei.app.utils.JsonArrayReaderUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

//@Api(tags = "系统配置接口")
@RestController
@RequestMapping("/app/config")
public class ConfigController {
    @Autowired
    private JsonFileConfig jsonFileConfig;

    @Autowired
    private BasisInfoCenterService basisInfoCenterService;

    private static Map<String, Object> jsonConfigMap;

    @Value("${url.phoneUrl}")
    private String phoneUrl;

    @GetMapping("/json")
    public RestResponse<?> getJson() {
        Map<String, Object> result = new HashMap<>();
        if (jsonConfigMap != null) {
            return RestResponse.good(jsonConfigMap);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        JSONObject entityDepartment = new JSONObject(true);
        entityDepartment.put("不限院系", "不限院系");
        entityDepartment.putAll(basisInfoCenterService.getDepartment(null, null));

        result.put("phoneUrl", phoneUrl);
        result.put("campus", Arrays.asList("中山北路校区", "闵行校区", "不限校区"));
        result.put("frequency", MapBuilder.begin("day.1", "每天1次").and("day.2", "每2天1次").and("day.3", "每3天1次")
                .and("day.4", "每4天1次").and("day.5", "每5天1次").and("day.6", "每6天1次").and("day.7", "每7天1次")
                .and("day.14", "每两周1次").and("day.0", "永不推荐").build());
        jsonConfigMap = result;

        return RestResponse.good(result);
    }

    @GetMapping("/role/department")
    public RestResponse<?> getDepartmentRoles() {
        Map<String, Object> result = new HashMap<>();
        //获取学生基本信息中，字段的码值转换
        result.put("roles", JsonArrayReaderUtils.getInstance(jsonFileConfig.getCodeMapper()).getJsonMapByName("departmentRoles"));

        return RestResponse.good(result);
    }

    @GetMapping("/role/office")
    public RestResponse<?> getOfficeRoles() {
        Map<String, Object> result = new HashMap<>();
        //获取学生基本信息中，字段的码值转换
        result.put("roles", JsonArrayReaderUtils.getInstance(jsonFileConfig.getCodeMapper()).getJsonMapByName("officeRoles"));

        return RestResponse.good(result);
    }

    @GetMapping("/role/student")
    public RestResponse<?> getStudentRoles() {
        Map<String, Object> result = new HashMap<>();
        //获取学生基本信息中，字段的码值转换
        result.put("roles", JsonArrayReaderUtils.getInstance(jsonFileConfig.getCodeMapper()).getJsonMapByName("studentRoles"));

        return RestResponse.good(result);
    }

    @GetMapping("/role/teacher")
    public RestResponse<?> getTeacherRoles() {
        Map<String, Object> result = new HashMap<>();
        //获取学生基本信息中，字段的码值转换
        result.put("roles", JsonArrayReaderUtils.getInstance(jsonFileConfig.getCodeMapper()).getJsonMapByName("teacherRoles"));

        return RestResponse.good(result);
    }

    @GetMapping("/role/management")
    public RestResponse<?> getManagementRoles() {
        Map<String, Object> result = new HashMap<>();
        //获取学生基本信息中，字段的码值转换
        result.put("roles", JsonArrayReaderUtils.getInstance(jsonFileConfig.getCodeMapper()).getJsonMapByName("managementRoles"));

        return RestResponse.good(result);
    }

    /**
     * 获取可以导出的字段信息
     *
     * @return
     */
    @GetMapping("/exportConfig")
    public RestResponse<?> getExportFileField(@RequestParam(defaultValue = "") String objectType) {
        Map<String, Object> all = JsonMapReaderUtils.getInstance(jsonFileConfig.getCodeMapper()).getJsonMapByName("exportFields");

        //如果json文件中没有此类的到处字段，则返回错误信息
        if (!all.containsKey(objectType)) {
            return RestResponse.bad(-1, "no data!");
        }
        //如果指定要某个类的导出字段，则导出，没有则返回全部
        if (StringUtils.isNotEmpty(objectType)) {
            return RestResponse.good(all.get(objectType));
        }
        return RestResponse.good(all);
    }
//
//    @GetMapping()
//    @ApiOperation(value = "获取系统配置内容，不传参数则返回所有数据库中的配置项")
//    @ApiImplicitParams(value = {@ApiImplicitParam(name = "parentItem", value = "配置项所属父模块", defaultValue = "", example = "association"),
//            @ApiImplicitParam(name = "item", value = "子配置项", defaultValue = "", example = "currentSchoolYear, currentTerm, registrationPeriod")}
//    )
//    public RestResponse<?> getAllConfiguration(Principal principal,
//                                               @RequestParam(defaultValue = "") String parentItem,
//                                               @RequestParam(defaultValue = "") String item) {
//        return RestResponse.good(systemConfigurationService.find(parentItem, item));
//    }
}
