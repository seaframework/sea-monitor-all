package com.github.seaframework.monitor.common;

import lombok.extern.slf4j.Slf4j;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/3/24
 * @since 1.0
 */
@Slf4j
public class MonitorConst {

    public static final String MONITOR_CONFIG_FILE = "sea.monitor.properties";

    public static final String CONFIG_KEY_REGION = "sea.region";
    //是否启用监控
    public static final String CONFIG_KEY_ENABLED = "sea.monitor.enabled";
    // 当前系统名称,当前环境唯一，后期会演化成机器唯一标识，启动后不再变化
    public static final String CONFIG_KEY_ENDPOINT = "sea.monitor.endpoint";
    // 当前系统名称
    public static final String CONFIG_KEY_APP_NAME = "sea.monitor.application.name";
    // monitor sdk
    // @See monitor mode enum
    public static final String CONFIG_KEY_MODE = "sea.monitor.mode";

    public static final String CONFIG_KEY_TRACE = "sea.monitor.trace";

    // 请求地址
    public static final String CONFIG_KEY_URI = "sea.monitor.uri";
    // 采样频率
    public static final String CONFIG_KEY_SAMPLE_PERCENT = "sea.monitor.sample.percent";
    // 消费者个数
    public static final String CONFIG_KEY_CONSUMER_COUNT = "sea.monitor.consumer.count";
    // 发送最大元素个数
    public static final String CONFIG_KEY_SEND_ELEMENT_MAX_COUNT = "sea.monitor.send.element.max.count";
    // 发送间隔
    public static final String CONFIG_KEY_SEND_PERIOD_TIME = "sea.monitor.send.period.time";

    public static final String HTTP = "http";
    public static final String DUBBO = "dubbo";
    public static final String MQ = "mq";

    public static final String METRIC_EXCEPTION = "exception";

    // 监控指标 http异常
    public static final String METRIC_HTTP_REQUEST_TIME = "http.request.cost.time";
    // 监控指标 - 请求异常
    public static final String METRIC_HTTP_ERROR = "http.request.error";
    //监控指标 - 定时任务异常
    public static final String METRIC_TASK_ERROR = "task.error";


    public static final String METRIC_SYS_REBOOT = "sea.sys.reboot";
    public static final String METRIC_SYS_ALIVE = "sea.sys.alive";
    public static final String METRIC_SYS_ERROR = "sea.sys.error.count";

    public static final String METRIC_DUBBO_EXCEPTION = "dubbo.exception";
    public static final String METRIC_DUBBO_COST = "dubbo.cost";

    public static final String METRIC_MQ_ERROR = "mq.error";

    // redis
    public static final String METRIC_REDIS_ACTIVE_COUNT = "redis.pool.active.count";
    public static final String METRIC_REDIS_IDLE_COUNT = "redis.pool.idle.count";
    public static final String METRIC_REDIS_WAITER_COUNT = "redis.pool.waiter.count";
    public static final String METRIC_REDIS_TOTAL_COUNT = "redis.pool.total.count";
    public static final String METRIC_REDIS_ACTIVE_PERCENT = "redis.pool.active.percent";

    // db
    public static final String METRIC_DB_SQL_ERROR = "db.sql.error";
    public static final String METRIC_DB_SQL_LARGE_RECORD_ERROR = "db.sql.large.record.error";
    public static final String METRIC_DB_SQL_ERROR_COUNT = "db.sql.error.count";
    public static final String METRIC_DB_SQL_COST = "db.sql.cost";


    //


    // 默认收集地址
    public static final String DEFAULT_COLLECTOR_URI = "http://127.0.0.1:2058/api/collector/push";
    // 处理能力
    public static final int DEFAULT_CONSUMER_COUNT = 1;
    // 发送元素最大个数
    public static final int DEFAULT_SEND_ELEMENT_MAX_COUNT = 200;
    // 默认采样率
    public static final int DEFAULT_SAMPLE_PERCENT = 100;
    /**
     * 定时推送监控信息到agent的间隔时间,单位:秒，默认20
     */
    public static final int DEFAULT_PUSH_PERIOD_TIME = 20;

}
