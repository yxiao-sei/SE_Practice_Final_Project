package com.cloume.ecnu.sei.app.utils;

/**
 * @author yxiao
 * @date
 * @description
 */
public class Constant {

    public final static class InterfaceType{
        public static final String GET = "GET";
        public static final String POST = "POST";
        public static final String PUT = "PUT";
        public static final String DELETE = "DELETE";
    }

    public final static class SyncTaskConstStatus {
        public static final String SUCCESS = "success"; //任务已完成
        public static final String ABORTED = "aborted"; //任务已终止
        public static final String PROGRESS = "progress"; //任务执行中
        public static final String INITIAL = "initial"; //任务初始化
        public static final String ERROR = "error"; //任务执行出错
    }
    public final static class SyncRecordConstResult {
        public static final String COMPLETED = "completed"; //任务已完成
        public static final String ABORTED = "aborted"; //任务已终止
        public static final String PROGRESS = "progress"; //任务执行中
        public static final String INITIAL = "initial"; //任务初始化
        public static final String ERROR = "error"; //任务执行出错
        public static final String NONEW = "nonew"; //任务未获取到数据
    }
    public static final class AuthenticationType {
        public static final String USERNAME_PASSWORD = "USERNAME_PASSWORD";
        public static final String CORP_WECHAT = "CORP_WECHAT";
        public static final String CAS_REDIRECT = "CAS_REDIRECT";

        public AuthenticationType() {
        }
    }

    public static final class USER_ROLE {
        public static final String ROLE_INSTRUCTOR = "ROLE_INSTRUCTOR";
        public static final String ROLE_NON_NATIONAL_STUDENT_INSTRUCTOR = "ROLE_NON_NATIONAL_STUDENT_MANAGER";
        public static final String ROLE_STUDENT = "ROLE_STUDENT";
        public static final String ROLE_DEPARTMENT_ADMIN = "ROLE_DEPARTMENT_ADMIN";
        public static final String ROLE_DEPARTMENT_READ = "ROLE_DEPARTMENT_READ";
        public static final String ROLE_XGB_SUPER_ADMIN = "ROLE_XGB_SUPER_ADMIN";
        public static final String ROLE_DEPARTMENT_FUNDING_CENTER_ADMIN = "ROLE_DEPARTMENT_FUNDING_CENTER_ADMIN";
        public static final String ROLE_INSTRUCTOR_FUNDING_CENTER = "ROLE_INSTRUCTOR_FUNDING_CENTER";
        public static final String ROLE_XGB_FUNDING_CENTER_ADMIN = "ROLE_XGB_FUNDING_CENTER_ADMIN";
        public static final String ROLE_STUDENT_FUNDING_CENTER = "ROLE_STUDENT_FUNDING_CENTER";
        public static final String ROLE_CROSS_DEPARTMENT_MANAGEMENT = "ROLE_CROSS_DEPARTMENT_MANAGEMENT";
        public static final String ROLE_TUTOR = "ROLE_TUTOR";
        public static final String ROLE_RAS_TUTOR = "ROLE_RAS_TUTOR";
        public static final String ROLE_RAS_DEPARTMENT_ADMIN = "ROLE_RAS_DEPARTMENT_ADMIN";
        public static final String ROLE_XGB_RAS_ADMIN = "ROLE_XGB_RAS_ADMIN";
        public static final String ROLE_RAS_DEPARTMENT_ASSISTANT = "ROLE_RAS_DEPARTMENT_ASSISTANT";
        public static final String ROLE_XGB_SCA_ADMIN = "ROLE_XGB_SCA_ADMIN";
        public static final String ROLE_DEPARTMENT_SCA_ADMIN = "ROLE_DEPARTMENT_SCA_ADMIN";
        public static final String ROLE_SCA_INSTRUCTOR = "ROLE_SCA_INSTRUCTOR";
        public static final String ROLE_SCA_COLLEGE_ADMIN = "ROLE_SCA_COLLEGE_ADMIN";
        public static final String ROLE_SCA_COLLEGE_ORGANIZER = "ROLE_SCA_COLLEGE_ORGANIZER";
        public static final String ROLE_SCA_ACTIVITY_ADMIN = "ROLE_SCA_ACTIVITY_ADMIN";
        public static final String ROLE_SCA_STUDENT = "ROLE_SCA_STUDENT";
        public static final String ROLE_SAMS_XGB_ADMIN = "ROLE_SAMS_XGB_ADMIN";
        public static final String ROLE_SAMS_PERSONNEL_ADMIN = "ROLE_SAMS_PERSONNEL_ADMIN";
        public static final String ROLE_SAMS_ORGANIZATION_PERSONNEL_ADMIN = "ROLE_SAMS_ORGANIZATION_PERSONNEL_ADMIN";
        public static final String ROLE_SAMS_ORGANIZATION_ASSISTANT_ADMIN = "ROLE_SAMS_ORGANIZATION_ASSISTANT_ADMIN";
        public static final String ROLE_SAMS_STUDENT = "ROLE_SAMS_STUDENT";
        public static final String ROLE_ZIZHU_XGB_ADMIN = "ROLE_ZIZHU_XGB_ADMIN";
        public static final String ROLE_ZIZHU_DEPARTMENT_ADMIN = "ROLE_ZIZHU_DEPARTMENT_ADMIN";
        public static final String ROLE_ZIZHU_INSTRUCTOR = "ROLE_ZIZHU_INSTRUCTOR";
        public static final String ROLE_ZIZHU_STUDENT = "ROLE_ZIZHU_STUDENT";
        public static final String ROLE_ZXS_DD_ADMIN = "ROLE_ZXS_DD_ADMIN";

        public USER_ROLE() {
        }
    }
}
