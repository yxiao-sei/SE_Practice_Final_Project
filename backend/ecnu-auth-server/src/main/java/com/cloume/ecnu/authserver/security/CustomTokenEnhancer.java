package com.cloume.ecnu.authserver.security;

import com.cloume.ecnu.authserver.model.CustomUserDetails;
import com.cloume.ecnu.authserver.repository.DefaultCustomUserDetailsRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomTokenEnhancer implements TokenEnhancer {

    @Autowired
    private DefaultCustomUserDetailsRepository userRepository;

    @SuppressWarnings("unchecked")
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		try {
            final Map<String, Object> additionalUserInfo = new HashMap<>();
            Map<String, Object> userDetails = (HashMap<String, Object>) authentication.getUserAuthentication().getDetails();

            // 下面的代码是为了给微信小程序返回用户信息
            CustomUserDetails customUserDetails = userRepository.findTopByUsernameAndIsRemovedFalse(authentication.getName());
            if (StringUtils.isNotEmpty(customUserDetails.getRoles())) {
                additionalUserInfo.put("roles", StringUtils.isNotEmpty(customUserDetails.getRoles()) ? Arrays.asList(customUserDetails.getRoles().split(",")) : null);
            }
            if (StringUtils.isNotEmpty(customUserDetails.getUserType())) {
                additionalUserInfo.put("userType", customUserDetails.getUserType());
            }
            if (StringUtils.isNotEmpty(customUserDetails.getRealName())) {
                additionalUserInfo.put("realName", customUserDetails.getRealName());
            }
            if (StringUtils.isNotEmpty(customUserDetails.getOffice())) {
                additionalUserInfo.put("office", customUserDetails.getOffice());
            }
            if (StringUtils.isNotEmpty(customUserDetails.getDepartmentId())) {
                additionalUserInfo.put("departmentId", customUserDetails.getDepartmentId());
            }
            if (StringUtils.isNotEmpty(customUserDetails.getSubDepartmentCode())) {
                additionalUserInfo.put("subDepartmentCode", customUserDetails.getSubDepartmentCode());
            }
            if (StringUtils.isNotEmpty(customUserDetails.getDepartmentName())) {
                additionalUserInfo.put("departmentName", customUserDetails.getDepartmentName());
            }
            if (StringUtils.isNotEmpty(customUserDetails.getSubDepartmentName())) {
                additionalUserInfo.put("subDepartmentName", customUserDetails.getSubDepartmentName());
            }

            List<String> roles = additionalUserInfo.get("roles") == null ? null : (List<String>)additionalUserInfo.get("roles");
            String username = userDetails.get("username").toString();
//            String entityDepartmentCode = "";
//            String entityDepartmentName = "";
            //TODO: 设置当前登录账号所在院系部门
//            additionalUserInfo.put("departmentCode", entityDepartmentCode);
//            additionalUserInfo.put("departmentName", entityDepartmentName);

            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalUserInfo);

            return accessToken;
        } catch (Exception e) {
		    return accessToken;
        }

	}

}
