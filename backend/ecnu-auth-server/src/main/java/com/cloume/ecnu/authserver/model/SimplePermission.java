package com.cloume.ecnu.authserver.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

/**
 * @author yxiao
 */
@Data
@Entity
@NoArgsConstructor
public class SimplePermission extends BaseResource {

    /**
     * 资源id
     */
    private String resourceId;

    /**
     * 资源名
     */
    private String resourceName;

    /**
     * 权限操作
     */
    private String privilege;

    /**
     * 权限操作描述
     */
    private String privilegeDescription;
}
