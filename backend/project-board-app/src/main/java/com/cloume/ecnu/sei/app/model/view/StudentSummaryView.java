package com.cloume.ecnu.sei.app.model.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 学生信息视图
 */


@Entity
@Data
public class StudentSummaryView {

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
     * private String creatorName;
     * <p>
     * /**
     * 最近一次更新时间
     */
    private Long lastUpdateTime = System.currentTimeMillis();

    /**
     * 最近一次更新操作人
     */
    private String lastUpdater;

    /**
     * 是否被移除
     */
    @JsonIgnore
    private Boolean isRemoved = false;


    /**
     * 学生学号
     */
    @Id
    private String number;

    /**
     * 学生姓名
     */
    private String name;

    /**
     * 管理院系
     */
    private String department;

    /**
     * 院系编号
     */
    private String departmentCode;

    /**
     * 所处专业
     */
    private String zy;

    /**
     * 专业号
     */
    private String zyh;

    /**
     * 所处年级
     */
    private String grade;

    /**
     * 学生类型
     */
    private String type;

    /**
     * 政治面貌
     */
    private String ZZMMM = "";

    /**
     * 移动电话
     * (实际界面，联系电话使用这个字段，而不是LXDH）
     */
    private String YDDH = "";

    /**
     * 培养方案Id
     */
    private String cultivatePlanId;

    /**
     * 原培养方案id
     */
    private String originalCultivePlanId;

    /**
     * 培养方案状态(未设置,有待认定活动，转专业新方案待定，完成进度) 状态的判断需要放在drools规则文件中进行
     */
    private String cultivateProgress = "未设置";

    /**
     * 可能存在状态改变的情况;
     */
    private Boolean isChangeState;

    /**
     * 累计活动次数（不赋值）每次显示时计算
     */
    private int activtyCount;

    /**
     * 累计申报考核数（不赋值）每次显示时计算
     */
    private int declareCount;

    /**
     * 完成进度
     */
    private int progress;

    private Boolean isReading;

    /**
     * 辅导员工号
     */
    private String instructorNumbers;

    private String cultivatePlanName;

    private String studentType;

    private String result;
}
