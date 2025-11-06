package com.cloume.ecnu.sei.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.cloume.commons.rest.response.RestResponse;
import com.cloume.commons.utils.MapBuilder;
import com.cloume.ecnu.sei.app.model.Administrator;
import com.cloume.ecnu.sei.app.repository.AdministratorRepository;
import com.cloume.ecnu.sei.app.utils.CommonRestfulApiUtils;
import com.cloume.ecnu.sei.app.utils.Constant;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.loadbalancer.config.LoadBalancerCacheAutoConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.*;

/**
 * @author yxiao
 * @date
 * @description
 */
@Log4j2
@RestController
@RequestMapping("/app/user")
public class UserController {

    @Autowired
    private AdministratorRepository administratorRepository;
    //@Autowired
   // private LoadBalancerCacheAutoConfiguration.LoadBalancerCaffeineWarnLogger caffeineWarnLogger;


    @Value("${url.strapi}")
    private String strapiBaseUrl;

    @Resource
    private CommonRestfulApiUtils commonRestfulApiUtils;


    /**
     * 获取当前登录用户的基本信息：姓名、部门和操作权限
     *
     * @param principal
     * @return
     */
    @GetMapping("/principal")
    public RestResponse<?> getPrincipalV2(Principal principal) {
        try {
            if (principal instanceof OAuth2Authentication) {
                OAuth2Authentication authentication = (OAuth2Authentication) principal;
                OAuth2AuthenticationDetails oauth2Details = (OAuth2AuthenticationDetails) authentication.getDetails();
                Map<String, Object> decodedDetails = (Map<String, Object>) oauth2Details.getDecodedDetails();
                Map<String, Object> userDetails = new HashMap<>();
                Map<String, Object> personalInfo = new HashMap<>();
                personalInfo.put("username", principal.getName());
                List<String> roles = (List<String>) decodedDetails.get("roles");
                personalInfo.put("roles", String.join(",", roles));
                personalInfo.put("realName", decodedDetails.get("realName"));
                personalInfo.put("userType", decodedDetails.get("userType"));
                personalInfo.put("departmentName", decodedDetails.get("departmentName"));
                personalInfo.put("office", decodedDetails.getOrDefault("office", ""));

                List<Administrator> administratorList = administratorRepository.findAllByNumber(principal.getName());
                personalInfo.put("departmentName", administratorList.isEmpty() ? null: administratorList.get(0).getPerformanceDepartmentName());
                //根据roles获取用户的部门信息及在各个部门的角色列表
                Map<String, Map<String, Object>> departmentWithRoles = new HashMap<>();
                administratorList.forEach(departmentMember -> {
                    if (departmentWithRoles.containsKey(departmentMember.getDepartmentCode())) {
                        List<Map<String, String>> tempRoles = (List<Map<String, String>>) departmentWithRoles.get(departmentMember.getDepartmentCode()).get("roles");
                        if (!tempRoles.stream().anyMatch(t -> departmentMember.getRoles().equalsIgnoreCase(t.get("role")))) {
                            tempRoles.add(MapBuilder.begin("role", departmentMember.getRoles()).build());
                            departmentWithRoles.get(departmentMember.getDepartmentCode()).put("roles", tempRoles);
                        }
                    } else {
                        List<Map<String, String>> tempRoles = new ArrayList<>();
                        tempRoles.add(MapBuilder.begin("role", departmentMember.getRoles()).build());
                        Map<String, Object> singleDepartmentWithRoles = new HashMap<>();
                        singleDepartmentWithRoles.put("departmentCode", departmentMember.getDepartmentCode());
                        singleDepartmentWithRoles.put("departmentName", departmentMember.getDepartmentName());
                        singleDepartmentWithRoles.put("performanceDepartmentName", departmentMember.getPerformanceDepartmentName());
                        singleDepartmentWithRoles.put("buinCode", departmentMember.getBuinCode());
                        singleDepartmentWithRoles.put("roles", tempRoles);
                        departmentWithRoles.put(departmentMember.getDepartmentCode(), singleDepartmentWithRoles);
                    }
                });

//                //增加老龄办管理员角色适配
//                String office = personalInfo.get("office").toString();
//                if (StringUtils.isNotEmpty(office) && roles.contains(Constant.USER_ROLE..USER_ROLE.ROLE_XGB_SCA_ADMIN)) {
//                    Map<String, Object> singleDepartmentWithRoles = new HashMap<>();
//                    singleDepartmentWithRoles.put("departmentCode", office);
//                    singleDepartmentWithRoles.put("departmentName", office);
//                    singleDepartmentWithRoles.put("roles", Arrays.asList(MapBuilder.begin("role", FieldConst.USER_ROLE.ROLE_XGB_SCA_ADMIN).and("name", "综合素质档案老龄办管理员").build()));
//                    departmentWithRoles.put(office, singleDepartmentWithRoles);
//                    personalInfo.replace("departmentType", Constant.DepartmentType.SCHOOL);
//                }

                userDetails.put("departmentWithRoles", departmentWithRoles.values());
//                personalInfo.put("subDepartmentCode", decodedDetails.get("subDepartmentCode"));
//                personalInfo.put("subDepartmentName", decodedDetails.get("subDepartmentName"));
//                personalInfo.put("departmentName", decodedDetails.get("departmentName"));
//                personalInfo.put("departmentId", decodedDetails.get("departmentId"));
                userDetails.put("details", personalInfo);

                Collection<GrantedAuthority> grantedAuthorities = authentication.getAuthorities();
                Map<String, Map<String, String>> authorities = new HashMap<>();
                grantedAuthorities.stream().forEach(t -> {
                    String[] resourceWithPrivilege = t.getAuthority().split("-");
                    String resourceId = resourceWithPrivilege[0];
                    String privilege = resourceWithPrivilege[1];
                    if (authorities.containsKey(resourceWithPrivilege[0])) {
                        authorities.get(resourceId).put(privilege, "1");
                    } else {
                        Map<String, String> tempPermission = new HashMap<>();
                        tempPermission.put(privilege, "1");
                        authorities.put(resourceId, tempPermission);
                    }
                });
                userDetails.put("authorities", authorities);
                return RestResponse.good(userDetails);
            }
            return RestResponse.good(principal);
        } catch (Exception e) {
            log.error("获取用户信息失败：{}", e);
            return RestResponse.bad(-2, e.getMessage());
        }
    }

