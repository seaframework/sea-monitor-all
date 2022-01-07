package com.github.seaframework.monitor.plugin.dubbo;

import com.github.seaframework.monitor.SeaMonitor;
import com.github.seaframework.monitor.common.MonitorConst;
import com.github.seaframework.monitor.common.TagConst;
import com.github.seaframework.monitor.dto.MetricDTO;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/8/14
 * @since 1.0
 */
@Slf4j
public abstract class AbstractDubboExceptionMonitorFilter {
    // 10s
    private static final int MIN_COST_TIME = 1000 * 10;


    protected void postCheckCost(Stopwatch stopwatch, String service, String method) {
        if (stopwatch == null) {
            return;
        }
        if (!SeaMonitor.isEnabled()) {
            return;
        }
        long cost = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        if (cost >= MIN_COST_TIME) {
            log.warn("DUBBO502");
            MetricDTO metricDTO = new MetricDTO();
            metricDTO.setMetric(MonitorConst.METRIC_DUBBO_COST);
            metricDTO.setValue(cost);
            metricDTO.setTraceIdFlag(true);

            Map<String, String> tagsMap = new HashMap<>(2);
            tagsMap.put(TagConst.TAG1, service);
            tagsMap.put(TagConst.TAG2, method);
            metricDTO.setTagsMap(tagsMap);

            SeaMonitor.logMetric(metricDTO);
        }
    }
}
