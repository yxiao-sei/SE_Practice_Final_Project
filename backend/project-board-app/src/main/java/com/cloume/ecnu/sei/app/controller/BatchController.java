package com.cloume.ecnu.sei.app.controller;

import com.alibaba.excel.EasyExcel;
import com.cloume.commons.rest.response.RestResponse;
import com.cloume.ecnu.sei.app.utils.SchemaEnum;
import com.cloume.ecnu.sei.app.model.Project;
import com.cloume.ecnu.sei.app.model.dto.Creator;
import com.cloume.ecnu.sei.app.model.dto.StrapiData;
import com.cloume.ecnu.sei.app.model.dto.ProjectDto;
import com.cloume.ecnu.sei.app.service.IBaseService;
import com.cloume.ecnu.sei.app.utils.CommonRestfulApiUtils;
import com.cloume.ecnu.sei.app.utils.ExcelImportListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 批量处理模块
 */
@Log4j2
@RestController
@RequestMapping("/app/batch")
public class BatchController {

    @Value("${url.strapi}")
    private String strapiBaseUrl;
    @Resource
    private CommonRestfulApiUtils commonRestfulApiUtils;
    private final IBaseService<Project> projectService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BatchController(IBaseService<Project> projectService) {
        this.projectService = projectService;
    }

    /**
     * 获取操作者信息
     *
     * @param principal 请求自带context
     * @return 操作者信息，包括工号与名称
     */
    @NotNull
    private static Creator getCreator(@NotNull Principal principal) {
        OAuth2Authentication authentication = (OAuth2Authentication) principal;
        OAuth2AuthenticationDetails oauth2Details = (OAuth2AuthenticationDetails) authentication.getDetails();
        Map<String, Object> decodedDetails = (Map<String, Object>) oauth2Details.getDecodedDetails();
        return Creator.builder().number(principal.getName()).name((String) decodedDetails.get("realName")).build();
    }

    /**
     * 获取schema对应的Dto类型
     *
     * @param schemaEnum 请求路径中的schema
     * @return Dto类型
     */
    @NotNull
    private static Class<?> getTableHeadClass(@NotNull SchemaEnum schemaEnum) {
        if (schemaEnum == SchemaEnum.PROJECTS) {
            return ProjectDto.class;
        }
        throw new IllegalArgumentException();
    }

    /**
     * 获取schema对应的模板范例
     *
     * @param schemaEnum 请求路径中的schema
     * @return 范例
     */
    @NotNull
    private static List<Object> getTemplate(@NotNull SchemaEnum schemaEnum) {
        List<Object> list = new ArrayList<>();
        if (schemaEnum == SchemaEnum.PROJECTS) {
            list.add(ProjectDto.getTemplate());
            return list;
        }
        throw new IllegalArgumentException();
    }

    /**
     * 批量导入项目时调用的操作类
     *
     * @param principal 请求者信息
     * @return 操作类
     */
    @NotNull
    private ExcelImportListener<ProjectDto, Project> getProjectListener(Principal principal) {
        return new ExcelImportListener<>(log, projectService, (dto -> {
            val result = dto.toModel();
            val creator = getCreator(principal);
            result.setCreatorNumber(creator.getNumber());
            result.setCreatorName(creator.getName());
            return result;
        }));
    }

    /**
     * 批量导入
     *
     * @param file      excel文件
     * @param schema    需导入的schema
     * @param principal 创建者信息
     * @return 成功导入条目数信息
     */
    @PutMapping("/import/{schema}")
    public RestResponse<?> importData(@RequestParam MultipartFile file, @PathVariable SchemaEnum schema, Principal principal) {
        // 空文件
        if (file == null) {
            return RestResponse.bad(-1, "文件为空，请重新上传");
        }
        String fileName = file.getOriginalFilename();
        // 不是excel
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            return RestResponse.bad(-3, "不支持的文件类型，请勿上传！");
        }
        try {
            //region 获取操作类
            ExcelImportListener<?, ?> listener = null;
            if (Objects.requireNonNull(schema) == SchemaEnum.PROJECTS) {
                listener = getProjectListener(principal);
            }
            if (listener == null) {
                return RestResponse.bad(-2, "导入异常，请联系管理员处理");
            }
            //endregion
            val head = getTableHeadClass(schema);
            EasyExcel.read(file.getInputStream(), head, listener).sheet().doRead();
            // 返回结果
            return RestResponse.good(String.format("成功导入 %d 条数据", listener.getSuccessCount()));
        } catch (Exception e) {
            log.error(e);
            return RestResponse.bad(-2, "导入异常，请联系管理员处理");
        }
    }

    /**
     * 导出模板文件
     *
     * @param httpServletResponse 响应体
     * @param schema              需导出的schema
     */
    @GetMapping("/template/{schema}")
    public void exportTemplate(HttpServletResponse httpServletResponse, @PathVariable SchemaEnum schema) {
        try {
            String fileName = URLEncoder.encode("导入模板", "UTF-8");
            httpServletResponse.setContentType("application/vnd.ms-excel");
            httpServletResponse.setCharacterEncoding("utf-8");
            httpServletResponse.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            val head = getTableHeadClass(schema);
            var template = getTemplate(schema);
            EasyExcel.write(httpServletResponse.getOutputStream(), head)
                    .sheet("模板")
                    .doWrite(template);
        } catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * 导出查询结果
     *
     * @param request  请求体
     * @param response 响应体
     * @param schema   查询的schema
     */
    @GetMapping("/export/{schema}")
    public void exportData(HttpServletRequest request, HttpServletResponse response, @PathVariable("schema") SchemaEnum schema, @RequestParam Map<String, String> fieldsMap) {
        try {
            String fileName = URLEncoder.encode("导出结果", "UTF-8");
            String url = strapiBaseUrl + "/api/" + schema.getValue() + "?" + request.getQueryString();
            // 获取所选展示的列
            val fields = new ArrayList<String>();
            fieldsMap.forEach(((key, value) -> {
                if (key.matches("^fields\\[\\d+]$")) {
                    fields.add(value);
                }
            }));
            // 从strapi导出数据
            Map<String, Object> res = commonRestfulApiUtils.callInterface("GET", url, null, null);
            String jsonString = res.getOrDefault("data", "").toString();
            val typeRef = new TypeReference<List<StrapiData<ProjectDto>>>() {
            };
            val data = objectMapper.readValue(jsonString, typeRef).stream().map(StrapiData::getAttributes).collect(Collectors.toList());
            // 设置响应信息
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            val head = getTableHeadClass(schema);
            var excelWriter = EasyExcel.write(response.getOutputStream(), head);
            // 用户自定义筛选出的列
            if (!fields.isEmpty()) {
                excelWriter = excelWriter.includeColumnFiledNames(fields);
            }
            excelWriter.sheet().doWrite(data);
        } catch (Exception e) {
            log.error(e);
        }
    }
}
