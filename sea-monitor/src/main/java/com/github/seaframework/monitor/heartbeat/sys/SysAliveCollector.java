package com.github.seaframework.monitor.heartbeat.sys;

import com.github.seaframework.monitor.common.MonitorConst;
import com.github.seaframework.monitor.heartbeat.AbstractCollector;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/4/12
 * @since 1.0
 */
@Slf4j
public class SysAliveCollector extends AbstractCollector {
    @Override
    public String getId() {
        return "sea.sys.alive";
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> map = new HashMap<>(1);
        map.put(MonitorConst.METRIC_SYS_ALIVE, 1);
        return map;
    }
}
