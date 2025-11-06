package com.cloume.ecnu.sei.app.enums;

public enum FileTypeEnum {
    DEFAULT(1, "默认"),
    //班车预约
    BUS_RESERVATION(2, "班车预约");

    public int code;

    public String type;

    FileTypeEnum(int code, String type) {
        this.code = code;
        this.type = type;
    }

    public static FileTypeEnum getByCode(int code) {
        for (FileTypeEnum typeEnum : FileTypeEnum.values()) {
            if (typeEnum.code == code) {
                return typeEnum;
            }
        }
        return FileTypeEnum.DEFAULT;
    }

    public static FileTypeEnum getByType(String type) {
        for (FileTypeEnum typeEnum : FileTypeEnum.values()) {
            if (typeEnum.type.equals(type)) {
                return typeEnum;
            }
        }
        return FileTypeEnum.DEFAULT;
    }
}
