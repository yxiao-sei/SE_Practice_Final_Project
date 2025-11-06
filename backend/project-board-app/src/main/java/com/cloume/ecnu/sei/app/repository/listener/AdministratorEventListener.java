package com.cloume.ecnu.sei.app.repository.listener;

import com.alibaba.fastjson.JSONObject;
import com.cloume.ecnu.sei.app.model.Administrator;
import com.cloume.ecnu.sei.app.config.MQMessageSender;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AdministratorEventListener extends AbstractRepositoryEventListener<Administrator> {

    @Autowired
    private MQMessageSender mqMessageSender;

    @Override
    public void onAfterCreate(Administrator administrator) {
        log.info(administrator);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", administrator.getNumber());
        jsonObject.put("name", administrator.getName());
        jsonObject.put("roles", administrator.getRoles());
        jsonObject.put("departmentName", administrator.getDepartmentName());
        jsonObject.put("departmentCode", administrator.getDepartmentCode());
//        jsonObject.put("authenticationMethods", administrator.getAuthenticationMethods());
        jsonObject.put("authenticationMethods", "USERNAME_PASSWORD");
        jsonObject.put("creator", administrator.getCreator());
        jsonObject.put("password", "173afad5992a3f73a472fc09b05b1fb7");
        mqMessageSender.send(jsonObject.toJSONString(), MQMessageSender.TOPIC_NEW_USER);
    }
}
