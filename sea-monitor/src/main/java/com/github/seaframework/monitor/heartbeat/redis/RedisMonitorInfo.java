package com.github.seaframework.monitor.heartbeat.redis;

import lombok.Data;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/4/23
 * @since 1.0
 */
@Data
public class RedisMonitorInfo {
    // 实例名
    private String serviceName;

    // current active count
    private int activeCount;
    // idle count
    private int idleCount;
    // waiter count
    private int waiterCount;

    private int totalCount;
}
