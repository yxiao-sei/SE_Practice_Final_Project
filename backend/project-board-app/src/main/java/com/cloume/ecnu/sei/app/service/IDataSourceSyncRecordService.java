package com.cloume.ecnu.sei.app.service;

import com.cloume.ecnu.sei.app.model.DataSourceSyncRecord;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IDataSourceSyncRecordService extends IBaseService<DataSourceSyncRecord> {
    Page<DataSourceSyncRecord> list(List<String> username, String outerDataSourceId, String type, String dataSourceTypeName, String createdTime, int page, int size);
}
