package com.cloume.ecnu.sei.app.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author yxiao
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "departments")
public class Department extends BaseResource {

    /**
     * 一级院系部门名称
     */
    private String departmentName;

    private String departmentCode;

    /**
     * 状态
     */
    private String state;
}
