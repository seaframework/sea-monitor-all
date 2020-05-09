package com.github.seaframework.monitor.heartbeat.redis;

import com.github.seaframework.core.util.MapUtil;
import com.github.seaframework.core.util.NumberUtil;
import com.github.seaframework.monitor.common.MonitorConst;
import com.github.seaframework.monitor.heartbeat.AbstractCollector;
import lombok.extern.slf4j.Slf4j;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/4/23
 * @since 1.0
 */
@Slf4j
public abstract class AbstractRedisStatsCollector extends AbstractCollector {
    @Override
    public String getId() {
        return "sea.redis.stats.collector";
    }

    public abstract RedisMonitorInfo getRedisMonitorInfo();

    // for single
    @Override
    public Map<String, Object> getProperties() {

        RedisMonitorInfo redisMonitorInfo = getRedisMonitorInfo();

        if (redisMonitorInfo == null) {
            return MapUtil.empty();
        }
        Map<String, Object> map = new HashMap<>();

        map.put(MonitorConst.METRIC_REDIS_ACTIVE_COUNT, redisMonitorInfo.getActiveCount());
        map.put(MonitorConst.METRIC_REDIS_IDLE_COUNT, redisMonitorInfo.getIdleCount());
        map.put(MonitorConst.METRIC_REDIS_WAITER_COUNT, redisMonitorInfo.getWaiterCount());
        map.put(MonitorConst.METRIC_REDIS_TOTAL_COUNT, redisMonitorInfo.getTotalCount());
        map.put(MonitorConst.METRIC_REDIS_ACTIVE_PERCENT, NumberUtil.divide(redisMonitorInfo.getActiveCount(), redisMonitorInfo.getTotalCount(), 3, RoundingMode.UP));

        return map;
    }
}
