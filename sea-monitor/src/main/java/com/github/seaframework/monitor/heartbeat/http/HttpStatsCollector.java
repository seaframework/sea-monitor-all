package com.github.seaframework.monitor.heartbeat.http;

import com.github.seaframework.monitor.heartbeat.AbstractCollector;

import java.util.HashMap;
import java.util.Map;

/**
 * 应该没有使用
 *
 * @author spy
 * @version 1.0
 */
public class HttpStatsCollector extends AbstractCollector {

    private Map<String, Object> doClassLoadingCollect() {
        Map<String, Object> map = new HashMap<>(4);
        HttpStats stats = HttpStats.getAndReset();

        map.put("http.count", stats.getHttpCount());
        map.put("http.meantime", stats.getHttpMeantime());
        map.put("http.status400.count", stats.getHttpStatus400Count());
        map.put("http.status500.count", stats.getHttpStatus500Count());
        return map;
    }

    @Override
    public String getId() {
        return "http.status";
    }

    @Override
    public Map<String, Object> getProperties() {
        return doClassLoadingCollect();
    }

}
