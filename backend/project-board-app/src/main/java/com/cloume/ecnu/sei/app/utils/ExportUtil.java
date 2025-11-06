package com.cloume.ecnu.sei.app.utils;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExportUtil {

    public static List<String> mapToList(List<Map<String, Object>> mapList) {
        return mapList.stream()
                .flatMap(map -> map.values().stream())
                .map(value -> value != null ? value.toString() : "")
                .collect(Collectors.toList());
    }

    public static Integer indexOfSortList(Map<String, Object> map, List<String> sortList) {
        String title = map.values().stream().findFirst().orElse("").toString();
        return sortList.indexOf(title);
    }
}
