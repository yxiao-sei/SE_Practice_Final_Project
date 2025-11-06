package com.cloume.ecnu.sei.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * @author yxiao
 * @date 2020-05-09
 * @description 基础类，所有实体类都将继承此类
 */
@Data
@MappedSuperclass
public class BaseResource implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 创建时间
     */
    private Long createdTime = System.currentTimeMillis();

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建人名称
     */
    private String creatorName;

    /**
     * 最近一次更新时间
     */
    private Long lastUpdateTime = System.currentTimeMillis();

    /**
     * 最近一次更新操作人
     */
    private String lastUpdater;

    /**
     * 最近一次更新操作人姓名
     */
    private String lastUpdaterName;

    /**
     * 是否被移除
     */
//    @JsonIgnore
    private Boolean isRemoved = false;
}
