package com.cloume.ecnu.authserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 院系白名单配置属性
 */
@Component
@ConfigurationProperties(prefix = "white-list")
public class WhiteListProperties {
    
    private boolean enabled = false;
    private List<String> departments;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getDepartments() {
        return departments;
    }

    public void setDepartments(List<String> departments) {
        this.departments = departments;
    }
}
