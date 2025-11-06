package com.cloume.ecnu.sei.app.factory;

import com.cloume.ecnu.sei.app.enums.FileTypeEnum;
import com.cloume.ecnu.sei.app.handler.FileTypeHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FileHandlerFactory implements InitializingBean {

    @Autowired
    private List<FileTypeHandler> fileTypeHandlerList;

    private final Map<FileTypeEnum, FileTypeHandler> handlerMap = new HashMap<>();

    public FileTypeHandler getHandler(Integer fileCode) {
        FileTypeEnum fileTypeEnum = FileTypeEnum.getByCode(fileCode);
        return handlerMap.get(fileTypeEnum);
    }

    @Override
    public void afterPropertiesSet() {
        fileTypeHandlerList.forEach(handler -> handlerMap.put(handler.getHandlerType(), handler));
    }
}
