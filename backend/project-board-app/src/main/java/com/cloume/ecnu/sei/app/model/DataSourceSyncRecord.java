package com.cloume.ecnu.sei.app.model;

import com.cloume.ecnu.sei.app.utils.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author yxiao
 * @date 2020-05-09
 * @description 外部数据源同步记录
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "data_source_sync_record")
public class DataSourceSyncRecord extends BaseResource {

    public DataSourceSyncRecord(DataSourceSyncTask dataSourceSyncTask) {
        this.beginTime = System.currentTimeMillis();
        this.taskId = String.valueOf(dataSourceSyncTask.getId());
        this.setCreator(dataSourceSyncTask.getCreator());
    }

    /**
     * 外部数据源类型
     */
    private String dataSourceType = "";

    /**
     * 外部数据源类型名
     */
    private String dataSourceTypeName = "";

    /**
     * 关联的外部数据源id
     */
    private Integer outerDataSource;

    /**
     * 任务同步状态
     */
    private String result = Constant.SyncRecordConstResult.INITIAL;

    /**
     * 同步开始时间
     */
    private Long beginTime;

    /**
     * 同步结束时间
     */
    private Long endTime;

    /**
     * 本次同步任务涉及的数据总量
     */
    private Long totalRecordNumber = 0L;

    /**
     * 已同步数据数量/页码
     * FIXME:如果是分页的页码，没有记录每页的条数！
     */
    private Long progressedRecordNumber = 0L;

    /**
     * 备注信息
     */
    private String comment = "";

    /**
     * 同步记录关联的同步任务id
     */
    private String taskId = "";

    /**
     * excel导入时的文件名
     */
    private String outerDataSourceFileName = "";
}
