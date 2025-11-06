package com.cloume.ecnu.authserver.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TimestampUtil {

    /**
     * 获取当前学年的字符串：2018-2019
     * @return
     */
    public static String getCurrentSchoolYear(Long timestamp) {
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy-MM");

        String currentTime;
        if (timestamp == null) {
            currentTime = yearFormat.format(new Date(System.currentTimeMillis()));
        } else {
            currentTime = yearFormat.format(new Date(timestamp));
        }
        long currentMonth = Long.parseLong(currentTime.split("-")[1]);
        long currentYear = Long.parseLong(currentTime.split("-")[0]);
        String schoolYear;
        if (currentMonth >= 9) {
            schoolYear = currentYear + "-" + (currentYear + 1);
        } else {
            schoolYear = (currentYear - 1) + "-" + currentYear;
        }

        return schoolYear;
    }

    /**
     * 根据时间获取上一学年
     * @return
     */
    public static String getLastSchoolYear(Long timestamp) {
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy-MM");

        String currentTime;
        if (timestamp == null) {
            currentTime = yearFormat.format(new Date(System.currentTimeMillis()));
        } else {
            currentTime = yearFormat.format(new Date(timestamp));
        }
        long currentMonth = Long.parseLong(currentTime.split("-")[1]);
        long currentYear = Long.parseLong(currentTime.split("-")[0]) - 1;
        String schoolYear;
        if (currentMonth >= 9) {
            schoolYear = currentYear + "-" + (currentYear + 1);
        } else {
            schoolYear = (currentYear - 1) + "-" + currentYear;
        }

        return schoolYear;
    }

    /**
     * 获取最小的时间戳区间
     * @param year 年份
     * @param schoolYear 学年
     * @param startMonth 学年开始月份
     * @param endMonth 学年结束月份
     * @param beginTime 开始时间戳
     * @param endTime 结束时间戳
     * @return
     */
    public static List<Long> getTimestampRegion(String year, String schoolYear, String startMonth, String endMonth, Long beginTime, Long endTime) {
        Long begin = 0L;
        Long end = Long.MAX_VALUE;

        Long beginTemp, endTemp;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            //"2018"
            if (StringUtils.isNotEmpty(year)) {
                Long yearBegin = Long.valueOf(year);
                SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
                String yearCount = yearFormat.format(new Date(yearBegin));
                beginTemp = simpleDateFormat.parse(yearCount + "-01-01 00:00:00").getTime();
                if (beginTemp > begin) {
                    begin = beginTemp;
                }

                endTemp = simpleDateFormat.parse(yearCount + "-12-31 23:59:59").getTime();
                if (endTemp < end) {
                    end = endTemp;
                }
            }

            //"2018-2019"
            if (StringUtils.isNotEmpty(schoolYear)) {
                String beginYear = schoolYear.split("-")[0];

                beginTemp = simpleDateFormat.parse(beginYear + "-" + startMonth + "-01 00:00:00").getTime();
                if (beginTemp > begin) {
                    begin = beginTemp;
                }

                endTemp = simpleDateFormat.parse((Integer.valueOf(beginYear) + 1) + "-" + endMonth + "-30 23:59:59").getTime();
                if (endTemp < end) {
                    end = endTemp;
                }
            }

            if (beginTime != null && beginTime > begin) {
                begin = beginTime;
            }

            if (endTime != null && endTime < end) {
                end = endTime;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return Arrays.asList(begin, end);
    }
}
