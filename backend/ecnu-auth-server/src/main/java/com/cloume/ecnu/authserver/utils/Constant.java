package com.cloume.ecnu.authserver.utils;

/**
 * @author yjc
 * @Description 常量
 * @date 2021/7/14-11:16
 */
public class Constant {

    public final static class InterfaceType{
        public final static String PRINCIPAL = "PRINCIPAL";//当前登录用户基本信息
        public final static String BASE_INFO = "BASE_INFO";//基本信息
        public final static String PHOTO = "PHOTO";//照片
    }

    public final static class UserRole {
        public static final String ADMINISTRATOR = "administrators";
    }
}
