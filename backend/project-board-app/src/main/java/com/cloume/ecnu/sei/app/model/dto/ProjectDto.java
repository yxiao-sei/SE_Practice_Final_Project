package com.cloume.ecnu.sei.app.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.cloume.ecnu.sei.app.model.Project;
import com.cloume.ecnu.sei.app.utils.BooleanToStringConverter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectDto {
    @ExcelProperty("项目名称")
    private String name;
    @ExcelProperty("办内编号")
    private String code;
    @ExcelProperty("校内责任单位")
    private String responsibleDepartment;
    @ExcelProperty("供应商")
    private String supplier;
    @ExcelProperty(value = "是否已列入预算", converter = BooleanToStringConverter.class)
    private Boolean isBudgetProject;
    @ExcelProperty("预算项目")
    private String budgetProjectName;
    @ExcelProperty("预算额度")
    private BigDecimal budgetAmount;
    @ExcelProperty("立项时间")
    private Date setupTime;
    @ExcelProperty("经费编号")
    private String fundingNumber;
    @ExcelProperty("结余金额")
    private BigDecimal balanceAmount;
    @ExcelProperty("项目简介")
    private String details;
    @ExcelProperty("立项金额")
    private BigDecimal setupAmount;
    @ExcelProperty("申购渠道")
    private String purchaseChannel;
    @ExcelProperty("询价情况")
    private String priceCompareRecord;
    @ExcelProperty("经费名称")
    private String fundingProjectName;
    @ExcelProperty("申购结果")
    private String purchaseResult;

    @NotNull
    public Project toModel() {
        return Project.builder()
                .code(code)
                .name(name)
                .responsibleDepartment(responsibleDepartment)
                .supplier(supplier)
                .isBudgetProject(isBudgetProject)
                .budgetProjectName(budgetProjectName)
                .budgetAmount(budgetAmount)
                .setupTime(setupTime)
                .fundingNumber(fundingNumber)
                .balanceAmount(balanceAmount)
                .details(details)
                .setupAmount(setupAmount)
                .purchaseChannel(purchaseChannel)
                .priceCompareRecord(priceCompareRecord)
                .fundingProjectName(fundingProjectName)
                .purchaseResult(purchaseResult)
                .build();
    }

    public static ProjectDto getTemplate() {
        return ProjectDto.builder()
                .code("123")
                .name("车辆安全控制")
                .responsibleDepartment("学工部")
                .supplier("华夏钢铁")
                .isBudgetProject(true)
                .budgetProjectName("一级预算")
                .budgetAmount(new BigDecimal(100000))
                .setupTime(new Date())
                .fundingNumber("123456")
                .balanceAmount(new BigDecimal("1543.26"))
                .details("为保证车辆控制安全而诞生的项目")
                .setupAmount(new BigDecimal(50000))
                .purchaseChannel("学校统一采购")
                .priceCompareRecord("价格合理")
                .fundingProjectName("国家基金")
                .purchaseResult("已申购")
                .build();
    }
}
