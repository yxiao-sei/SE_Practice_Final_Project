package com.cloume.ecnu.sei.app.model.dto;

import com.github.crab2died.annotation.ExcelField;
import lombok.Data;

@Data
public class AllowanceDto {

    public String allowanceGrantRecordId;

    public String allowanceGrantRecordTopic;

    public String allowanceGrantRecordType;

    public String allowanceProjectName;

    public String allowanceProjectNumber;

    public String amount;

    public String comment;

    @ExcelField(title = "学院名称")
    public String departmentName;

    @ExcelField(title = "性别")
    public String gender;

    public String grantMethod;

    public String grantTime;

    @ExcelField(title = "姓名")
    public String staffName;

    @ExcelField(title = "新退休编号")
    public String staffNumber;

    @ExcelField(title = "单位编号")
    public String subDepartmentCode;

    @ExcelField(title = "单位名称")
    public String subDepartmentName;


}
