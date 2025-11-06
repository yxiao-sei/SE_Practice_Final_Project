package com.cloume.ecnu.sei.app.converter;

import com.cloume.ecnu.sei.app.utils.SchemaEnum;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class SchemaEnumConverter implements Converter<String, SchemaEnum> {
    @Override
    public SchemaEnum convert(@NotNull String s) {
        return SchemaEnum.valueOf(s.toUpperCase());
    }
}
