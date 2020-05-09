package com.github.seaframework.monitor.heartbeat.jvm;


import com.github.seaframework.monitor.heartbeat.AbstractCollector;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClassLoadingInfoCollector extends AbstractCollector {

    private Map<String, Object> doClassLoadingCollect() {
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("jvm.classloading.loaded.count", classLoadingMXBean.getLoadedClassCount());
        map.put("jvm.classloading.totalloaded.count", classLoadingMXBean.getTotalLoadedClassCount());
        map.put("jvm.classloading.unloaded.count", classLoadingMXBean.getUnloadedClassCount());

        return map;
    }

    @Override
    public String getId() {
        return "jvm.classloading";
    }

    @Override
    public Map<String, Object> getProperties() {
        return doClassLoadingCollect();
    }

}
