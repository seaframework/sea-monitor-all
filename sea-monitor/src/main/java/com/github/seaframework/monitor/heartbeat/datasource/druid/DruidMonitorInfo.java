package com.github.seaframework.monitor.heartbeat.datasource.druid;

import lombok.Data;

@Data
public class DruidMonitorInfo {
    private String jdbcUrl;
    private int activeCount;
    private int poolingCount;
    private int maxActive;
    private int maxIdle;
    private int maxOpenPreparedStatements;
    private int maxPoolPreparedStatementPerConnectionSize;
    private long maxWait;
    private int maxWaitThreadCount;
    private int minIdle;
    private int queryTimeout;
    private int notEmptyWaitThreadCount;
    private int waitThreadCount;
    private long notEmptyWaitCount;
    private long notEmptyWaitMillis;
    private long startTransactionCount;
    private long recycleCount;
    private long rollbackCount;
    private long closeCount;
    private long closedPreparedStatementCount;
    private long commitCount;
    private long connectCount;
    private long connectErrorCount;
    private long createCount;
    private long createErrorCount;
    private long destroyCount;
    private long discardCount;
    private long dupCloseCount;
    private long errorCount;
    private long initialSize;
    private int lockQueueLength;
    private long createTimeSpanMillis;
    private long removeAbandonedCount;
}
