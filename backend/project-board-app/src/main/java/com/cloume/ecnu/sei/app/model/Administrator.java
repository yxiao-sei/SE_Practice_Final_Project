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
@Table(name = "administrators")
public class Administrator extends BaseResource {

    private String name;

    private String number;

    private String roles;

    private String state;

    private String departmentName;

    private String departmentCode;
    private String performanceDepartmentName;
    private String buinCode;

    private Boolean disabled = false;

    private String authenticationMethods;
}