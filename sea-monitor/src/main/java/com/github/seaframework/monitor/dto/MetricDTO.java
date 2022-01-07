package com.github.seaframework.monitor.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/3/24
 * @since 1.0
 */
@Data
@ToString(callSuper = true)
public class MetricDTO extends BaseMonitorDTO {

    /**
     * 指标值
     */
    private double value;

    /**
     * 是否是错误标志，如果是错误类型，会进行sys.error++
     */
    @JSONField(serialize = false)
    private boolean errorFlag;

    /**
     * 是否添加traceId
     */
    @JSONField(serialize = false)
    private boolean traceIdFlag;


    /**
     * 是否周期性指标
     */
    @JSONField(serialize = false)
    private boolean periodFlag;
}
