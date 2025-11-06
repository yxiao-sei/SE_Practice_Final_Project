package com.cloume.ecnu.sei.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 项目
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "projects")
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "update projects set is_removed = true where id = ?")
@Where(clause = "is_removed <> true")
public class Project {
    /**
     * 办内编号
     */
    private String code;
    /**
     * 项目名称
     */
    private String name;
    /**
     * 校内责任单位
     */
    private String responsibleDepartment;
    /**
     * 供应商
     */
    private String supplier;
    /**
     * 是否已列入预算
     */
    private Boolean isBudgetProject;
    /**
     * 预算项目
     */
    private String budgetProjectName;
    /**
     * 预算额度
     */
    private BigDecimal budgetAmount;
    /**
     * 立项时间
     */
    private Date setupTime;
    /**
     * 经费编号
     */
    private String fundingNumber;
    /**
     * 结余金额
     */
    private BigDecimal balanceAmount;

    /**
     * 项目简介
     */
    private String details;
    /**
     * 立项金额
     */
    private BigDecimal setupAmount;
    /**
     * 申购渠道
     */
    private String purchaseChannel;
    /**
     * 询价情况
     */
    private String priceCompareRecord;
    /**
     * 经费名称
     */
    private String fundingProjectName;
    /**
     * 申购结果
     */
    private String purchaseResult;

    //region 实体固有字段
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 创建时间
     */
    @CreatedDate
    @Column(updatable = false)
    private Timestamp createdAt;
    /**
     * 修改时间
     */
    @LastModifiedDate
    private Timestamp updatedAt;
    /**
     * 公开时间
     */
    @CreatedDate
    private Timestamp publishedAt;
    /**
     * 创建者ID
     */
//    @CreatedBy
    @Column(updatable = false)
    private Integer createdById;
    /**
     * 更新者ID
     */
//    @LastModifiedBy
    private Integer updatedById;
    /**
     * 创建者名称
     */
    private String creatorName;
    /**
     * 创建者工号
     */
    private String creatorNumber;
    /**
     * 是否被删除
     */
    private Boolean isRemoved;
    //endregion
}
