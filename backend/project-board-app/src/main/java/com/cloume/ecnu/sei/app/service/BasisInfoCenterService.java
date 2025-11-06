package com.cloume.ecnu.sei.app.service;

import com.cloume.ecnu.sei.app.config.JsonFileConfig;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class BasisInfoCenterService {

    @Autowired
    private JsonFileConfig jsonFileConfig;

    @Autowired
    private JdbcTemplate sqlJdbcTemplate;

    /**
     * 查询老龄办学生信息
     *
     * @param xh
     * @param entityDepartmentCode
     * @param page
     * @param size
     * @return
     */
    public Map<String, Object> getStudent(String xh, String entityDepartmentCode, int page, int size) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            String sql = "SELECT * FROM `sca_student_view` where 1=1 ";
            if (StringUtils.isNotEmpty(xh)) {
                sql = sql + " and number ='" + xh + "'";
            }
            if (StringUtils.isNotEmpty(entityDepartmentCode)) {
                sql = sql + " and entity_department_code='" + xh + "'";
            }
            List<Map<String, Object>> counts = sqlJdbcTemplate.queryForList(sql);
            resultMap.put("count", counts.size());
            sql = sql + " LIMIT " + page + "," + size;
//            log.info(sql);
            List<Map<String, Object>> result = sqlJdbcTemplate.queryForList(sql);
            resultMap.put("result", result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    /**
     * 获取老龄办实体院系
     *
     * @param departmentName
     * @param departmentCode
     * @param page
     * @param size
     * @return
     */
    public Map<String, Object> getDepartment(String departmentName, String departmentCode, int page, int size) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            String sql = "SELECT * FROM `sca_department_view` where sfst=true";
            if (StringUtils.isNotEmpty(departmentName)) {
                sql = sql + " and department_name like '%" + departmentName + "%'";
            }
            if (StringUtils.isNotEmpty(departmentCode)) {
                sql = sql + " and department_code ='" + departmentCode + "'";
            }
            List<Map<String, Object>> counts = sqlJdbcTemplate.queryForList(sql);
            resultMap.put("count", counts.size());
            sql = sql + " ORDER BY created_time desc LIMIT " + page + "," + size;
//            log.info(sql);
            List<Map<String, Object>> result = sqlJdbcTemplate.queryForList(sql);
            resultMap.put("result", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    /**
     * 获取老龄办实体院系
     *
     * @param departmentName
     * @param departmentCode
     * @return 院系码和院系名，key-value
     */
    public Map<String, Object> getDepartment(String departmentName, String departmentCode) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            String sql = "SELECT department_code,department_name FROM `sca_department_view` where sfst=true";
            if (StringUtils.isNotEmpty(departmentName)) {
                sql = sql + " and department_name like '%" + departmentName + "%'";
            }
            if (StringUtils.isNotEmpty(departmentCode)) {
                sql = sql + " and department_code ='" + departmentCode + "'";
            }
            sql = sql + " ORDER BY created_time desc ";
//            log.info(sql);
            List<Map<String, Object>> result = sqlJdbcTemplate.queryForList(sql);
            result.forEach(department -> {
                resultMap.put(department.get("department_code").toString(), department.get("department_name").toString());
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    /**
     * 查询老龄办院系管理员信息+辅导员信息
     *
     * @param zgh
     * @param entityDepartmentCode
     * @param page
     * @param size
     * @return
     */
    public Map<String, Object> getTeacher(String zgh, String entityDepartmentCode, int page, int size) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            String sql = "SELECT * FROM `sca_administrator_instructor_view` where is_removed=false";
            if (StringUtils.isNotEmpty(zgh)) {
                sql = sql + " and zgh='" + zgh + "'";
            }
            if (StringUtils.isNotEmpty(entityDepartmentCode)) {
                sql = sql + " and entity_department_code='" + entityDepartmentCode + "'";
            }
            List<Map<String, Object>> counts = sqlJdbcTemplate.queryForList(sql);
            resultMap.put("count", counts.size());
            sql = sql + " LIMIT " + page + "," + size;
//            log.info(sql);
            List<Map<String, Object>> result = sqlJdbcTemplate.queryForList(sql);
            resultMap.put("result", result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    /**
     * 获取老龄办所有有效的院系管理员和辅导员
     *
     * @return
     */
    public List<Map<String, Object>> getAdministratorAndInstructor() {
        try {
            String sql = "SELECT * FROM `sca_administrator_instructor_view`";
            List<Map<String, Object>> result = sqlJdbcTemplate.queryForList(sql);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取老龄办的所有专业
     *
     * @return
     */
    public List<Map<String, Object>> getALLXGBZY() {
        try {
            String sql = "SELECT zy,zyh from `sca_student_view` WHERE  !ISNULL(zy) and zy!='' group by zy,zyh";
            List<Map<String, Object>> result = sqlJdbcTemplate.queryForList(sql);
            return result;
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        return null;
    }

    /**
     * 获取奖助学生登记记录，用于同步奖干记录
     *
     * @return
     */
    public List<Map<String, Object>> getApplicationRecord() {
        try {
            String sql = "SELECT * FROM `sca_application_record_view`";
            List<Map<String, Object>> result = sqlJdbcTemplate.queryForList(sql);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
