package com.cloume.ecnu.sei.app.controller;

import com.cloume.commons.rest.response.RestResponse;
import com.cloume.ecnu.sei.app.service.ExternalDataService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 信息办数据
 */
@RestController
@RequestMapping("/app/externalData")
@Log4j2
public class ExternalDataController {
    @Autowired
    private ExternalDataService externalDataService;

    @Value("${interface.url}")
    private String urlHead;

    @Value("${interface.organization}")
    private String organizationUrl;

    @Value("${interface.staff}")
    private String staffUrl;

    @Value("${interface.student}")
    private String studentUrl;

    @Value("${interface.staffAppointment}")
    private String staffAppointment;

    @Value("${interface.staffStudyExperience}")
    private String staffStudyExperience;

    @Value("${interface.personnelTalents}")
    private String personnelTalents;

    /**
     * 组织机构
     *
     * @return
     */
    @GetMapping("/organization")
    public RestResponse<?> getOrganizationList(@RequestParam(required = false, defaultValue = "") String departmentId,
                                               @RequestParam(required = false, defaultValue = "0") String pageNum,
                                               @RequestParam(required = false, defaultValue = "10") String pageSize) {
        String param = String.format("pageNum=%s&pageSize=%s&departmentId=%s", Integer.valueOf(pageNum) + 1, pageSize, departmentId);
        String urlGet = urlHead + organizationUrl + param;
        Object result = externalDataService.getDataList(urlGet);
        return RestResponse.good(result);
    }

    /**
     * 教职工基本信息
     * @param number
     * @return
     */
    @GetMapping("/staff")
    public RestResponse<?> getStaffList(@RequestParam(required = false, defaultValue = "") String number) {
        String urlGet = urlHead + staffUrl + number;
        Object staff = externalDataService.getDataList(urlGet);
        return RestResponse.good(staff);
    }

    @GetMapping("/personnelTalents")
    public RestResponse<?> getPersonnelTalents(@RequestParam(required = false, defaultValue = "") String number) {
        String urlGet = urlHead + personnelTalents;
        Object staff = externalDataService.getDataList(urlGet);
        return RestResponse.good(staff);
    }

    /**
     * 学生基本信息
     *
     * @param number
     * @return
     */
    @GetMapping("/student")
    public RestResponse<?> getStudentList(@RequestParam(required = false, defaultValue = "") String number) {
        String urlGet = urlHead + studentUrl + number;
        Object staff = externalDataService.getDataList(urlGet);
        return RestResponse.good(staff);
    }
}
