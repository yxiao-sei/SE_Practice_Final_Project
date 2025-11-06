package com.cloume.ecnu.sei.app.repository;

import com.cloume.ecnu.sei.app.model.DataSourceSyncTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "data_source_sync_task")
public interface DataSourceSyncTaskRepository extends JpaSpecificationExecutor<DataSourceSyncTask>, JpaRepository<DataSourceSyncTask, Integer> {
}
