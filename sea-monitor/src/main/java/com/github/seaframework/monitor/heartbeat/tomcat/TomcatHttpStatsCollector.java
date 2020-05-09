package com.github.seaframework.monitor.heartbeat.tomcat;

import com.github.seaframework.monitor.common.MonitorCommon;
import com.github.seaframework.monitor.common.TagConst;
import com.github.seaframework.monitor.dto.MetricDTO;
import com.github.seaframework.monitor.heartbeat.AbstractCollector;
import com.github.seaframework.monitor.util.JMXUtil;
import lombok.extern.slf4j.Slf4j;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * tomcat http stats collector
 *
 * @author spy
 * @version 1.0 2020/5/5
 * @since 1.0
 */
@Slf4j
public class TomcatHttpStatsCollector extends AbstractCollector {
    private MBeanServer mbeanServer;
    private ObjectName objectName;

    public TomcatHttpStatsCollector() {
        objectName = JMXUtil.createObjectName("*:type=GlobalRequestProcessor,*");
        mbeanServer = JMXUtil.getMBeanServer();
    }

    public static boolean exist() {
        return MonitorCommon.isTomcatContainer();
    }

    @Override
    public String getId() {
        return "tomcat.http";
    }

    @Override
    public Map<String, Object> getProperties() {
        if (log.isDebugEnabled()) {
            log.debug("collect tomcat http info");
        }
        // collect thread metrics
        Map<String, Object> map = new HashMap<>();
        ObjectName[] connectorNames = JMXUtil.getObjectNames(objectName);


        List<MetricDTO> metrics = new ArrayList<>();
        for (ObjectName connectorName : connectorNames) {
            String name = ObjectName.unquote(connectorName.getKeyProperty("name"));
            // 基本不使用的协议
            if (name.startsWith("ajp")) {
                continue;
            }

            Map<String, String> tags = new HashMap<>();
            tags.put(TagConst.SERVICE, name);

            try {
                int requestCount = (Integer) mbeanServer.getAttribute(connectorName, "requestCount");
                metrics.add(buildMetric("tomcat.http.request.count", requestCount, tags));

                int errorCount = (Integer) mbeanServer.getAttribute(connectorName, "errorCount");
                metrics.add(buildMetric("tomcat.http.error.count", errorCount, tags));

                long processingTime = (Long) mbeanServer.getAttribute(connectorName, "processingTime");
                metrics.add(buildMetric("tomcat.http.processing.time", processingTime, tags));

                long maxTime = (Long) mbeanServer.getAttribute(connectorName, "maxTime");
                metrics.add(buildMetric("tomcat.http.max.time", maxTime, tags));

                long bytesReceived = (Long) mbeanServer.getAttribute(connectorName, "bytesReceived");
                metrics.add(buildMetric("tomcat.http.bytes.received", bytesReceived, tags));

                long bytesSent = (Long) mbeanServer.getAttribute(connectorName, "bytesSent");
                metrics.add(buildMetric("tomcat.http.bytes.sent", bytesSent, tags));

            } catch (Exception e) {
                log.error("Exception occur when getting connector global stats: ", e);
            }

        }
        map.put("data", metrics);
        return map;
    }
}
