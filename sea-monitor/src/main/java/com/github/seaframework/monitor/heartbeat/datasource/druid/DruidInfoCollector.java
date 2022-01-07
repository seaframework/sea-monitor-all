package com.github.seaframework.monitor.heartbeat.datasource.druid;

import com.github.seaframework.core.util.ClassUtil;
import com.github.seaframework.core.util.NumberUtil;
import com.github.seaframework.monitor.common.TagConst;
import com.github.seaframework.monitor.dto.MetricDTO;
import com.github.seaframework.monitor.heartbeat.datasource.DataSourceCollector;
import com.github.seaframework.monitor.heartbeat.datasource.DatabaseParserHelper;

import javax.management.ObjectName;
import java.math.RoundingMode;
import java.util.*;

public class DruidInfoCollector extends DataSourceCollector {

    private final static String PREFIX_KEY = "druid";

    public static final String DATASOURCE_NAME = "com.alibaba.druid.pool.DruidDataSource";

    public static boolean exist() {
        return ClassUtil.load(DATASOURCE_NAME) != null;
    }

    private Map<String, Object> doCollect() {
        Map<String, DruidMonitorInfo> druidMonitorInfoMap = getDruidMonitorInfoMap();
        Map<String, Object> map = new HashMap<>();

        List<MetricDTO> metrics = new ArrayList<>();
        for (Map.Entry<String, DruidMonitorInfo> entry : druidMonitorInfoMap.entrySet()) {
            String dataSourceName = entry.getKey();
            DruidMonitorInfo value = entry.getValue();

            Map<String, String> tags = new HashMap<>();
            tags.put(TagConst.TAG1, PREFIX_KEY);
            tags.put(TagConst.TAG2, dataSourceName);

            metrics.add(buildMetric("database.thread.pool.busy", value.getActiveCount(), tags));
            metrics.add(buildMetric("database.thread.pool.total", value.getPoolingCount(), tags));
            metrics.add(buildMetric("database.thread.pool.idle", value.getPoolingCount() - value.getActiveCount(), tags));
            metrics.add(buildMetric("database.thread.pool.busy.percent", NumberUtil.divide(value.getActiveCount(), value.getMaxActive(), 3, RoundingMode.UP).doubleValue(), tags));
            metrics.add(buildMetric("database.thread.pool.connect_error_count", value.getConnectErrorCount(), tags));
            metrics.add(buildMetric("database.thread.pool.create_error_count", value.getCreateErrorCount(), tags));
            metrics.add(buildMetric("database.thread.pool.error_count", value.getErrorCount(), tags));
        }

        map.put("data", metrics);
        return map;
    }

    private DruidMonitorInfo getDruidMonitorInfo(ObjectName objectName) {
        DruidMonitorInfo druidMonitorInfo = new DruidMonitorInfo();
        String jdbcUrl = getStringAttribute(objectName, "Url");

        druidMonitorInfo.setJdbcUrl(jdbcUrl);
        druidMonitorInfo.setActiveCount(getIntegerAttribute(objectName, "ActiveCount", false));
        druidMonitorInfo.setPoolingCount(getIntegerAttribute(objectName, "PoolingCount", false));
        druidMonitorInfo.setMaxActive(getIntegerAttribute(objectName, "MaxActive", false));

        druidMonitorInfo.setConnectErrorCount(getLongAttribute(objectName, "ConnectErrorCount", true));
        druidMonitorInfo.setCreateErrorCount(getLongAttribute(objectName, "CreateErrorCount", true));
        druidMonitorInfo.setErrorCount(getLongAttribute(objectName, "ErrorCount", true));

        return druidMonitorInfo;
    }

    private Map<String, DruidMonitorInfo> getDruidMonitorInfoMap() {
        Map<String, DruidMonitorInfo> dataSourceInfoMap = new HashMap<String, DruidMonitorInfo>();
        try {
            Hashtable<String, String> table = new Hashtable<String, String>();

            table.put("type", "DruidDataSource");
            table.put("id", "*");

            ObjectName pooledDataSourceObjectName = new ObjectName("com.alibaba.druid", table);
            Set<ObjectName> objectNameSet = mbeanServer.queryNames(pooledDataSourceObjectName, null);

            if (objectNameSet == null || objectNameSet.isEmpty()) {
                return dataSourceInfoMap;
            }

            Map<String, Integer> datasources = new LinkedHashMap<String, Integer>();

            for (ObjectName objectName : objectNameSet) {
                DruidMonitorInfo info = getDruidMonitorInfo(objectName);
                String url = info.getJdbcUrl();
                DatabaseParserHelper.Database datasource = databaseParser.parseDatabase(url);
                String key = getConnection(datasources, datasource.toString());

                dataSourceInfoMap.put(key, info);
            }
        } catch (Exception e) {
            // ignore
        }
        return dataSourceInfoMap;
    }

    @Override
    public String getId() {
        return "datasource.druid";
    }

    @Override
    public Map<String, Object> getProperties() {
        return doCollect();
    }

}
