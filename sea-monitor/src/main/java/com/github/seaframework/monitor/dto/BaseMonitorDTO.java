package com.github.seaframework.monitor.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.github.seaframework.core.util.MapUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/3/28
 * @since 1.0
 */
@Data
public class BaseMonitorDTO implements Serializable {
    private String metric;
    private String endpoint;
    private int step;
    private long timestamp;
    private Map<String, String> tagsMap;

    private String extra;

    @JSONField(serialize = false)
    private Map<String, String> extraMap;

    public String getExtra() {
        return MapUtil.toString(extraMap);
    }

}
