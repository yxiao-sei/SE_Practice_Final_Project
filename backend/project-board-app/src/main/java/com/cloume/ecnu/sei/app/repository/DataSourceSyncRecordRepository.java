package com.cloume.ecnu.sei.app.repository;


import com.cloume.ecnu.sei.app.model.DataSourceSyncRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataSourceSyncRecordRepository extends JpaRepository<DataSourceSyncRecord, Integer>, JpaSpecificationExecutor<DataSourceSyncRecord> {
}
