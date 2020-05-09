package com.github.seaframework.monitor.enums;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/4/18
 * @since 1.0
 */
@Slf4j
public enum CounterEnum {
    // sys
    SYS_ERROR("sea.sys.error.count"),

    // query per second
    SYS_QPS_COUNT("sea.sys.qps.count"),
    SYS_QPS_HTTP_COUNT("sea.sys.qps.http.count"),
    SYS_QPS_DUBBO_COUNT("sea.sys.qps.dubbo.count"),


    // query per minute
    SYS_QPM_COUNT("sea.sys.qpm.count"),
    SYS_QPM_HTTP_COUNT("sea.sys.qpm.http.count"),
    SYS_QPM_DUBBO_COUNT("sea.sys.qpm.dubbo.count"),

    //log for log4j or logback
    SYS_LOG_COUNT("sea.sys.log.error.count"),

    // HTTP
    HTTP500("http.500.count"),

    //DB
    DB_SQL_ERROR_COUNT("db.sql.error.count"),

    // DUBBO
    DUBBO_EXCEPTION_COUNT("dubbo.exception.count"),
    DUBBO_EXCEPTION_UNKNOWN("dubbo.exception.unknown.count"),
    DUBBO_EXCEPTION_BIZ("dubbo.exception.biz.count"),
    DUBBO_EXCEPTION_FORBIDDEN("dubbo.exception.forbidden.count"),
    DUBBO_EXCEPTION_TIMEOUT("dubbo.exception.timeout.count"),
    DUBBO_EXCEPTION_NETWORK("dubbo.exception.network.count"),
    DUBBO_EXCEPTION_SERIALIZATION("dubbo.exception.serialization.count");

    @Getter
    @Setter
    private String key;

    CounterEnum(String key) {
        this.key = key;
    }

    public static CounterEnum[] dubboMetricList() {
        CounterEnum[] counters = new CounterEnum[]{
                DUBBO_EXCEPTION_UNKNOWN,
                DUBBO_EXCEPTION_BIZ,
                DUBBO_EXCEPTION_FORBIDDEN,
                DUBBO_EXCEPTION_TIMEOUT,
                DUBBO_EXCEPTION_NETWORK,
                DUBBO_EXCEPTION_SERIALIZATION,
        };
        return counters;
    }

    public static CounterEnum[] baseMetricList() {
        CounterEnum[] counters = new CounterEnum[]{
                SYS_ERROR,
                HTTP500,
                DB_SQL_ERROR_COUNT,
                SYS_LOG_COUNT
        };
        return counters;
    }

}
