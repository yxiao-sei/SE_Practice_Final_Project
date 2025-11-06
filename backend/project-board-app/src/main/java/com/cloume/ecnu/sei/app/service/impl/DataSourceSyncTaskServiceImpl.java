package com.cloume.ecnu.sei.app.service.impl;

import com.cloume.ecnu.sei.app.model.DataSourceSyncTask;
import com.cloume.ecnu.sei.app.repository.DataSourceSyncTaskRepository;
import com.cloume.ecnu.sei.app.service.DataSourceSyncTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

/**
 * @author gfq
 * @description: TODO
 * @date 2024年07月03日
 */
@Service
@Slf4j
public class DataSourceSyncTaskServiceImpl implements DataSourceSyncTaskService {

    @Autowired
    DataSourceSyncTaskRepository dataSourceSyncTaskRepository;

    @Override
    public DataSourceSyncTask save(DataSourceSyncTask dataSourceSyncTask) {
        return dataSourceSyncTaskRepository.save(dataSourceSyncTask);
    }

    public DataSourceSyncTask find(Integer taskId) {
        try {
            Optional<DataSourceSyncTask> optionalTask = dataSourceSyncTaskRepository.findById(taskId);
            if (optionalTask.isPresent()) {
                return optionalTask.get();
            } else {
                throw new EntityNotFoundException("DataSourceSyncTask with id " + taskId + " not found.");
            }
        } catch (Exception e) {
            log.error("Failed to fetch DataSourceSyncTask with id {}: {}", taskId, e.getMessage());
            throw new RuntimeException("An error occurred while fetching the task.", e);
        }
    }
}
