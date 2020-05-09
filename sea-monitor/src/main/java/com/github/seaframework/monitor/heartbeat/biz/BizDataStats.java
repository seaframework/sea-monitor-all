package com.github.seaframework.monitor.heartbeat.biz;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * biz common data collector.
 *
 * @author spy
 * @version 1.0 2020/4/18
 * @since 1.0
 */
@Slf4j
public class BizDataStats {
    private static BizDataStats current = null;
    private Map<String, AtomicLong> cache = new ConcurrentHashMap<>();

    private BizDataStats() {
    }

    public static BizDataStats currentStatsHolder() {
        if (null == current) {
            synchronized (BizDataStats.class) {
                if (null == current) {
                    current = new BizDataStats();
                }
            }
        }
        return current;
    }

    public static synchronized BizDataStats getAndReset() {
        BizDataStats tmp = new BizDataStats();
        BizDataStats old = currentStatsHolder();
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

    public Map<String, AtomicLong> getCache() {
        return cache;
    }
}
