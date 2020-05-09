package com.github.seaframework.monitor.heartbeat.datasource.hikari;

import com.github.seaframework.core.util.ClassUtil;
import com.github.seaframework.core.util.NumberUtil;
import com.github.seaframework.monitor.common.TagConst;
import com.github.seaframework.monitor.dto.MetricDTO;
import com.github.seaframework.monitor.heartbeat.datasource.DataSourceCollector;
import lombok.extern.slf4j.Slf4j;

import javax.management.ObjectName;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/4/17
 * @since 1.0
 */
@Slf4j
public class HikariInfoCollector extends DataSourceCollector {
    private final static String PREFIX_KEY = "hikari";
    public static final String DATASOURCE_NAME = "com.zaxxer.hikari.HikariDataSource";

    public static boolean exist() {
        return ClassUtil.load(DATASOURCE_NAME) != null;
    }

    private Map<String, Object> doCollect() {
        Map<String, Object> map = new LinkedHashMap<>();
        Map<String, HikariMonitorInfo> monitorInfoMap = getMonitorInfoMap();
        List<MetricDTO> metrics = new ArrayList<>();

        for (Map.Entry<String, HikariMonitorInfo> entry : monitorInfoMap.entrySet()) {
            String dataSourceName = entry.getKey();
            HikariMonitorInfo value = entry.getValue();

            Map<String, String> tags = new HashMap<>();
            tags.put(TagConst.SERVICE, dataSourceName);
            tags.put(TagConst.DB_TYPE, PREFIX_KEY);

            metrics.add(buildMetric("database.thread.pool.busy", value.getActiveConnections(), tags));
            metrics.add(buildMetric("database.thread.pool.total", value.getTotalConnections(), tags));
            metrics.add(buildMetric("database.thread.pool.idle", value.getIdleConnections(), tags));
            metrics.add(buildMetric("database.thread.pool.waiting", value.getThreadsAwaitingConnection(), tags));
            metrics.add(buildMetric("database.thread.pool.busy.percent", NumberUtil.divide(value.getActiveConnections(), value.getMaximumPoolSize(), 3, RoundingMode.UP).doubleValue(), tags));
        }

        return map;
    }

    private HikariMonitorInfo getPoolInfo(ObjectName objectName) {
        HikariMonitorInfo monitorInfo = new HikariMonitorInfo();
        monitorInfo.setName(getMatchValue(objectName.getCanonicalName()));

        monitorInfo.setActiveConnections(getIntegerAttribute(objectName, "ActiveConnections", false));
        monitorInfo.setIdleConnections(getIntegerAttribute(objectName, "IdleConnections", false));
        monitorInfo.setThreadsAwaitingConnection(getIntegerAttribute(objectName, "ThreadsAwaitingConnection", false));
        monitorInfo.setTotalConnections(getIntegerAttribute(objectName, "TotalConnections", false));

        return monitorInfo;
    }

    private HikariMonitorInfo getPoolConfigInfo(ObjectName objectName, HikariMonitorInfo monitorInfo) {
        monitorInfo.setMaximumPoolSize(getIntegerAttribute(objectName, "MaximumPoolSize", false));
        return monitorInfo;
    }

    private Map<String, HikariMonitorInfo> getMonitorInfoMap() {
        Map<String, HikariMonitorInfo> dataSourceInfoMap = new LinkedHashMap<>();

        try {
            Hashtable<String, String> table = new Hashtable<>();
            table.put("type", "Pool (*");

            ObjectName poolObjectName = new ObjectName("com.zaxxer.hikari", table);
            Set<ObjectName> objectNameSet = mbeanServer.queryNames(poolObjectName, null);

            if (objectNameSet == null || objectNameSet.isEmpty()) {
                return dataSourceInfoMap;
            }

            for (ObjectName objectName : objectNameSet) {
                HikariMonitorInfo info = getPoolInfo(objectName);
                dataSourceInfoMap.put(info.getName(), info);
            }

            table = new Hashtable<>();
            table.put("type", "PoolConfig (*");

            ObjectName poolConfigObjectName = new ObjectName("com.zaxxer.hikari", table);
            Set<ObjectName> configObjectNameSet = mbeanServer.queryNames(poolConfigObjectName, null);

            if (configObjectNameSet == null || configObjectNameSet.isEmpty()) {
                return dataSourceInfoMap;
            }

            for (ObjectName objectName : configObjectNameSet) {
                String key = getMatchValue(objectName.getCanonicalName());

                if (dataSourceInfoMap.containsKey(key)) {
                    HikariMonitorInfo info = dataSourceInfoMap.get(key);
                    getPoolConfigInfo(objectName, info);
                }
            }
        } catch (Exception ignored) {
        }
        return dataSourceInfoMap;
    }

    @Override
    public String getId() {
        return "datasource.hikari";
    }

    @Override
    public Map<String, Object> getProperties() {
        return doCollect();
    }

    Pattern pattern = Pattern.compile("(?<=\\()(.+?)(?=\\))");

    private String getMatchValue(String name) {
        try {
            Matcher matcher = pattern.matcher(name);
            while (matcher.find()) {
                return matcher.group();
            }
        } catch (Exception e) {
            log.error("match error", e);
        }
        return name;
    }
}
