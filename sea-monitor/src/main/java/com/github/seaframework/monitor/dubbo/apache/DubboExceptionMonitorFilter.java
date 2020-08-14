package com.github.seaframework.monitor.dubbo.apache;

import com.github.seaframework.monitor.SeaMonitor;
import com.github.seaframework.monitor.common.MonitorConst;
import com.github.seaframework.monitor.dto.MetricDTO;
import com.github.seaframework.monitor.dubbo.AbstractDubboExceptionMonitorFilter;
import com.github.seaframework.monitor.enums.CounterEnum;
import com.github.seaframework.monitor.heartbeat.data.DataStats;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.*;

import java.util.HashMap;
import java.util.Map;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/4/13
 * @since 1.0
 */
@Slf4j
public class DubboExceptionMonitorFilter extends AbstractDubboExceptionMonitorFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Stopwatch stopwatch = Stopwatch.createStarted();

        Throwable exception = null;
        Result result = null;
        try {
            result = invoker.invoke(invocation);
        } catch (RpcException e) {
            exception = e;
            logRPCException(invoker, invocation, e);
            throw e;
        } catch (Throwable t) {
            exception = t;
            throw new RpcException(t);
        } finally {
            if (result.hasException() || exception != null) {
                log.info("dubbo500");
                if (SeaMonitor.isEnabled()) {
                    MetricDTO metricDTO = new MetricDTO();
                    metricDTO.setMetric(MonitorConst.METRIC_DUBBO_EXCEPTION);
                    metricDTO.setValue(1);
                    Map<String, String> tags = new HashMap<>(2);
                    tags.put("service", invoker.getInterface().getName());
                    tags.put("method", invocation.getMethodName());
                    metricDTO.setTagsMap(tags);
                    metricDTO.setTraceIdFlag(true);

                    SeaMonitor.logMetric(metricDTO);

                    // for dubbo total
                    DataStats dataStats = DataStats.currentStatsHolder();
                    dataStats.logCount(CounterEnum.DUBBO_EXCEPTION_COUNT);
                    // for system total
                    dataStats.logCount(CounterEnum.SYS_ERROR);
                }
            }

            postCheckCost(stopwatch, invoker.getInterface().getName(), invocation.getMethodName());
        }

        return result;
    }


    private void logRPCException(Invoker<?> invoker, Invocation invocation, RpcException exception) {
        if (!SeaMonitor.isEnabled()) {
            return;
        }
        if (exception == null) {
            return;
        }
        log.warn("dubbo rpc exception", exception);

        try {
            DataStats dataStats = DataStats.currentStatsHolder();

            switch (exception.getCode()) {
                default:
                case com.alibaba.dubbo.rpc.RpcException.UNKNOWN_EXCEPTION:
                    dataStats.logCount(CounterEnum.DUBBO_EXCEPTION_UNKNOWN.getKey());
                    break;
                case com.alibaba.dubbo.rpc.RpcException.NETWORK_EXCEPTION:
                    dataStats.logCount(CounterEnum.DUBBO_EXCEPTION_NETWORK.getKey());
                    break;
                case com.alibaba.dubbo.rpc.RpcException.TIMEOUT_EXCEPTION:
                    dataStats.logCount(CounterEnum.DUBBO_EXCEPTION_TIMEOUT.getKey());
                    break;
                case com.alibaba.dubbo.rpc.RpcException.BIZ_EXCEPTION:
                    dataStats.logCount(CounterEnum.DUBBO_EXCEPTION_BIZ.getKey());
                    break;
                case com.alibaba.dubbo.rpc.RpcException.FORBIDDEN_EXCEPTION:
                    dataStats.logCount(CounterEnum.DUBBO_EXCEPTION_FORBIDDEN.getKey());
                    break;
                case com.alibaba.dubbo.rpc.RpcException.SERIALIZATION_EXCEPTION:
                    dataStats.logCount(CounterEnum.DUBBO_EXCEPTION_SERIALIZATION.getKey());
                    break;
            }
        } catch (Exception e) {
            log.error("fail to collector", e);
        }

    }
}
