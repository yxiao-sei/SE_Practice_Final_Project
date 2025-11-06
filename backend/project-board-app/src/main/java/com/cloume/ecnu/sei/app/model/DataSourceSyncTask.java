package com.cloume.ecnu.sei.app.model;

import com.cloume.ecnu.sei.app.utils.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author yxiao
 * @date 2020-05-09
 * @description 外部数据源同步任务
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "data_source_sync_task")
public class DataSourceSyncTask extends BaseResource {

    private String fileName;
    /**
     * 任务同步状态
     */
    private String status = Constant.SyncTaskConstStatus.INITIAL;

    /**
     * 如果报错，此处存放报错信息
     */
    private String errorMessage = "";
}
