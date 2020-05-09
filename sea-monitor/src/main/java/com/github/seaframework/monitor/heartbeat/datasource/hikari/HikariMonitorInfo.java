
package com.github.seaframework.monitor.heartbeat.datasource.hikari;

import lombok.Data;

@Data
public class HikariMonitorInfo {
    private String name;

    private int activeConnections;
    private int idleConnections;
    private int threadsAwaitingConnection;
    private int totalConnections;

    // pool config
    private int minimumIdle;
    private int maximumPoolSize;

}
