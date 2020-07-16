package com.github.seaframework.monitor.heartbeat.tomcat;

import com.github.seaframework.core.util.EqualUtil;
import com.github.seaframework.core.util.JvmUtil;
import com.github.seaframework.core.util.NumberUtil;
import com.github.seaframework.core.util.StringUtil;
import com.github.seaframework.monitor.common.MonitorCommon;
import com.github.seaframework.monitor.common.TagConst;
import com.github.seaframework.monitor.dto.MetricDTO;
import com.github.seaframework.monitor.heartbeat.AbstractCollector;
import com.github.seaframework.monitor.util.JMXUtil;
import lombok.extern.slf4j.Slf4j;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * tomcat thread pool stats collector
 *
 * @author spy
 * @version 1.0 2020/5/3
 * @since 1.0
 */
@Slf4j
public class TomcatThreadPoolStatsCollector extends AbstractCollector {
    private MBeanServer mbeanServer;
    private ObjectName objectName;

    private static final String[] metricNames = {
            "busy_count",
            "total_count",
            "min_pool_size",
            "max_pool_size",
            "thread_pool_queue_size"
    };

    public TomcatThreadPoolStatsCollector() {
        objectName = JMXUtil.createObjectName("*:type=ThreadPool,*");
        mbeanServer = JMXUtil.getMBeanServer();
    }

    public static boolean exist() {
        return MonitorCommon.isTomcatContainer();
    }


    @Override
    public String getId() {
        return "tomcat.stats";
    }

    @Override
    public Map<String, Object> getProperties() {
        if (log.isDebugEnabled()) {
            log.debug("collect tomcat stats info");
        }
        // collect thread metrics
        Map<String, Object> map = new HashMap<>();
        ObjectName[] connectorNames = JMXUtil.getObjectNames(objectName);

        List<MetricDTO> metrics = new ArrayList<>();
        for (ObjectName connectorName : connectorNames) {
            // only check one
            String subType = connectorName.getKeyProperty("subType");
            if (StringUtil.isNotEmpty(subType)) {
                continue;
            }
            if (!isTomcatServer(connectorName.getDomain())) {
                continue;
            }

            String name = ObjectName.unquote(connectorName.getKeyProperty("name"));
            // 基本不使用的协议
            if (name.startsWith("ajp")) {
                continue;
            }

            Map<String, String> tags = new HashMap<>();
            tags.put(TagConst.SERVICE, name);
            try {
                int currentThreadsBusy = (Integer) mbeanServer.getAttribute(connectorName, "currentThreadsBusy");
                metrics.add(buildMetric("tomcat.thread.pool.busy", currentThreadsBusy, tags));

                int currentThreadCount = (Integer) mbeanServer.getAttribute(connectorName, "currentThreadCount");
                metrics.add(buildMetric("tomcat.thread.pool.current", currentThreadCount, tags));

                int minSpareThreads = (Integer) mbeanServer.getAttribute(connectorName, "minSpareThreads");
                metrics.add(buildMetric("tomcat.thread.pool.min.spare", minSpareThreads, tags));

                int maxThreads = (Integer) mbeanServer.getAttribute(connectorName, "maxThreads");
                metrics.add(buildMetric("tomcat.thread.pool.max", maxThreads, tags));

                double busyPercent = NumberUtil.divide(currentThreadsBusy, maxThreads, 3, RoundingMode.UP).doubleValue();
                metrics.add(buildMetric("tomcat.thread.pool.busy.percent", busyPercent, tags));
                // should be only one stat object there
                checkDumpStack(busyPercent);

            } catch (Exception e) {
                log.error("Exception occur when getting connector global stats: ", e);
            }

            // cannot visit for now
//            try {
//                int queueSize = (Integer) mbeanServer.getAttribute(connectorName, "threadPoolTaskQueueSize");
//                metrics.add(buildMetric("tomcat.thread.pool.queue", queueSize, tags));
//            } catch (Exception e) {
//                log.error("Exception occur when getting connector global stats: ", e);
//            }

        }
        map.put("data", metrics);
        return map;
    }

    private boolean isTomcatServer(String domain) {
        if (StringUtil.isEmpty(domain)) {
            return false;
        }

        return EqualUtil.isEq("tomcat", domain, false) ||
                EqualUtil.isEq("catalina", domain, false);
    }

    private static final double MAX_THRESHOLD_VALUE = 0.9;

    private void checkDumpStack(double activePercent) {
        if (activePercent >= MAX_THRESHOLD_VALUE) {
            log.warn("begin dump java stack");
            Thread t = new Thread(() -> JvmUtil.dumpStackLimiter());
            t.start();
        }
    }
}
