package com.cloume.ecnu.sei.app.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * @author yxiao
 */
@Entity
@Data
@NoArgsConstructor
public class Role extends BaseResource {

    /**
     * 角色英文名，用于权限校验
     */
    private String name;

    /**
     * 角色中文名，用于显示
     */
    private String nickname;

    /**
     * 角色描述信息
     */
    private String description;

    /**
     * 是否为内置
     */
    private boolean builtIn = false;

    /**
     * 角色状态，是否已禁用
     */
    private Boolean banned = false;

    /**
     * 角色可进行的操作列表
     */
    @OneToMany(fetch = FetchType.EAGER)
    private List<SimplePermission> permissions;

    /**
     * Spring Security 4.0以上版本角色都默认以'ROLE_'开头
     * @param name
     */
    public void setName(String name) {
        String PREFIX = "ROLE_";
        if (name.indexOf(PREFIX) == -1) {
            this.name = PREFIX + name;
        } else {
            this.name = name;
        }
    }
}
