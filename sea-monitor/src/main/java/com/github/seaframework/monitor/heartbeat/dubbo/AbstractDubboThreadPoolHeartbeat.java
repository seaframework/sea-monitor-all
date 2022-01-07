package com.github.seaframework.monitor.heartbeat.dubbo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.seaframework.core.util.*;
import com.github.seaframework.monitor.common.TagConst;
import com.github.seaframework.monitor.dto.MetricDTO;
import com.github.seaframework.monitor.heartbeat.AbstractCollector;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/4/3
 * @since 1.0
 */
@Slf4j
public abstract class AbstractDubboThreadPoolHeartbeat extends AbstractCollector {

    private static final double MAX_THRESHOLD_VALUE = 0.9;

    public abstract String getThreadPoolName();

    protected Map<String, Object> doStatics() {
        String threadPoolName = getThreadPoolName();
        if (StringUtil.isEmpty(threadPoolName)) {
            return MapUtil.empty();
        }

        try {
            Class<?> clazz = ClassUtil.load(getThreadPoolName());
            if (clazz == null) {
                return MapUtil.empty();
            }
            Method check = clazz.getMethod("check");
            Object invoke = check.invoke(clazz.newInstance());

            // parse message
            //{"level":"OK","message":"Pool status:OK, max:200, core:200, largest:27, active:0, task:27, service port: 20888"}
//            message=@String[Pool status:OK, max:200, core:200, largest:200, active:0, task:12961, service port: 20880;Pool status:OK, max:2147483647, core:0, largest:1, active:0, task:2824, service port: 7070],
            JSONObject jsonObj = JSON.parseObject(JSON.toJSONString(invoke));
            String level = jsonObj.getString("level");
            String msg = jsonObj.getString("message");

            if (StringUtil.isEmpty(level) || EqualUtil.isEq(level, "UNKNOWN", false) || StringUtil.isEmpty(msg)) {
                return MapUtil.empty();
            }

            List<String> values = StringUtil.splitToList(msg, ';');
            if (ListUtil.isEmpty(values)) {
                return MapUtil.empty();
            }

            List<MetricDTO> data = new ArrayList<>();
            for (int i = 0; i < values.size(); i++) {
                String message = values.get(i);
                List<String> items = StringUtil.splitToList(message, ',');
                if (ListUtil.isEmpty(items)) {
                    continue;
                }
                Map<String, String> valueMap = new HashMap<>(items.size());

                for (int j = 0; j < items.size(); j++) {
                    String item = items.get(j);
                    String[] valueArray = StringUtil.split(item, ':');
                    if (valueArray == null || valueArray.length != 2) {
                        continue;
                    }
                    valueMap.put(valueArray[0].trim(), valueArray[1].trim());
                }

                String port = MapUtil.getString(valueMap, "service port", "");
                if (StringUtil.isEmpty(port)) {
                    log.warn("service port is null, skip it");
                    continue;
                }
                Map<String, String> tags = new HashMap<>(1, 1);
                tags.put(TagConst.TAG1, port);
                // report
                double max = MapUtil.getDoubleValue(valueMap, "max", 0);
                double active = MapUtil.getDoubleValue(valueMap, "active", 0);
                double activePercent = NumberUtil.divide(active, max, 3, RoundingMode.UP).doubleValue();

                data.add(buildMetric("dubbo.thread.pool.max", max, tags));
                data.add(buildMetric("dubbo.thread.pool.core", MapUtil.getDoubleValue(valueMap, "core", 0), tags));
                data.add(buildMetric("dubbo.thread.pool.largest", MapUtil.getDoubleValue(valueMap, "largest", 0), tags));
                data.add(buildMetric("dubbo.thread.pool.active", active, tags));
                data.add(buildMetric("dubbo.thread.pool.task", MapUtil.getDoubleValue(valueMap, "task", 0), tags));
                data.add(buildMetric("dubbo.thread.pool.active.percent", activePercent, tags));

                if (max < Integer.MAX_VALUE) {
                    checkDumpStack(activePercent);
                }
            }

            Map<String, Object> retMap = new HashMap<>();
            retMap.put("data", data);

            return retMap;
        } catch (Exception e) {
            log.info("fail to report ", e);
        }

        return MapUtil.empty();
    }

    @Override
    public String getId() {
        return "dubbo.thread.pool";
    }

    @Override
    public Map<String, Object> getProperties() {
        return doStatics();
    }


    protected void checkDumpStack(double activePercent) {
        if (activePercent >= MAX_THRESHOLD_VALUE) {
            log.warn("begin dump java stack");
            Thread t = new Thread(() -> JvmUtil.dumpStackLimiter());
            t.start();
        }
    }
}
