package com.cloume.ecnu.authserver.utils;

import com.cloume.ecnu.authserver.model.BaseResource;
import lombok.Data;

/**
 * @author bitxu
 * @Description TODO
 * @date 2020/6/16-14:54
 */
@Data
public class TimeDuration extends BaseResource {
    /**
     * 开始时间
     */
    private Long startTime = 0L;

    /**
     * 结束时间
     */
    private Long endTime = 0L;
}
