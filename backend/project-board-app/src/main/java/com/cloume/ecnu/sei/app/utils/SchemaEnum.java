package com.cloume.ecnu.sei.app.utils;

import lombok.Getter;

/**
 * 批量处理功能可选的的schema
 */
@Getter
public enum SchemaEnum {
    PROJECTS("projects");

    private final String value;

    SchemaEnum(String value) {
        this.value = value;
    }
}
