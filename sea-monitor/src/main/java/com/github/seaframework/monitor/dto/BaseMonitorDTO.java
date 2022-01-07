package com.github.seaframework.monitor.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.github.seaframework.core.util.MapUtil;
import com.github.seaframework.core.util.StringUtil;
import com.github.seaframework.monitor.common.MonitorCommon;
import com.github.seaframework.monitor.common.Version;
import com.github.seaframework.monitor.util.ExceptionResolve;
import lombok.Data;
import org.slf4j.helpers.MessageFormatter;

import java.io.Serializable;
import java.util.HashMap;
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
    @JSONField(name = "tags")
    protected Map<String, String> tagsMap;

    @JSONField(serialize = false)
    protected String extra;

    @JSONField(name = "extra")
    protected Map<String, String> extraMap;

    /**
     * 异常内容
     */
    @JSONField(serialize = false)
    protected String problem;

    /**
     * 解决方案
     */
    @JSONField(serialize = false)
    protected String solution;

    /**
     * 备注
     */
    @JSONField(serialize = false)
    protected String remark;

    /**
     * 设置异常
     *
     * @param t
     */
    public void setException(Throwable t) {
        this.problem = ExceptionResolve.getSimpleMsg(t);
    }

    /**
     * overwrite default method.
     *
     * @return
     */
    public String getExtra() {
        checkExtraMap();
        extraMap.put("version", Version.getVersion());
        extraMap.put("uptime", MonitorCommon.getUptime());

        if (StringUtil.isNotEmpty(problem)) {
            extraMap.put("problem", problem);
        }
        if (StringUtil.isNotEmpty(solution)) {
            extraMap.put("solution", solution);
        }
        if (StringUtil.isNotEmpty(remark)) {
            extraMap.put("remark", remark);
        }

        return JSON.toJSONString(extraMap);
        //return MapUtil.toString(extraMap);
    }

    private void checkExtraMap() {
        if (MapUtil.isEmpty(extraMap)) {
            extraMap = new HashMap<>();
        }
    }

    /**
     * add tag.
     *
     * @param key
     * @param value
     */
    public void addTag(String key, String value) {
        if (this.tagsMap == null) {
            this.tagsMap = new HashMap<>();
        }
        this.tagsMap.put(key, value);
    }

    /**
     * 格式化输出文案
     *
     * @param messagePattern
     * @param args
     */
    public void setRemarkF(String messagePattern, Object... args) {
        this.remark = MessageFormatter.arrayFormat(messagePattern, args).getMessage();
    }

    /**
     * 格式化输出文案
     *
     * @param messagePattern
     * @param args
     */
    public void setSolutionF(String messagePattern, Object... args) {
        this.solution = MessageFormatter.arrayFormat(messagePattern, args).getMessage();
    }

}
