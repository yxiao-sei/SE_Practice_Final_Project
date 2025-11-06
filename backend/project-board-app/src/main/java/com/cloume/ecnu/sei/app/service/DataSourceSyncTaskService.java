package com.cloume.ecnu.sei.app.service;

import com.cloume.ecnu.sei.app.model.DataSourceSyncTask;

public interface DataSourceSyncTaskService {
    DataSourceSyncTask save(DataSourceSyncTask dataSourceSyncTask);

    DataSourceSyncTask find(Integer taskId);
}
