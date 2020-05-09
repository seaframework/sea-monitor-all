package com.github.seaframework.monitor.heartbeat.data;

import com.github.seaframework.monitor.common.MonitorCommon;
import com.github.seaframework.monitor.enums.CounterEnum;
import com.github.seaframework.monitor.heartbeat.AbstractCollector;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/4/18
 * @since 1.0
 */
@Slf4j
public class DataStatsCollector extends AbstractCollector {
    @Override
    public String getId() {
        return "sea.data.stats.collector";
    }

    @Override
    public Map<String, Object> getProperties() {

        Map<String, Object> map = new HashMap<>();

        DataStats stats = DataStats.getAndReset();

        stats.getCache()
             .entrySet()
             .stream()
             .parallel()
             .forEach(item -> {
                 map.put(item.getKey(), item.getValue().get());
             });

        // check and compensate dubbo
        if (MonitorCommon.hasDubbo()) {
            compensateMetric(map, CounterEnum.dubboMetricList());
        }

        // compensate
        compensateMetric(map, CounterEnum.baseMetricList());

        return map;
    }

    private void compensateMetric(Map<String, Object> map, CounterEnum[] counters) {
        for (int i = 0; i < counters.length; i++) {
            map.putIfAbsent(counters[i].getKey(), 0);
        }
    }
}
