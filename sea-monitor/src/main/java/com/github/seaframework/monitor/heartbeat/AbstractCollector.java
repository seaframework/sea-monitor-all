package com.github.seaframework.monitor.heartbeat;

import com.github.seaframework.core.config.Configuration;
import com.github.seaframework.core.config.ConfigurationFactory;
import com.github.seaframework.monitor.common.MonitorConst;
import com.github.seaframework.monitor.dto.MetricDTO;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractCollector implements StatusExtension {

    protected Map<String, String> convert(Map<String, Number> map) {
        Map<String, String> result = new LinkedHashMap<>();

        for (Entry<String, Number> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue().toString());
        }
        return result;
    }

    @Override
    public String getDescription() {
        return getId();
    }

    protected String getAppName() {
        Configuration cfg = ConfigurationFactory.getInstance();
        return cfg.getString(MonitorConst.CONFIG_KEY_APP_NAME, "unkonw-app-anme");
    }

    protected MetricDTO buildMetric(String metric, double value, Map<String, String> tags) {
        MetricDTO metricDTO = new MetricDTO();
        metricDTO.setMetric(metric);
        metricDTO.setValue(value);
        metricDTO.setTagsMap(tags);
        return metricDTO;
    }
}
