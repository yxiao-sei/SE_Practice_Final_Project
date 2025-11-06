package com.cloume.ecnu.sei.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.sql.Timestamp;
import java.util.Collection;

/**
 * @Author xiaoyu
 * @Date 2020-06-25
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "custom_userdetails")
public class CustomUserDetails extends BaseResource implements UserDetails {

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private String username;

    @JsonIgnore
    private String password;

    /**
     * 用户在系统中的角色列表，将根据角色对用户操作权限进行限制
     */
    private String roles;

    /**
     * 是否已激活
     */
    private Boolean activated = false;

    /**
     * 账户激活时间
     */
    private Long activateTime;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 账户支持的登录方式:逗号隔开的字符串
     * username-password,corp-wechat,cas-redirect
     * username-password与其他登录方式不同时生效
     */
    private String authenticationMethods;

    /**
     * 用户所在部门：仅用于老龄办/院系管理员
     */
    private String office;

    /**
     * 用户所在部门：仅用于老龄办/院系管理员
     */
    private String subDepartmentCode;

    /**
     * 用户所在部门：仅用于老龄办/院系管理员
     */
    private String subDepartmentName;

    /**
     * 用户所在部门：仅用于老龄办/院系管理员
     */
    private String departmentName;

    /**
     * 用户所在部门：仅用于老龄办/院系管理员
     */
    private String departmentId;

    private String creatorName;
    private Timestamp publishedAt = new Timestamp(System.currentTimeMillis());

    @Transient
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
