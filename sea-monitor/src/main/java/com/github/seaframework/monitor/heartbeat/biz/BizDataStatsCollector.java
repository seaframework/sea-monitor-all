package com.github.seaframework.monitor.heartbeat.biz;

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
public abstract class BizDataStatsCollector extends AbstractCollector {
    @Override
    public String getId() {
        return "sea.biz.data.stats.collector";
    }

    @Override
    public Map<String, Object> getProperties() {

        Map<String, Object> map = new HashMap<>();

        BizDataStats stats = BizDataStats.getAndReset();

        stats.getCache()
             .entrySet()
             .stream()
             .parallel()
             .forEach(item -> {
                 map.put(item.getKey(), item.getValue().get());
             });

        // compensate
        metricCompensate(map);

        return map;
    }

    /**
     * 指标补偿
     *
     * @param map
     */
    protected abstract void metricCompensate(Map<String, Object> map);
}
