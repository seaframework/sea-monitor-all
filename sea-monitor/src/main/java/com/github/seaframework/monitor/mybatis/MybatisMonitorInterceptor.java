package com.github.seaframework.monitor.mybatis;

import com.github.seaframework.monitor.SeaMonitor;
import com.github.seaframework.monitor.common.MonitorConst;
import com.github.seaframework.monitor.dto.MetricDTO;
import com.github.seaframework.monitor.enums.CounterEnum;
import com.github.seaframework.monitor.heartbeat.data.DataStats;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/4/27
 * @since 1.0
 */
@Slf4j
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
public class MybatisMonitorInterceptor implements Interceptor {

    private static final int MAX_RECORD_SIZE = 5000;
    //10s
    private static final int MIN_COST_TIME = 1000 * 5;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("mybatis monitor interceptor begin.");
        }
        long start = System.currentTimeMillis();
        Object returnValue;
        try {
            returnValue = invocation.proceed();
            dealReturnValue(returnValue);
        } catch (Exception e) {
            if (SeaMonitor.isEnabled()) {
                log.error("DB500");
                MetricDTO metricDTO = new MetricDTO();
                metricDTO.setMetric(MonitorConst.METRIC_DB_SQL_ERROR);
                metricDTO.setValue(1);
                metricDTO.setErrorFlag(true);
                metricDTO.setTraceIdFlag(true);
                SeaMonitor.logMetric(metricDTO);

                // for sql total
                DataStats dataStats = DataStats.currentStatsHolder();
                dataStats.logCount(CounterEnum.DB_SQL_ERROR_COUNT);
            }
            throw e;
        } finally {
            postCheck(start);
        }

        if (log.isDebugEnabled()) {
            log.debug("mybatis monitor interceptor end.");
        }

        return returnValue;
    }

    private void postCheck(long start) {
        if (!SeaMonitor.isEnabled()) {
            return;
        }
        long cost = System.currentTimeMillis() - start;
        if (cost > MIN_COST_TIME) {
            log.warn("DB502");
            MetricDTO metricDTO = new MetricDTO();
            metricDTO.setMetric(MonitorConst.METRIC_DB_SQL_COST);
            metricDTO.setValue(cost);
            metricDTO.setErrorFlag(true);
            metricDTO.setTraceIdFlag(true);
            SeaMonitor.logMetric(metricDTO);
        }
    }

    private void dealReturnValue(Object returnValue) {
        if (!SeaMonitor.isEnabled()) {
            return;
        }
        if (returnValue == null) {
            return;
        }

        if (returnValue instanceof ArrayList) {
            List data = (ArrayList) returnValue;
            if (data.size() >= MAX_RECORD_SIZE) {
                log.error("DB501");
                MetricDTO metricDTO = new MetricDTO();
                metricDTO.setMetric(MonitorConst.METRIC_DB_SQL_LARGE_RECORD_ERROR);
                metricDTO.setValue(1);
                metricDTO.setErrorFlag(true);
                metricDTO.setTraceIdFlag(true);
                SeaMonitor.logMetric(metricDTO);
            }
        }

    }

    @Override
    public Object plugin(Object target) {

        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
