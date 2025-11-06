package com.cloume.ecnu.sei.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.cloume.commons.rest.response.RestResponse;
import com.cloume.commons.utils.MapBuilder;
import com.cloume.ecnu.sei.app.utils.CommonRestfulApiUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/app/audit-record")
public class AuditRecordController {

    @Resource
    private CommonRestfulApiUtils commonRestfulApiUtils;

    @Value("${url.strapi}")
    private String strapiBaseUrl;
    @PostMapping("/batch")
    public RestResponse<?> batchCreateAuditRecord(@RequestBody Map<String, Object> body) {
        String url = strapiBaseUrl + "/api/audit-records";
        String request = "POST";
        String token = "";
        String auditObjectType = body.getOrDefault("auditObjectType", "contract").toString();
        Integer auditObjectId = Integer.valueOf(body.getOrDefault("auditObjectId", "0").toString());
        List<Map<String, String>> auditors = (List<Map<String, String>>)body.get("auditors");
        List<Map<String, String>> auditorCopy = (List<Map<String, String>>)body.get("auditorCopy");
        List<String> currentAuditorNumbers = new ArrayList<>();
        List<String> currentCopyAuditorNumbers = new ArrayList<>();
        auditors.forEach(auditor -> {
            try {
                JSONObject auditRecord = new JSONObject();
                auditRecord.put("auditObjectType", auditObjectType);
                auditRecord.put("auditObjectId", auditObjectId);
                auditRecord.put("auditMethod", "审批");
                auditRecord.put("auditorNumber", auditor.get("number"));
                auditRecord.put("auditorName", auditor.get("name"));
                JSONObject data = new JSONObject();
                data.put("data", auditRecord);
                Map<String, Object> res = commonRestfulApiUtils.callInterface(request, url, data, token);
                log.info("add audit record: {}", auditRecord);
                currentAuditorNumbers.add(auditor.get("number"));
            } catch (Exception e) {
                log.error("add audit record error: {}", auditor);
            }
        });
        auditorCopy.forEach(auditor -> {
            try {
                JSONObject auditRecord = new JSONObject();
                auditRecord.put("auditObjectType", auditObjectType);
                auditRecord.put("auditObjectId", auditObjectId);
                auditRecord.put("auditMethod", "阅知");
                auditRecord.put("auditorNumber", auditor.get("number"));
                auditRecord.put("auditorName", auditor.get("name"));
                JSONObject data = new JSONObject();
                data.put("data", auditRecord);
                Map<String, Object> res = commonRestfulApiUtils.callInterface(request, url, data, token);
                log.info("add audit record: {}", auditRecord);
                currentCopyAuditorNumbers.add(auditor.get("number"));
            } catch (Exception e) {
                log.error("add audit record error: {}", auditor);
            }
        });
        return RestResponse.good(MapBuilder.begin("currentAuditors", String.join(",", currentAuditorNumbers)).and("currentAuditorCopy", String.join(",", currentCopyAuditorNumbers)).build());
    }

}
