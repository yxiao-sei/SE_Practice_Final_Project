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
@Table(name = "staff_update_records")
public class UpdateRecord extends BaseResource {

    private String fieldName;

    private String oldValue;

    private String newValue;

    private String updater;

    private String updateResult;

    private String approvalStatus;

    private String approvalName;

    private Long approvalTime;

    private String field;

    private String model;

    private String modelName;

    private String modelResourceOwner;

    private String modelResourceOwnerName;

    private String oldValueDescription;

    private String newValueDescription;

    private String approvalNumber;

    private String updateName;

    private String modelResourceOwnerId;
}
