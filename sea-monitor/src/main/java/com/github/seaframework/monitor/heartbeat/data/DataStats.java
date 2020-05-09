package com.github.seaframework.monitor.heartbeat.data;

import com.github.seaframework.monitor.enums.CounterEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * common data collector for <font color='red'> system level, this is very important </font>
 *
 * @author spy
 * @version 1.0 2020/4/18
 * @since 1.0
 */
@Slf4j
public class DataStats {
    private static DataStats current = null;
    private Map<String, AtomicLong> cache = new ConcurrentHashMap<>();

    private DataStats() {
    }

    public static DataStats currentStatsHolder() {
        if (null == current) {
            synchronized (DataStats.class) {
                if (null == current) {
                    current = new DataStats();
                }
            }
        }
        return current;
    }

    public static synchronized DataStats getAndReset() {
        DataStats tmp = new DataStats();
        DataStats old = currentStatsHolder();
        current = tmp;
        return old;
    }


    public void logCount(String metric) {
        try {
            cache.putIfAbsent(metric, new AtomicLong());
            cache.get(metric).incrementAndGet();
        } catch (Exception e) {
            // ignore
        }
    }

    public void logCount(CounterEnum counterEnum) {
        if (counterEnum == null) {
            return;
        }
        logCount(counterEnum.getKey());
    }


    public Map<String, AtomicLong> getCache() {
        return cache;
    }
}
