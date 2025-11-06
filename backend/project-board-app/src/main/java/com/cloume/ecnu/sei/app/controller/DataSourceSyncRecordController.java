package com.cloume.ecnu.sei.app.controller;

import com.cloume.commons.rest.response.PagingRestResponse;
import com.cloume.commons.rest.response.RestResponse;

import com.cloume.ecnu.sei.app.model.DataSourceSyncRecord;
import com.cloume.ecnu.sei.app.service.IDataSourceSyncRecordService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;

/**
 * 数据导入记录
 */
@Log4j2
@RestController
@RequestMapping("/app/dataSourceSyncRecord")
public class DataSourceSyncRecordController {

    @Autowired
    private IDataSourceSyncRecordService dataSourceSyncRecordService;

    @DeleteMapping("/{id}")
    public RestResponse<?> delete(Principal principal, @PathVariable Integer id) {
        DataSourceSyncRecord dataSourceSyncRecord = dataSourceSyncRecordService.find(id);
        if (dataSourceSyncRecord == null || dataSourceSyncRecord.getIsRemoved()) {
            return RestResponse.bad(-1, "can not find teacher by id : " + id);
        }
        log.info("删除数据导入记录，id：{}，操作人：{}", id, principal.getName());

        dataSourceSyncRecordService.delete(id);
        return RestResponse.good("ok");
    }

    /**
     * 根据外部数据源id筛选
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    public PagingRestResponse<?> list(Principal principal,
                                      @RequestParam(defaultValue = "") String username,
                                      @RequestParam(defaultValue = "") String outerDataSourceId,
                                      @RequestParam(defaultValue = "") String type,
                                      @RequestParam(defaultValue = "") String dataSourceTypeName,
                                      @RequestParam(defaultValue = "") String createdTime,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int pageSize) {

        Page<DataSourceSyncRecord> result = dataSourceSyncRecordService.list(Arrays.asList(username), outerDataSourceId, type, dataSourceTypeName, createdTime, page == 0 ? page : page - 1, pageSize);
        return PagingRestResponse.good(result.getContent(), result.getTotalElements());
    }
}