    /**
     * 新增多院系用户
     * @param body
     * @param principal
     * @return
     */
    @PostMapping("batch")
    public RestResponse<?> getManyUser(@RequestBody Map<String, Object> body, Principal principal) {
        if (body ==null || body.isEmpty()) {
            return RestResponse.bad(-1, "参数不能为空");
        }
        List<Map<String,Object>> administrators = (List<Map<String,Object>>)body.getOrDefault("administrators", new ArrayList<>());
        if (administrators.isEmpty()) {
            return RestResponse.bad(-1, "参数不能为空");
        }

//        String realName = principal.getName();
//        if (principal instanceof OAuth2Authentication) {
//            OAuth2Authentication authentication = (OAuth2Authentication) principal;
//            OAuth2AuthenticationDetails oauth2Details = (OAuth2AuthenticationDetails) authentication.getDetails();
//            Map<String, Object> decodedDetails = (Map<String, Object>) oauth2Details.getDecodedDetails();
//            realName = decodedDetails.get("realName").toString();
//        }

        //String finalRealName = realName;
        administrators.forEach(administrator ->{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", administrator.get("name"));
            jsonObject.put("password", administrator.get("password"));
            jsonObject.put("number", administrator.get("number"));
            jsonObject.put("roles", administrator.get("roles"));
            jsonObject.put("authenticationMethods", administrator.get("authenticationMethods"));
            jsonObject.put("departmentName", administrator.get("departmentName"));
            jsonObject.put("departmentCode", administrator.get("departmentCode"));
            jsonObject.put("performanceDepartmentName", administrator.get("performanceDepartmentName"));
            jsonObject.put("buinCode", administrator.get("buinCode"));
//            jsonObject.put("lastUpdaterNumber", principal.getName());
//            jsonObject.put("lastUpdaterName", finalRealName);
            HashMap<String, Object> map = new HashMap<>();
            map.put("data", jsonObject);
            try {
                Map<String, Object> resMap = commonRestfulApiUtils.callInterface(Constant.InterfaceType.POST, strapiBaseUrl + "/api/administrators", map, "");
                log.info("新增用户成功. name={}，password={}，department={},resMap={}" , administrator.get("name"), administrator.get("password"), administrator.get("departmentName")+"-"+administrator.get("departmentCode"), resMap);
            }catch (Exception e){
                log.error("新增用户失败. name={}，password={}，department={},e={}" , administrator.get("name"), administrator.get("password"), administrator.get("departmentName")+"-"+administrator.get("departmentCode"), e.getMessage());
            }
        });
        return RestResponse.good("新增用户成功");

    }
}
